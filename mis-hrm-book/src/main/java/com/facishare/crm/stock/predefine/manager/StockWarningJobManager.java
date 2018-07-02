package com.facishare.crm.stock.predefine.manager;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.SetRemindRecordModel;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.restful.client.FRestApiProxyFactory;
import com.facishare.restful.client.exception.FRestClientException;
import com.fxiaoke.api.JobAdminApi;
import com.fxiaoke.api.ReturnT;
import com.fxiaoke.model.AddJobInfo;
import com.fxiaoke.model.DeleteJobInfo;
import com.github.autoconf.ConfigFactory;
import com.github.autoconf.api.IConfig;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linchf
 * @date 2018/3/26
 */
@Service
@Slf4j(topic = "stockAccess")
public class StockWarningJobManager {

    @Resource
    private CrmRestApi crmRestApi;


    private JobAdminApi jobAdminApi;
    private String authorId;
    private String author;
    private String rightsGroup;
    private String jobDesc;
    private String cron;
    private String executorParam;

    private String workflowCronExecutor;
    private String workflowCronHandler;
    private String baseUrl;


    private final String CHECK_STOCK_WARNING_PATH = "stock/service/check_stock_warning";

    private final String STOCK_WARNING_SESSION_KEY = "459";
    @PostConstruct
    void init() throws Exception {
        jobAdminApi = FRestApiProxyFactory.getInstance().create(JobAdminApi.class);
        ConfigFactory.getConfig("fs-crm-stock", this::reloadConfig);
    }

    private void reloadConfig(IConfig config) {
        jobDesc = config.get("job.desc");

        author = config.get("author");
        authorId = config.get("author.id");
        rightsGroup = config.get("rights.group");

        workflowCronExecutor = config.get("executor");
        workflowCronHandler = config.get("handler");

        cron = config.get("cron");
        baseUrl = config.get("paas_framework_url");
    }

    public int addJob(String tenantId) {

        Map<String, String> headBodyMap = new HashMap<>();
        Map<String, String> requestMap = new HashMap<>();

        headBodyMap.put("x-fs-ei", tenantId);
        headBodyMap.put("x-fs-info", "1000");

        requestMap.put("tenantId", tenantId);

        ExecutorParam param = new ExecutorParam();
        param.setUrl(baseUrl + CHECK_STOCK_WARNING_PATH);
        param.setMethod("post");
        param.setHeadBody(JSON.toJSONString(headBodyMap));
        param.setRequestJsonBody(JSON.toJSONString(requestMap));

        executorParam = new Gson().toJson(param);

        String tenantJobDesc = jobDesc + "_" + tenantId + "_" + StockUtils.getUUID();
        AddJobInfo.Arg jobAddArg = AddJobInfo.Arg.getCronJob(tenantJobDesc, workflowCronExecutor, cron, workflowCronHandler, executorParam, author, authorId, rightsGroup);
        try {
            ReturnT<AddJobInfo.Result> jobInfoResult = jobAdminApi.addJob(jobAddArg);
            if (jobInfoResult.getCode() == ReturnT.SUCCESS_CODE) {
                AddJobInfo.Result addResult = jobInfoResult.getContent();
                return addResult.getId();
            } else {
                log.warn("stock add job failed. arg[{}], result[{}]", jobAddArg, jobInfoResult);
            }

        } catch (Exception e) {
            log.warn("stock add job failed.", e);
        }
        return -1;
    }

    public void deleteJob(int jobId) {
        DeleteJobInfo.Arg arg = DeleteJobInfo.Arg.getCronJobArg(jobId, authorId);
        try {
            jobAdminApi.deleteJob(arg);
        } catch (FRestClientException e) {
            log.warn("stock delete job failed. arg[{}]", arg);
        }
    }

    public void setRecordRemind(User user, Integer notReadCount, List<String> targetIds) {
        List<SetRemindRecordModel.Item> items = Lists.newArrayList();

        targetIds.forEach(targetId -> {
            SetRemindRecordModel.Item item = new SetRemindRecordModel.Item();
            item.setEmployeeId(Integer.valueOf(targetId));
            item.setLastSummary("");
            item.setLastTime(0);
            item.setNotReadCount(notReadCount);
            item.setRemindCount(notReadCount);
            item.setShouldSendPush(true);
            items.add(item);
         });

         if (!CollectionUtils.isEmpty(items)) {
             SetRemindRecordModel.Item[] itemsArray = new SetRemindRecordModel.Item[items.size()];
             SetRemindRecordModel.Result result = crmRestApi.setRemindRecord(STOCK_WARNING_SESSION_KEY, items.toArray(itemsArray), StockUtils.getHeaders(user.getTenantId(), user.getUserId()));
         }

    }

    @Data
    class ExecutorParam {
        String url;
        String method;
        String headBody;
        String requestJsonBody;
    }
}
