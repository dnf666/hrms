package com.facishare.crm.sfa.predefine.service;

import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.google.common.collect.Lists;

import com.facishare.crm.sfa.predefine.service.model.PriceBookInitResult;
import com.facishare.crm.userdefobj.DefObjConstants;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.RecordTypeLogicServiceImpl;
import com.facishare.paas.appframework.privilege.DataPrivilegeService;
import com.facishare.paas.appframework.privilege.dto.ObjectDataPermissionInfo;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@ServiceModule("pricebook_init")
@Component
@Slf4j
public class PriceBookInitService {

    @Autowired
    private RecordTypeLogicServiceImpl recordTypeLogicService;
    @Autowired
    private DataPrivilegeService dataPrivilegeService;
    @Autowired
    private IObjectDescribeService objectDescribeService;

    @ServiceMethod("init")
    public PriceBookInitResult.Result priceBookInit(PriceBookInitResult.Arg arg, ServiceContext context) {
        log.info("arg:{},context{", arg, context);
        String tenantIds = arg.getTenantId();
        String[] eis = tenantIds.split(",");
        StringBuilder sb = new StringBuilder();
        for (String tenantId : eis) {
            String userId = arg.getUserId();
            List<String> describeApiNames = arg.getDescribeApiNames();
            if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(userId) || describeApiNames.isEmpty()) {
                log.error("accountId is blank,tenantId {}", context.getTenantId());
                return PriceBookInitResult.Result.builder().rtnMsg("wrong param").build();
            }
            User user = new User(tenantId, userId);
            for (String descApiName : describeApiNames) {
                try {
                    recordTypeLogicService.recordTypeInit(user, null, tenantId, descApiName);
                    sb.append("||Success: Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" recordTypeInit |||");
                } catch (Exception e) {
                    log.error("recordTypeLogicService.recordTypeInit error", e);
                    sb.append("Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" recordTypeInit failed|||");
                }
                IObjectDescribe describe = null;
                try {
                    describe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, descApiName);
                } catch (MetadataServiceException e) {
                    log.error("Exception:", e);
                    sb.append("Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" findByTenantIdAndDescribeApiName failed|||");
                }
                // 数据权限初始化
                try {
                    if ("PriceBookObj".equals(descApiName)) {
                        dataPrivilegeService.addCommonPrivilegeListResult(user, Lists.newArrayList(
                                new ObjectDataPermissionInfo(descApiName, describe.getDisplayName(),
                                        DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PRIVATE.getValue())));
                        sb.append("||Success: Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" addCommonPrivilegeListResult |||");
                    }
                } catch (Exception e) {
                    log.error("dataPrivilegeService.addCommonPrivilegeListResult error", e);
                    sb.append("Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" addCommonPrivilegeListResult failed|||");
                }
            }
        }
        log.info("priceBookInit executed:", sb.toString());
        return PriceBookInitResult.Result.builder().rtnMsg(sb.toString()).build();
    }
}
