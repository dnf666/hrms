package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;

import com.facishare.crm.sfa.predefine.service.model.PredObjInitResult;
import com.facishare.crm.sfa.predefine.service.model.PriceBookInitResult;
import com.facishare.crm.userdefobj.DefObjConstants;
import com.facishare.crm.util.GsonUtil;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.service.ObjectValueMappingService;
import com.facishare.paas.appframework.core.predef.service.dto.objectMapping.CreateRule;
import com.facishare.paas.appframework.metadata.RecordTypeLogicServiceImpl;
import com.facishare.paas.appframework.privilege.DataPrivilegeService;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.appframework.privilege.dto.ObjectDataPermissionInfo;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.ILayoutService;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.ErrorCode;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.exception.MetadataValidateException;
import com.facishare.paas.metadata.impl.describe.ObjectDescribe;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.ILayout;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.facishare.crm.privilege.util.Constant.SYSTEM_OPT_USER_ID;
import static com.facishare.paas.metadata.api.describe.IFieldDescribe.DEFINE_TYPE_CUSTOM;

@ServiceModule("enterprise_init")
@Component
@Slf4j
public class EnterpriseInitService {

    @Autowired
    private RecordTypeLogicServiceImpl recordTypeLogicService;
    @Autowired
    private DataPrivilegeService dataPrivilegeService;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private FunctionPrivilegeService functionPrivilegeService;
    @Autowired
    ILayoutService layoutService;
    @Autowired
    ObjectValueMappingService objectValueMappingService;

    @ServiceMethod("initProductPkg")
    public PriceBookInitResult.Result initProductPkg(PriceBookInitResult.Arg arg, ServiceContext context) {
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
                // 功能权限初始化
                functionPrivilegeService.initFunctionPrivilege(user, descApiName);
                try {
                    //record type 初始化
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
                    if ("ProductPackageObj".equals(descApiName)) {
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
        log.info("initProductPkg executed:", sb.toString());
        return PriceBookInitResult.Result.builder().rtnMsg(sb.toString()).build();
    }

    @ServiceMethod("commonInitAll")
    public PriceBookInitResult.Result commonInitAll(PriceBookInitResult.Arg arg, ServiceContext context) {
        log.info("arg:{},context{", arg, context);
        String tenantIds = arg.getTenantId();
        String[] eis = tenantIds.split(",");
        StringBuilder sb = new StringBuilder();
        for (String tenantId : eis) {
            User user = new User(tenantId, arg.getUserId());
            String msg = this.initPrivilegeRelate(arg.getDescribeApiNames(), user);
            sb.append(msg);
        }
        log.info("commonInitAll executed:", sb.toString());
        return PriceBookInitResult.Result.builder().rtnMsg(sb.toString()).build();
    }

    public String initPrivilegeRelate(List<String> describeApiNames, User user) {
        StringBuilder sb = new StringBuilder();
        String tenantId = user.getTenantId();
        for (String descApiName : describeApiNames) {
            // 功能权限初始化
            try {
                functionPrivilegeService.initFunctionPrivilege(user, descApiName);
            } catch (Exception e) {
                log.error("initFunctionPrivilege failed:", e);
            }
            try {
                //record type 初始化
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
            try {
                dataPrivilegeService.addCommonPrivilegeListResult(user, Lists.newArrayList(
                        new ObjectDataPermissionInfo(descApiName, describe.getDisplayName(),
                                DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PRIVATE.getValue())));
                sb.append("||Success: Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" addCommonPrivilegeListResult |||");
            } catch (Exception e) {
                log.error("dataPrivilegeService.addCommonPrivilegeListResult error", e);
                sb.append("Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" addCommonPrivilegeListResult failed|||");
            }
        }
        log.info("initPrivilegeRelate executed:", sb.toString());
        return sb.toString();
    }

    @ServiceMethod("pred_obj_init")
    public PredObjInitResult.Result predObjInit(PredObjInitResult.Arg arg, ServiceContext context) {
        log.info("predObjInit init arg:{},context{", arg, context);
        String tenantIds = arg.getTenantId();
        List<String> operations = arg.getOperations();
        String[] eis = tenantIds.split(",");
        StringBuilder sb = new StringBuilder();
        for (String tenantId : eis) {
            List<String> describeApiNames = arg.getDescribeApiNames();
            User user = new User(tenantId, "-10000");
            for (String descApiName : describeApiNames) {
                // 功能权限初始化
                if (operations.contains("funcPrivilegeInit")) {
                    try {
                        functionPrivilegeService.initFunctionPrivilege(user, descApiName);
                        log.info("initFunctionPrivilege executed:", sb.toString());
                        sb.append("Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" initFunctionPrivilege success!!");
                    } catch (Exception e) {
                        log.error("initFunctionPrivilege failed:", e);
                        sb.append("Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" initFunctionPrivilege failed|||");
                    }
                }

                // 数据权限初始化
                if (operations.contains("dataPrivilegeInit")) {
                    IObjectDescribe describe = null;
                    try {
                        describe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, descApiName);
                    } catch (MetadataServiceException e) {
                        log.error("Exception:", e);
                        sb.append("Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" findByTenantIdAndDescribeApiName failed|||");
                    }
                    try {
                        dataPrivilegeService.addCommonPrivilegeListResult(user, Lists.newArrayList(
                                new ObjectDataPermissionInfo(descApiName, describe.getDisplayName(),
                                        DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PRIVATE.getValue())));
                        sb.append("||Success: Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" addCommonPrivilegeListResult |||");
                    } catch (Exception e) {
                        log.error("dataPrivilegeService.addCommonPrivilegeListResult error", e);
                        sb.append("Tenant:" + tenantId).append(",descApiName:" + descApiName).append(" addCommonPrivilegeListResult failed|||");
                    }
                }
            }
        }
        return PredObjInitResult.Result.builder().rtnMsg(sb.toString()).build();
    }

    public void initDescribeForTenant(String tenantId, String describeApiName) throws MetadataServiceException {
        ObjectDescribe describe = getDescribeFromLocalResource(describeApiName);
        describe.setTenantId(tenantId);

        ObjectDescribe describeOld = (ObjectDescribe) objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, describeApiName);
        if (updateIfExists(tenantId, describeApiName, describe, describeOld)) return;
        describe.setCreatedBy(SYSTEM_OPT_USER_ID);
        describe.setLastModifiedBy(SYSTEM_OPT_USER_ID);
        objectDescribeService.create(describe, true, false);
        log.info("objectDescribeDraftService.create executed! tenantId:{},apiName:{}", tenantId, describeApiName);
    }

    public String initMultiLayoutForOneTenant(List<String> descApiNames, String tenantId) {
        StringBuilder msgForOneTenant = new StringBuilder();
        for (String apiName : descApiNames) {
            try {
                String detailLayoutStr = getLayoutJsonFromResourceByApiName(apiName, "detail");
                this.initLayoutByJson(tenantId, apiName, detailLayoutStr);
                String listLayoutStr = getLayoutJsonFromResourceByApiName(apiName, "list");
                this.initLayoutByJson(tenantId, apiName, listLayoutStr);
                msgForOneTenant.append(",init layout:'").append(apiName).append("' succeed");
            } catch (Exception e) {
                msgForOneTenant.append(",init:'").append(apiName).append("' failed:").append(e);
                log.error("init initMultiLayoutForOneTenant error:", e);
            }
        }
        return msgForOneTenant.toString();
    }

    //初始化映射规则
    public void initObjectMappingRule(User user, String ruleApiName) {
        String jsonRule = loadJsonFromResource(ruleApiName);
        RequestContext requestContext = RequestContext.builder().tenantId(user.getTenantId()).user(
                Optional.of(user)).build();
        ServiceContext context = new ServiceContext(requestContext, null, null);

        CreateRule.Arg arg = GsonUtil.json2object(jsonRule, CreateRule.Arg.class);
        objectValueMappingService.createRule(arg, context);
    }

    private boolean updateIfExists(String tenantId, String describeApiName, ObjectDescribe describe, ObjectDescribe describeOld) throws MetadataServiceException {
        if (describeOld != null) {
            //重复刷新的时候，保证自定义字段不会被刷丢。
            List<IFieldDescribe> udfields = describeOld.getFieldDescribes().stream().filter((IFieldDescribe field) -> DEFINE_TYPE_CUSTOM.equals(field.getDefineType()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(udfields)) {
                List<IFieldDescribe> newFieldList = Lists.newArrayList();
                newFieldList.addAll(describe.getFieldDescribes());
                newFieldList.addAll(udfields);
                describe.setFieldDescribes(newFieldList);
            }
            //业务类型字段的option如果用户自己增加了新的选项，要保证不被刷丢。
            RecordTypeFieldDescribe oldRecordTypeFieldDescribe = (RecordTypeFieldDescribe) describeOld.getFieldDescribe("record_type");
            if (oldRecordTypeFieldDescribe != null && oldRecordTypeFieldDescribe.getRecordTypeOptions() != null && oldRecordTypeFieldDescribe.getRecordTypeOptions().size() > 1) {
                RecordTypeFieldDescribe newRecordTypeFieldDescribe = (RecordTypeFieldDescribe) describe.getFieldDescribe("record_type");
                newRecordTypeFieldDescribe.setRecordTypeOptions(oldRecordTypeFieldDescribe.getRecordTypeOptions());
            }
            describe.setTenantId(describeOld.getTenantId());
            describe.setId(describeOld.getId());
            describe.setApiName(describeOld.getApiName());
            describe.setVersion(describeOld.getVersion());
            describe.setLastModifiedBy(SYSTEM_OPT_USER_ID);
            objectDescribeService.replace(describe, false);
            log.info("objectDescribeDraftService.replace executed! tenantID:{},apiName:{}"
                    , tenantId, describeApiName);
            return true;
        }
        return false;
    }

    private String loadJsonFromResource(String ruleApiName) {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            return IOUtils.toString(classLoader.getResource(getMappingRulePath(ruleApiName)), "UTF-8");
        } catch (IOException e) {
            log.error("loadJsonFromResource error");
            return "";
        }
    }

    public void initLayoutByJson(String tenantId, String describeApiName, String layoutJson) throws MetadataServiceException {
        Layout layout = new Layout();
        layout.fromJsonString(layoutJson);
        layout.setTenantId(tenantId);
        List<ILayout> layouts = layoutService.findByObjectDescribeApiNameAndTenantId(describeApiName, tenantId);

        if (CollectionUtils.isNotEmpty(layouts)) {
            Optional<ILayout> oldLayoutOpt = layouts.stream().filter(x -> layout.getLayoutType().equals(x.getLayoutType())).findFirst();
            if (oldLayoutOpt.isPresent()) {
                ILayout oldLayout = oldLayoutOpt.get();
                layout.setId(oldLayout.getId());
                layout.setVersion(oldLayout.getVersion());
                layoutService.replace(layout);
            } else {
                layoutService.create(layout);
            }
        } else {
            layoutService.create(layout);
        }
    }

    private ObjectDescribe getDescribeFromLocalResource(String apiName) {
        ObjectDescribe describe = new ObjectDescribe();
        //获取到此对象的json数据
        String jsonStr = getDescribeJsonFromResourceByApiName(apiName);
        describe.fromJsonString(jsonStr);
        return describe;
    }

    /**
     * 从本地resource种获取对应对象的预制json格式。
     *
     * @param apiName 对象apiName
     */
    private String getDescribeJsonFromResourceByApiName(String apiName) {
        ClassLoader classLoader = getClass().getClassLoader();
        String jsonstr;
        try {
            jsonstr = IOUtils.toString(classLoader.getResource(
                    "describejson/init_crm_" + apiName + "_describe.json"), "UTF-8");
        } catch (IOException e) {
            log.error("initCRMDescribeByApiName file parse error");
            throw new MetadataValidateException(ErrorCode.FS_PAAS_MDS_FILE_PARSE_EXCEPTION, "initCRMDescribeByApiName file parse error", e);
        }
        return jsonstr;
    }

    private String getMappingRulePath(String ruleApiName) {
        return String.format("objectmappingrulejson/init_%s.json", ruleApiName);
    }

    public String getLayoutJsonFromResourceByApiName(String apiName, String type) {
        ClassLoader classLoader = getClass().getClassLoader();
        String jsonstr;
        try {
            jsonstr = IOUtils.toString(classLoader.getResource(
                    "layoutjson/init_crm_" + apiName + "_" + type + "_layout.json"), "UTF-8");
        } catch (IOException e) {
            log.error("initCRMDescribeByApiName file parse error");
            throw new MetadataValidateException(ErrorCode.FS_PAAS_MDS_FILE_PARSE_EXCEPTION, "initCRMDescribeByApiName file parse error", e);
        }
        return jsonstr;
    }
}
