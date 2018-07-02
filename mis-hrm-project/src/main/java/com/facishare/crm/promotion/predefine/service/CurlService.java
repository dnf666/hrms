package com.facishare.crm.promotion.predefine.service;

import static com.facishare.crm.constants.CommonConstants.CRM_MANAGER_ROLE;
import static com.facishare.crm.constants.CommonConstants.ORDER_MANAGER_ROLE;
import static com.facishare.crm.constants.CommonConstants.PRODUCT_MANAGER_ROLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.NumberFieldDescribeBuilder;
import com.facishare.crm.describebuilder.SelectOneFieldDescribeBuilder;
import com.facishare.crm.describebuilder.SelectOptionBuilder;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.GiftTypeEnum;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.crm.promotion.predefine.manager.TenantConfigManager;
import com.facishare.crm.promotion.predefine.service.dto.AddEditActionModel;
import com.facishare.crm.promotion.predefine.service.dto.EmptyResult;
import com.facishare.crm.promotion.predefine.service.dto.PromotionStatusModel;
import com.facishare.crm.promotion.predefine.service.dto.PromotionType;
import com.facishare.crm.rest.FunctionProxy;
import com.facishare.crm.rest.dto.DelFuncModel;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.FieldLayoutPojo;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.RecordTypeLogicService;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeProxy;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.appframework.privilege.dto.AuthContext;
import com.facishare.paas.appframework.privilege.dto.CreateFunctionPrivilege;
import com.facishare.paas.appframework.privilege.model.FunctionPrivilegeProvider;
import com.facishare.paas.appframework.privilege.model.FunctionPrivilegeProviderManager;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.NumberFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ServiceModule("promotion_curl")
public class CurlService {
    @Autowired
    private FunctionPrivilegeProviderManager providerManager;
    @Autowired
    private FunctionProxy functionProxy;
    @Autowired
    private RecordTypeLogicService recordTypeLogicService;
    @Autowired
    private FunctionPrivilegeService functionPrivilegeService;
    @Autowired
    private TenantConfigManager tenantConfigManager;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private FunctionPrivilegeProxy functionPrivilegeProxy;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private PromotionInitService promotionInitService;
    @Autowired
    private DescribeLogicService describeLogicService;

    @ServiceMethod("addEditAction")
    public EmptyResult addEidtAction(ServiceContext serviceContext, AddEditActionModel.Arg arg) {
        List<String> tenantIds = arg.getTenantIds() == null ? Lists.newArrayList(serviceContext.getTenantId()) : arg.getTenantIds();
        if (!tenantIds.contains(serviceContext.getTenantId())) {
            tenantIds.add(serviceContext.getTenantId());
        }
        List<String> objectApiNames = arg.getObjectApiNames();
        if (CollectionUtils.empty(objectApiNames)) {
            return new EmptyResult();
        }

        for (String tenantId : tenantIds) {
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            for (String objectApiName : objectApiNames) {
                addEditAction(user, objectApiName);
            }
        }
        return new EmptyResult();
    }

    private void addEditAction(User user, String objectApiName) {
        String layoutApiName;
        List<String> funcCodes = Lists.newArrayList();
        if (PromotionProductConstants.API_NAME.equals(objectApiName)) {
            layoutApiName = PromotionProductConstants.DEFAULT_LAYOUT_API_NAME;
        } else if (PromotionRuleConstants.API_NAME.equals(objectApiName)) {
            layoutApiName = PromotionRuleConstants.DEFAULT_LAYOUT_API_NAME;
        } else {
            log.info("objectApiName:{} not match", objectApiName);
            return;
        }

        FunctionPrivilegeProvider provider = this.providerManager.getProvider(objectApiName);
        provider.getSupportedActionCodes().forEach(actionCode -> {
            if (actionCode.equals(ObjectAction.VIEW_LIST.getActionCode())) {
                funcCodes.add(objectApiName);
            } else {
                funcCodes.add(objectApiName + "||" + actionCode);
            }
        });
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(user.getTenantId()).userId(user.getUserId()).appId("CRM").build());
        arg.setFuncSet(funcCodes);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        log.debug("addEidtAction->delFuncModel.Result result:{}", result);
        this.functionPrivilegeService.initFunctionPrivilege(user, objectApiName);
        this.recordTypeLogicService.recordTypeInit(user, layoutApiName, user.getTenantId(), objectApiName);
    }

    @ServiceMethod("batchGetPromotionStatus")
    public PromotionStatusModel.Result batchGetPromotionStatus(ServiceContext serviceContext, PromotionStatusModel.Arg arg) {
        List<String> tenantIds = arg.getTenantIds();
        PromotionStatusModel.Result result = new PromotionStatusModel.Result();
        Map<String, String> promotionStatus = Maps.newHashMap();
        if (CollectionUtils.notEmpty(tenantIds)) {
            tenantIds.forEach(tenantId -> {
                PromotionType.PromotionSwitchEnum promotionSwitchEnum = tenantConfigManager.getPromotionStatus(tenantId);
                promotionStatus.put(tenantId, promotionSwitchEnum.name());
            });
        }
        result.setPromotionStatus(promotionStatus);
        return result;
    }

    @ServiceMethod("onlyAddEditAction")
    public EmptyResult justAddEditAction(ServiceContext serviceContext, AddEditActionModel.Arg arg) {
        List<String> tenantIds = arg.getTenantIds() == null ? Lists.newArrayList(serviceContext.getTenantId()) : arg.getTenantIds();
        if (!tenantIds.contains(serviceContext.getTenantId())) {
            tenantIds.add(serviceContext.getTenantId());
        }
        List<String> objectApiNames = arg.getObjectApiNames();
        if (CollectionUtils.empty(objectApiNames)) {
            return new EmptyResult();
        }

        for (String tenantId : tenantIds) {
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            for (String objectApiName : objectApiNames) {
                onlyAddEditAction(user, objectApiName);
            }
        }
        return new EmptyResult();
    }

    private void onlyAddEditAction(User user, String objectApiName) {
        AuthContext authContext = AuthContext.builder().appId("CRM").tenantId(user.getTenantId()).userId(user.getUserId()).build();
        //添加权限
        List<CreateFunctionPrivilege.FunctionPojo> functionPojos = getUserDefinedFunctionPojoList(authContext.getTenantId(), objectApiName);
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(functionPojos)) {
            CreateFunctionPrivilege.Arg arg = CreateFunctionPrivilege.Arg.builder().authContext(authContext).functionPojoList(functionPojos).build();
            this.functionPrivilegeProxy.createFunctionPrivilege(arg);
        }
        //给角色添加权限
        addRoleEditFunctionPrivilege(authContext, CRM_MANAGER_ROLE, objectApiName);
        addRoleEditFunctionPrivilege(authContext, PRODUCT_MANAGER_ROLE, objectApiName);
        addRoleEditFunctionPrivilege(authContext, ORDER_MANAGER_ROLE, objectApiName);
    }

    private void addRoleEditFunctionPrivilege(AuthContext authContext, String roleCode, String objectApiName) {
        FunctionPrivilegeProvider provider = this.providerManager.getProvider(objectApiName);
        String funcCode = provider.getFunctionCodeFromActionCode(objectApiName, "Edit");
        List<String> funcCodeList = new ArrayList<>();
        funcCodeList.add(funcCode);
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(funcCodeList)) {
            com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Arg arg = com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Arg.builder().authContext(authContext).roleCode(roleCode).addFuncCode(funcCodeList).build();
            com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Result result = this.functionPrivilegeProxy.updateRoleModifiedFuncPrivilege(arg);
            if (!result.isSuccess()) {
                log.error("addFunctionPrivilege error,arg:{},result:{}", arg, result);
            }
        }
    }

    private List<CreateFunctionPrivilege.FunctionPojo> getUserDefinedFunctionPojoList(String tenantId, String apiName) {
        FunctionPrivilegeProvider provider = this.providerManager.getProvider(apiName);
        ArrayList userDefinedFunctionPojoList = Lists.newArrayList();
        String actionCode = ObjectAction.UPDATE.getActionCode();
        CreateFunctionPrivilege.FunctionPojo pojo = new CreateFunctionPrivilege.FunctionPojo();
        pojo.setAppId("CRM");
        pojo.setTenantId(tenantId);
        pojo.setFuncType(Integer.valueOf(1));
        pojo.setParentCode("00000000000000000000000000000000");
        pojo.setFuncName(ObjectAction.of(actionCode).getActionLabel());
        pojo.setFuncCode(provider.getFunctionCodeFromActionCode(apiName, actionCode));
        userDefinedFunctionPojoList.add(pojo);
        return userDefinedFunctionPojoList;
    }

    @ServiceMethod("disableAddFieldInPromotionRule")
    public EmptyResult disableAddFieldInPromotionRule(TenantIdModel.Arg arg, ServiceContext serviceContext) {
        List<String> tenantIds = arg.getTenantIds();
        EmptyResult emptyResult = new EmptyResult();
        if (CollectionUtils.empty(tenantIds)) {
            return emptyResult;
        }
        if (!tenantIds.contains(serviceContext.getTenantId())) {
            tenantIds.add(serviceContext.getTenantId());
        }
        List<String> errorTenantIds = Lists.newArrayList();
        for (String tenantId : tenantIds) {
            try {
                IObjectDescribe promotionRuleDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, PromotionRuleConstants.API_NAME);
                //禁止添加字段
                Map<String, Object> configMap = Maps.newHashMap();
                Map<String, Object> fieldConfigMap = Maps.newHashMap();
                fieldConfigMap.put("add", 0);
                configMap.put("fields", fieldConfigMap);
                promotionRuleDescribe.setConfig(configMap);
                promotionRuleDescribe = objectDescribeService.updateDescribe(promotionRuleDescribe);
                log.info("after disableAddFieldInPromotionRule describe:{}", promotionRuleDescribe.toJsonString());
            } catch (MetadataServiceException e) {
                log.warn("disableAddFieldInPromotionRule", e);
                errorTenantIds.add(tenantId);
            }
        }
        if (CollectionUtils.notEmpty(errorTenantIds)) {
            emptyResult.setMessage("error tenantIds:" + JsonUtil.toJson(errorTenantIds));
        }
        return emptyResult;
    }

    @ServiceMethod("addGiftTypeAndQuotaField")
    public EmptyResult addGiftTypeInPromotionRule(TenantIdModel.Arg arg, ServiceContext serviceContext) {
        for (String tenantId : arg.getTenantIds()) {
            PromotionType.PromotionSwitchEnum promotionSwitchEnum = tenantConfigManager.getPromotionStatus(tenantId);
            if (promotionSwitchEnum != PromotionType.PromotionSwitchEnum.OPENED) {
                continue;
            }
            IObjectDescribe promotionRuleObjectDescribe = serviceFacade.findObject(tenantId, PromotionRuleConstants.API_NAME);
            FieldLayoutPojo giftTypeFieldLayoutPojo = new FieldLayoutPojo();
            giftTypeFieldLayoutPojo.setReadonly(false);
            giftTypeFieldLayoutPojo.setRequired(false);
            giftTypeFieldLayoutPojo.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
            promotionRuleObjectDescribe = addFieldAndLayout(promotionRuleObjectDescribe, PromotionRuleConstants.Field.GiftType.apiName, getGiftTypeFieldDescribe(), PromotionRuleConstants.DEFAULT_LAYOUT_API_NAME, giftTypeFieldLayoutPojo);
            removeGiftDataFilterInPromotionRule(promotionRuleObjectDescribe);

            IObjectDescribe promotionProductObjectDescribe = serviceFacade.findObject(tenantId, PromotionProductConstants.API_NAME);
            FieldLayoutPojo quotaFieldLayoutPojo = new FieldLayoutPojo();
            quotaFieldLayoutPojo.setReadonly(false);
            quotaFieldLayoutPojo.setRequired(false);
            quotaFieldLayoutPojo.setRenderType(SystemConstants.RenderType.Number.renderType);
            addFieldAndLayout(promotionProductObjectDescribe, PromotionProductConstants.Field.Quota.apiName, getQuotaFieldDescribe(), PromotionProductConstants.DEFAULT_LAYOUT_API_NAME, quotaFieldLayoutPojo);
        }
        fillGiftType(serviceContext, arg);
        return new EmptyResult();
    }

    @ServiceMethod("fill_gift_type")
    public TenantIdModel.Result fillGiftType(ServiceContext serviceContext, TenantIdModel.Arg arg) {
        TenantIdModel.Result result = new TenantIdModel.Result();
        List<String> sucTenantIds = Lists.newArrayList();
        for (String tenantId : arg.getTenantIds()) {
            PromotionType.PromotionSwitchEnum promotionSwitchEnum = tenantConfigManager.getPromotionStatus(tenantId);
            if (promotionSwitchEnum != PromotionType.PromotionSwitchEnum.OPENED) {
                continue;
            }
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            IObjectDescribe objectDescribe = serviceFacade.findObject(user.getTenantId(), PromotionRuleConstants.API_NAME);
            int limit = 100;
            int offset = 0;
            int size;
            do {
                SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
                searchTemplateQuery.setFilters(Lists.newArrayList());
                searchTemplateQuery.setLimit(limit);
                searchTemplateQuery.setOffset(offset);
                List<OrderBy> orders = Lists.newArrayList();
                SearchUtil.fillOrderBy(orders, SystemConstants.Field.Id.apiName, true);
                searchTemplateQuery.setOrders(orders);
                QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQueryWithDeleted(user, objectDescribe, searchTemplateQuery);
                fillGiftTypeWithNormalProductEnum(user, queryResult.getData());
                size = queryResult.getData().size();
                offset += limit;
            } while (size == limit);
            sucTenantIds.add(tenantId);
        }
        result.setTenantIds(sucTenantIds);
        return result;
    }

    @ServiceMethod("init_advertisement")
    public TenantIdModel.Result initAdvertisement(ServiceContext serviceContext, TenantIdModel.Arg arg) {
        TenantIdModel.Result result = new TenantIdModel.Result();
        List<String> sucTenantIds = Lists.newArrayList();
        for (String tenantId : arg.getTenantIds()) {
            PromotionType.PromotionSwitchEnum promotionSwitchEnum = tenantConfigManager.getPromotionStatus(tenantId);
            if (promotionSwitchEnum != PromotionType.PromotionSwitchEnum.OPENED) {
                continue;
            }
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            DescribeResult describeResult = promotionInitService.initAdvertisement(user);
            log.info("DescribeResult:{}", describeResult);
            sucTenantIds.add(tenantId);
        }
        result.setTenantIds(sucTenantIds);
        return result;
    }

    //删除促销规则中 赠品只能选择赠品 的过滤
    private IObjectDescribe removeGiftDataFilterInPromotionRule(IObjectDescribe objectDescribe) {
        try {
            ObjectReferenceFieldDescribe giftProductFieldDescribe = (ObjectReferenceFieldDescribe) objectDescribe.getFieldDescribe(PromotionRuleConstants.Field.GiftProduct.apiName);
            if (giftProductFieldDescribe != null && CollectionUtils.notEmpty(giftProductFieldDescribe.getWheres())) {
                giftProductFieldDescribe.setWheres(null);
                objectDescribe = objectDescribeService.updateFieldDescribe(objectDescribe, Lists.newArrayList(giftProductFieldDescribe));
            }
        } catch (MetadataServiceException e) {
            log.warn("", e);
        }
        return objectDescribe;
    }
    //删除促销产品 只能选择非赠品的过滤

    private IObjectDescribe addFieldAndLayout(IObjectDescribe objectDescribe, String fieldApiName, IFieldDescribe toAddFieldDescribe, String layoutApiName, FieldLayoutPojo fieldLayoutPojo) {
        try {
            IFieldDescribe fieldDescribe = objectDescribe.getFieldDescribe(fieldApiName);
            if (fieldDescribe == null) {
                User user = new User(objectDescribe.getTenantId(), User.SUPPER_ADMIN_USER_ID);
                objectDescribe = objectDescribeService.addCustomFieldDescribe(objectDescribe, Lists.newArrayList(toAddFieldDescribe));
                log.info("updated describe:{}", objectDescribe.toJsonString());
                ILayout layout = serviceFacade.findLayoutByApiName(user, layoutApiName, objectDescribe.getApiName());
                LayoutExt layoutExt = LayoutExt.of(layout);
                layoutExt.addField(toAddFieldDescribe, fieldLayoutPojo);
                layout = serviceFacade.updateLayout(user, layout);
                log.info("updated layout:{}", layout.toJsonString());
            }
        } catch (MetadataServiceException e) {
            log.warn("", e);
        }
        return objectDescribe;
    }

    private IFieldDescribe getGiftTypeFieldDescribe() {
        List<ISelectOption> giftTypeSelectOptions = Arrays.stream(GiftTypeEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe giftTypeSelectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.GiftType.apiName).label(PromotionRuleConstants.Field.GiftType.label).selectOptions(giftTypeSelectOptions).required(false).build();
        return giftTypeSelectOneFieldDescribe;
    }

    private IFieldDescribe getQuotaFieldDescribe() {
        NumberFieldDescribe quotaNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(PromotionProductConstants.Field.Quota.apiName).label(PromotionProductConstants.Field.Quota.label).defaultValue("0").decimalPalces(0).length(12).maxLength(14).build();
        return quotaNumberFieldDescribe;
    }

    private List<IObjectData> fillGiftTypeWithNormalProductEnum(User user, List<IObjectData> objectDataList) {
        List<IObjectData> result = Lists.newArrayList();
        objectDataList.stream().filter(objectData -> {
            boolean isProductPromotion = objectData.get(SystemConstants.Field.RecordType.apiName, String.class).equals(PromotionRecordTypeEnum.ProductPromotion.apiName);
            Object purchaseNum = objectData.get(PromotionRuleConstants.Field.PurchaseNum.apiName);
            return Objects.nonNull(purchaseNum) && isProductPromotion & !objectData.isDeleted();
        }).forEach(objectData -> {
            objectData.set(PromotionRuleConstants.Field.GiftType.apiName, GiftTypeEnum.NormalProduct.value);
            objectData = serviceFacade.updateObjectData(user, objectData, true);
            result.add(objectData);
        });
        return result;
    }

    @ServiceMethod("initAdvertisementLayoutRule")
    public TenantIdModel.Result initAdvertisementLayoutRule(ServiceContext serviceContext, TenantIdModel.Arg arg) {
        List<String> tenantIds = arg.getTenantIds();
        TenantIdModel.Result result = new TenantIdModel.Result();
        List<String> failTenantIds = Lists.newArrayList();
        if (CollectionUtils.notEmpty(tenantIds)) {
            tenantIds.forEach(tenantId -> {
                try {
                    User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
                    promotionInitService.initAdvertisementLayoutRule(user);
                } catch (Exception e) {
                    log.warn("", e);
                    failTenantIds.add(tenantId);
                }
            });
        }
        result.setTenantIds(failTenantIds);
        return result;
    }

    @Data
    public static class TenantIdModel {
        private int offset = 0;
        private int limit = 20;

        @Data
        public static class Arg {
            private List<String> tenantIds;
        }

        @Data
        public static class Result {
            private List<String> tenantIds;
        }
    }
}
