package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.crm.sfa.predefine.service.model.LeadsObjTransferModel;
import com.facishare.crm.sfa.utilities.common.convert.ConvertorFactory;
import com.facishare.crm.sfa.utilities.proxy.LeadsObjTransferProxy;
import com.facishare.crm.sfa.utilities.proxy.model.LeadsObjTransferResult;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.fcp.util.StopWatch;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.dto.RuleResult;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.IRule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 线索一转三
 */
@ServiceModule("leadsobj_transfer")
@Component
@Slf4j
public class LeadsObjTransferService {
    @Autowired
    LeadsObjTransferProxy leadsObjTransferProxy;
    @Autowired
    private ServiceFacade serviceFacade;

    /**
     * 终端/WEB端线索转换接口
     */
    @ServiceMethod("transfer")
    public LeadsObjTransferModel.Result transfer(ServiceContext context, LeadsObjTransferModel.Arg arg) {
        log.info("LeadsObjTransferService>transfer()arg=" + JsonUtil.toJsonWithNullValues(arg));
        StopWatch stopWatch = StopWatch.create("LeadsObjTransferService transfer");
        String salesClueID = arg.getSalesClueID();
        if(salesClueID == null || salesClueID.length() == 0)
            throw new ValidateException("线索转换线索对象不能为空");
    
        Map<String, ObjectDataDocument> dataList = arg.getDataList();
        Map<String, ObjectDataDocument> transferDataList = Maps.newHashMap();
        if(dataList.size() == 0)
            throw new ValidateException("线索转换对象不能为空");
    
        String apiName = SFAPreDefineObject.Account.getApiName();
        ObjectDataDocument originalData = dataList.get(apiName);
        if(originalData == null || originalData.size() == 0)
            throw new ValidateException("线索转换客户对象不能为空");

        IObjectData objectData = originalData.toObjectData();
        if(objectData == null){
            throw new ValidateException("线索转换客户对象不能为空");
        }
       String customerId = objectData.getId();
       if(StringUtils.isEmpty(customerId)) {
           validateValidationRules(context, apiName, objectData, Maps.newHashMap(), IRule.CREATE);
       }

        String dataJson = ConvertorFactory.convertToOldFieldNamesString(apiName, JsonUtil.toJsonWithNullValues(originalData));
        dataJson = ConvertorFactory.specialFieldConvert(apiName, dataJson);
        Gson gson = new GsonBuilder().create();
        transferDataList.put(apiName, gson.fromJson(dataJson, ObjectDataDocument.class));
    
        apiName = SFAPreDefineObject.Contact.getApiName();
        originalData = dataList.get(apiName);
        if(originalData != null && originalData.size() > 0)
        {
            validateValidationRules(context, apiName, originalData.toObjectData(), Maps.newHashMap(), IRule.CREATE);
            dataJson = ConvertorFactory.convertToOldFieldNamesString(apiName, JsonUtil.toJsonWithNullValues(originalData));
            dataJson = ConvertorFactory.specialFieldConvert(apiName, dataJson);
            transferDataList.put(apiName, gson.fromJson(dataJson, ObjectDataDocument.class));
        }
        apiName = SFAPreDefineObject.Opportunity.getApiName();
        originalData = dataList.get(apiName);
        if(originalData != null && originalData.size() > 0)
        {
            validateValidationRules(context, apiName, originalData.toObjectData(), Maps.newHashMap(), IRule.CREATE);
            dataJson = ConvertorFactory.convertToOldFieldNamesString(apiName, JsonUtil.toJsonWithNullValues(originalData));
            dataJson = ConvertorFactory.specialFieldConvert(apiName, dataJson);
            transferDataList.put(apiName, gson.fromJson(dataJson, ObjectDataDocument.class));
        }
        log.info("LeadsObjTransferService>transfer()DataJson=" + JsonUtil.toJsonWithNullValues(transferDataList));
    
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("x-fs-ei", context.getTenantId());
        headers.put("x-fs-userInfo", context.getUser().getUserId());
    
        log.info("LeadsObjTransferService>transfer() calling LeadsObjTransferProxy");
        LeadsObjTransferResult.Arg transferArg = new  LeadsObjTransferResult.Arg();
        transferArg.setSalesClueID(arg.getSalesClueID());
        transferArg.setCombineCRMFeed(arg.isCombineCRMFeed());
        transferArg.setPutTeamMembersIntoCustomer(arg.isPutTeamMembersIntoCustomer());
        transferArg.setDataList(transferDataList);
        dataJson = JsonUtil.toJsonWithNullValues(transferArg);
        log.info("LeadsObjTransferService>transfer() call LeadsObjTransferProxy arg: " + dataJson);
        LeadsObjTransferResult.Result transferResult = leadsObjTransferProxy.transfer(transferArg, headers);
        log.info("LeadsObjTransferService>transfer() call LeadsObjTransferProxy finished");
        stopWatch.lap("LeadsObjTransferService transfer finished");
        stopWatch.logSlow(5000L);

        if(transferResult.getErrorCode() > 0){
            log.info("LeadsObjTransferService>transfer() transfer error: " +  transferResult.getErrorCode() + transferResult.getMessage());
            throw new ValidateException("线索转换发生错误:" + transferResult.getMessage());
        }

        LeadsObjTransferModel.Result  result = new LeadsObjTransferModel.Result();
        if(transferResult.getValue() != null){
            result.setCustomerDuplicate(transferResult.getValue().isCustomerDuplicate());
            result.setContactDuplicate(transferResult.getValue().isContactDuplicate());
            result.setOpportunityDuplicate(transferResult.getValue().isOpportunityDuplicate());
            String contactId = transferResult.getValue().getContactID();
            if (!ObjectUtils.isEmpty(contactId)) {
                objectData = serviceFacade.findObjectData(context.getUser(), contactId, SFAPreDefineObject.Contact.getApiName());
                result.setContactData(ObjectDataDocument.of(ObjectDataExt.of(objectData).toMap()));
            }
        }

        return result;
    }

    private void validateValidationRules(ServiceContext context, String apiName, IObjectData objectData, Map<String, List<IObjectData>> details, String ruleOperation) {
        List<String> apiNames = Lists.newArrayList();
        apiNames.add(apiName);
        Map<String, IObjectDescribe> objectDescribes = this.serviceFacade.findObjects(context.getTenantId(), apiNames);
        RuleResult ruleResult = this.serviceFacade.validateRule(context.getUser(), ruleOperation, objectDescribes, objectData, details);
        if (ruleResult.isMatch()) {
            throw new ValidateException(ruleResult.getFailMessage());
        }
    }
}
