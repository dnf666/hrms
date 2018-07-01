package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.service.ServicePersonnelSearchSettingProxy;
import com.facishare.crm.service.dto.ServicePersonnelSearchSettingModel;
import com.facishare.crm.sfa.predefine.service.model.CasesPredefinedRefObjSearchModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2018/3/27.
 */
@Slf4j
@ServiceModule("cases_refobj_search")
@Component
public class CasesPredefinedRefObjSearchServiceImpl implements CasesPredefinedRefObjSearchService {
    @Autowired
    private ServicePersonnelSearchSettingProxy proxy;
    @Autowired
    private PredefinedObjSearchServiceManger predefinedObjSearchServiceManger;


    @Override
    @ServiceMethod("search")
    public CasesPredefinedRefObjSearchModel.Result search(ServiceContext context, CasesPredefinedRefObjSearchModel.Arg arg) {
        String apiName = arg.getApiName();

        String name = StringEscapeUtils.escapeSql(arg.getName());
        String accountId = StringEscapeUtils.escapeSql(arg.getAccountId());

        if (StringUtils.isBlank(name)) {
            return new CasesPredefinedRefObjSearchModel.Result(Lists.newArrayListWithCapacity(0));
        }

        boolean isFuzzySearch = isFuzzySearch(context.getTenantId(), context.getUser().getUserId());

        PredefinedObjSearchService predefinedObjSearchService = predefinedObjSearchServiceManger.getSearchService(apiName);

        List<IObjectData> objectDataList = predefinedObjSearchService.getObjectDataList(context.getTenantId(), context.getUser().getUserId(), name, accountId, isFuzzySearch);

        if (CollectionUtils.isEmpty(objectDataList)) {
            return new CasesPredefinedRefObjSearchModel.Result(Lists.newArrayListWithCapacity(0));
        } else {
            List<Map<String, Map<String, String>>> resultList = Lists.newArrayList();
            for (IObjectData objectData : objectDataList) {

                if (Utils.ACCOUNT_API_NAME.equals(apiName)) {
                    Map<String, Map<String, String>> resultMap = Maps.newHashMapWithExpectedSize(1);

                    Map<String, String> accountInfo = Maps.newHashMapWithExpectedSize(2);
                    accountInfo.put("_id", objectData.get("_id", String.class));
                    accountInfo.put("name", objectData.get("name", String.class));
                    resultMap.put(Utils.ACCOUNT_API_NAME, accountInfo);
                    resultList.add(resultMap);
                } else {
                    Map<String, Map<String, String>> resultMap = Maps.newHashMapWithExpectedSize(2);

                    if (StringUtils.isBlank(accountId)) {
                        Map<String, String> accountInfo = Maps.newHashMapWithExpectedSize(2);
                        accountInfo.put("_id", objectData.get("account_id", String.class));
                        accountInfo.put("name", objectData.get("account_id__r", String.class));
                        resultMap.put(Utils.ACCOUNT_API_NAME, accountInfo);
                    }

                    Map<String, String> objectInfo = Maps.newHashMapWithExpectedSize(2);
                    objectInfo.put("_id", objectData.get("_id", String.class));
                    objectInfo.put("name", objectData.get("name", String.class));

                    resultMap.put(arg.getApiName(), objectInfo);
                    resultList.add(resultMap);
                }
            }
            return new CasesPredefinedRefObjSearchModel.Result(resultList);
        }
    }


    /**
     * 获取企业是否是模糊查询,调用失败 默认返回位模糊查询
     *
     * @param tenantId
     * @param userId
     * @return
     */
    private boolean isFuzzySearch(String tenantId, String userId) {
        ServicePersonnelSearchSettingModel.Result result;
        try {
            result = proxy.getSearchSettingInfo(tenantId, userId);
        } catch (Throwable e) {
            log.error("invoke sfa error.", e);
            return Boolean.TRUE;
        }
        return result == null || !result.getSuccess() || !ServicePersonnelSearchSettingModel.ACCURATE_SEARCH.equals(result.getValue());
    }


}
