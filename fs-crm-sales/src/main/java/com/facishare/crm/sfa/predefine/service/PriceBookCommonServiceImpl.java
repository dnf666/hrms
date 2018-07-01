package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.service.model.ValidImportSalesOrder;
import com.facishare.crm.sfa.utilities.common.convert.RangeVerify;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.paas.appframework.common.util.ObjectAPINameMapping;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.action.IActionContext;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by luxin on 2018/1/30.
 */
@Slf4j
@Service("priceBookCommonService")
public class PriceBookCommonServiceImpl implements PriceBookCommonService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private RangeVerify rangeVerify;

    @Override
    public List<IObjectData> getNotInPriceBookPriceBookProducts(String tenantId, String priceBookId, Map<String, String> priceBookProductId2ProductId) {
        Set<String> priceBookProductIds = priceBookProductId2ProductId.keySet();

        List<IObjectData> inPriceBookPriceBookProducts = getPriceBookProducts(tenantId, priceBookId, priceBookProductIds);
        Set<String> notInPriceBookPriceBookProductIds = getNotInPriceBookProductIds(priceBookProductIds, inPriceBookPriceBookProducts);

        if (notInPriceBookPriceBookProductIds.isEmpty()) {
            return Lists.newArrayList();
        } else {
            Set<String> productIds = Sets.newHashSet();
            notInPriceBookPriceBookProductIds.forEach(priceBookProductId -> productIds.add(priceBookProductId2ProductId.get(priceBookProductId)));
            return getProducts(tenantId, productIds);
        }
    }

    @Override
    public Map<String, List<String>> getNotInPriceBookPriceBookProductIds(String tenantId, Map<String, List<String>> priceBookId2priceBookProductIds) {
        List<String> allPriceBookProductIds = Lists.newArrayList();
        priceBookId2priceBookProductIds.forEach((k, v) -> allPriceBookProductIds.addAll(v));

        List<IObjectData> allPriceBookProductInfo = getPriceBookProductsByPriceBookIds(tenantId, allPriceBookProductIds);

        Map<String, Set<String>> priceBookId2InPriceBookPriceBookProductIds = getPriceBook2InPriceBookProductIds(allPriceBookProductInfo);

        return getPriceBookId2NotInPriceBookProductIds(priceBookId2priceBookProductIds, priceBookId2InPriceBookPriceBookProductIds);
    }

    @NotNull
    /**
     * 获取不再价目表中的价目表产品列表
     */
    private Map<String, List<String>> getPriceBookId2NotInPriceBookProductIds(Map<String, List<String>> priceBookId2priceBookProductIds, Map<String, Set<String>> priceBookId2InPriceBookPriceBookProductIds) {
        Map<String, List<String>> notInPriceBookPriceBookProductIds = Maps.newHashMap();
        priceBookId2priceBookProductIds.forEach((k, v) -> {
            Set<String> inPriceBookPriceBookProductIds = priceBookId2InPriceBookPriceBookProductIds.get(k);
            if (priceBookId2InPriceBookPriceBookProductIds.get(k) == null) {
                notInPriceBookPriceBookProductIds.put(k, v);
            } else {
                List<String> notInPriceBookProductIds = Lists.newArrayList();
                for (String priceBookProductId : v) {
                    if (!inPriceBookPriceBookProductIds.contains(priceBookProductId)) {
                        notInPriceBookProductIds.add(priceBookProductId);
                    }
                }
                if (CollectionUtils.isNotEmpty(notInPriceBookProductIds)) {
                    notInPriceBookPriceBookProductIds.put(k, notInPriceBookProductIds);
                }
            }
        });
        return notInPriceBookPriceBookProductIds;
    }

    @NotNull
    /**
     * 通过价目表产品信息获取 价目表id 对应 价目表产品id列表的map
     */
    private Map<String, Set<String>> getPriceBook2InPriceBookProductIds(List<IObjectData> allPriceBookProducts) {
        Map<String, Set<String>> priceBookId2InPriceBookPriceBookProductIds = Maps.newHashMap();

        for (IObjectData priceBookProduct : allPriceBookProducts) {
            String priceBookId = priceBookProduct.get("pricebook_id", String.class);
            String priceBookProductId = priceBookProduct.getId();

            Set<String> inPriceBookPriceBookProductIds = priceBookId2InPriceBookPriceBookProductIds.get(priceBookId);
            if (inPriceBookPriceBookProductIds == null) {
                priceBookId2InPriceBookPriceBookProductIds.put(priceBookId, Sets.newHashSet(priceBookProductId));
            } else {
                inPriceBookPriceBookProductIds.add(priceBookProductId);
            }
        }
        return priceBookId2InPriceBookPriceBookProductIds;
    }

    @Override
    public Boolean validateAccountPriceBook(User user, String priceBookId, String accountId) {
        String tenantId = user.getTenantId();
        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(priceBookId)) {
            log.error("accountId or pricebook_id is blank,tenantId {} ,priceBookId {}，accountId {}", tenantId, priceBookId, accountId);
            return false;
        }
        List<IObjectData> accountObjDataList = serviceFacade.findObjectDataByIds(user.getTenantId(), Lists.newArrayList(accountId), ObjectAPINameMapping.Account.getApiName());
        if (CollectionUtils.isEmpty(accountObjDataList)) {
            return false;
        }
        IObjectData accountObjData, priceBookData;
        try {
            accountObjData = accountObjDataList.get(0);
            IActionContext actionContext = ActionContextExt.of(user).getContext();
            //不验证功能权限和数据权限
            actionContext.setPrivilegeCheck(false);
            priceBookData = serviceFacade.findObjectData(actionContext, priceBookId, PriceBookConstants.API_NAME);
        } catch (Exception e) {
            log.info("validateAccountPriceBook findObjectData error,priceBookId {}，accountId {}", priceBookId, accountId, e);
            return false;
        }
        if (priceBookData == null || accountObjData == null) {
            return false;
        }
        //正常生命状态
        //启用状态
        if (!UdobjConstants.LIFE_STATUS_VALUE_NORMAL.equals(priceBookData.get(UdobjConstants.LIFE_STATUS_API_NAME))
                || !PriceBookConstants.ActiveStatus.ON.getStatus().equals(priceBookData.get(PriceBookConstants.Field.ACTIVESTATUS.getApiName()))) {
            return false;
        }
        IObjectDescribe accountDescribe = this.serviceFacade.findObject(tenantId, Utils.ACCOUNT_API_NAME);
        return this.validateAccountPriceBookWithData(user, accountDescribe, priceBookData, accountObjData);
    }


    private List<IObjectData> getProducts(String tenantId, Set<String> productIds) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1000);

        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, "_id", productIds);
        searchQuery.setFilters(filters);
        searchQuery.setPermissionType(0);
        IActionContext context = ActionContextExt.of(new User(tenantId, "-10000")).dbType("rest").getContext();

        return serviceFacade.findObjectDataByIds(context, Lists.newArrayList(productIds), Utils.PRODUCT_API_NAME);
    }


    private Set<String> getNotInPriceBookProductIds(Set<String> priceBookProductIds, List<IObjectData> priceBookProducts) {
        if (CollectionUtils.isEmpty(priceBookProducts)) {
            return priceBookProductIds;
        } else {
            Set<Object> tmpPriceBookProductIds = priceBookProducts.stream().map(objectData -> objectData.get("_id")).collect(Collectors.toSet());
            // TODO: 2018/5/16 SA 需要价目表过滤
            return priceBookProductIds.stream().filter(priceBookProductId -> !tmpPriceBookProductIds.contains(priceBookProductId)).collect(Collectors.toSet());
        }
    }


    private List<IObjectData> getPriceBookProducts(String tenantId, String priceBookId, Set<String> priceBookProductIds) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1000);
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, "pricebook_id", priceBookId);
        SearchUtil.fillFilterIn(filters, "_id", Lists.newArrayList(priceBookProductIds));
        searchQuery.setFilters(filters);

        searchQuery.setPermissionType(0);
        // TODO: 2018/5/16 SA 需要价目表过滤
        return serviceFacade.findBySearchQuery(new User(tenantId, "-10000"), Utils.PRICE_BOOK_PRODUCT_API_NAME, searchQuery).getData();
    }


    private List<IObjectData> getPriceBookProductsByPriceBookIds(String tenantId, List<String> priceBookProductIds) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1000);
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, "_id", priceBookProductIds);
        searchQuery.setFilters(filters);

        searchQuery.setPermissionType(0);
        // TODO: 2018/5/16 SA 需要价目表过滤
        return serviceFacade.findBySearchQuery(new User(tenantId, "-10000"), Utils.PRICE_BOOK_PRODUCT_API_NAME, searchQuery).getData();
    }


    @Override
    public Boolean validateAccountPriceBookWithData(User user, IObjectDescribe accountDescribe, IObjectData priceBookData, IObjectData accountData) {
        return this.validateAccountPriceBookWithData(user, accountDescribe, priceBookData, accountData, null, null);
    }

    @Override
    public Boolean validateAccountPriceBookWithData(User user, IObjectDescribe accountDescribe, IObjectData priceBookData, IObjectData accountData, List<String> departList, Boolean isUserAdmin) {
        return this.batchValidateDept(user, priceBookData, departList, isUserAdmin) && this.validatePriceBook(accountDescribe, accountData, priceBookData);
    }

    @Override
    public List<ValidImportSalesOrder.ValidPriceBookImportInfo> validateAccountPriceBook(User user, List<ValidImportSalesOrder.ValidPriceBookImportInfo> importInfos) {
        Map<ValidImportSalesOrder.ValidPriceBookImportInfoKey, ValidImportSalesOrder.ValidPriceBookImportInfo> validPriceBookImportInfoKey2ValidPBInfo = Maps.newHashMap();

        Set<String> customerIds = Sets.newHashSet();
        Set<String> priceBookIds = Sets.newHashSet();
        for (ValidImportSalesOrder.ValidPriceBookImportInfo importInfo : importInfos) {
            String customerId = importInfo.getCustomerId();
            String priceBookId = importInfo.getPriceBookId();
            customerIds.add(customerId);
            priceBookIds.add(priceBookId);
            validPriceBookImportInfoKey2ValidPBInfo.put(new ValidImportSalesOrder.ValidPriceBookImportInfoKey(customerId, priceBookId), importInfo);
        }

        List<IObjectData> priceBookDataList = serviceFacade.findObjectDataByIds(user.getTenantId(), Lists.newArrayList(priceBookIds), Utils.PRICE_BOOK_API_NAME);
        Map<String, IObjectData> priceBookId2ObjectData = Maps.newHashMap();
        Map<String, Boolean> priceBookId2ValidateDeptStatus = Maps.newHashMap();
        priceBookDataList.forEach(priceBookData -> {
            if (priceBookData.getId() != null) {
                priceBookId2ObjectData.put(priceBookData.getId(), priceBookData);
                priceBookId2ValidateDeptStatus.put(priceBookData.getId(), validateDept(user, priceBookData));
            }
        });

        Map<String, IObjectData> customerId2ObjectData = getCustomerId2ObjectData(user.getTenantId(), Lists.newArrayList(customerIds));
        Set<String> effectCustomerIds = customerId2ObjectData.keySet();
        Set<String> effectPriceBookIds = priceBookId2ObjectData.keySet();

        IObjectDescribe accountDescribe = this.serviceFacade.findObject(user.getTenantId(), Utils.ACCOUNT_API_NAME);

        validPriceBookImportInfoKey2ValidPBInfo.forEach((importInfoKey, validPBInfo) -> {
            if (effectCustomerIds.contains(importInfoKey.getCustomerId())
                    && effectPriceBookIds.contains(importInfoKey.getPriceBookId())
                    && priceBookId2ValidateDeptStatus.get(importInfoKey.getPriceBookId())
                    && validatePriceBook(accountDescribe, customerId2ObjectData.get(importInfoKey.getCustomerId()), priceBookId2ObjectData.get(importInfoKey.getPriceBookId()))) {
                validPBInfo.setIsApply(Boolean.TRUE);
            } else {
                validPBInfo.setIsApply(Boolean.FALSE);
            }
        });
        return importInfos;
    }


    private Map<String, IObjectData> getCustomerId2ObjectData(String tenantId, List<String> objectIds) {
        List<IObjectData> objectDataList = serviceFacade.findObjectDataByIds(tenantId, objectIds, Utils.ACCOUNT_API_NAME);

        Map<String, IObjectData> objectId2ObjectData = Maps.newHashMap();
        objectDataList.forEach(o -> {
            if (o.getId() != null)
                objectId2ObjectData.put(o.getId(), o);
        });
        return objectId2ObjectData;
    }


    private boolean validateDept(User user, IObjectData priceBookData) {
        return batchValidateDept(user, priceBookData, null, null);
    }

    private boolean batchValidateDept(User user, IObjectData priceBookData, List<String> departList, Boolean isUserAdmin) {
        if (Objects.isNull(isUserAdmin)) {
            isUserAdmin = serviceFacade.isAdmin(user);
        }
        if (isUserAdmin) {
            return true;
        }
        Object deptRangeObj = priceBookData.get("dept_range");
        if (Objects.isNull(deptRangeObj)) {
            return false;
        }
        List<String> deptRange = (List<String>) deptRangeObj;
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(deptRange) && deptRange.contains("999999")) {
            return true;
        }
        if (Objects.isNull(departList)) {
            departList = serviceFacade.queryAllSuperDeptByUserId(user.getTenantId(), User.SUPPER_ADMIN_USER_ID, user.getUserId());
        }
        for (String dept : deptRange) {
            if (departList.contains(dept)) {
                return true;
            }
        }
        return false;
    }

    private boolean validatePriceBook(IObjectDescribe describe, IObjectData accountObjData, IObjectData priceBookData) {
        //根据价目表的有效开始时间和结束时间验证,过期不可用后，则返回false
        if (!validatePriceBookUsableByExpiry(priceBookData)) {
            return false;
        }
        // 根据当前客户的适用范围过滤符合条件的价目表
        Object accountRange = priceBookData.get("account_range");
        if (Objects.nonNull(accountRange) && StringUtils.isNotBlank(accountRange.toString())) {
            JSONObject accountRangeObj = JSON.parseObject(accountRange.toString());
            if (accountRangeObj != null && !"noCondition".equals(accountRangeObj.get("type").toString())) {
                return rangeVerify.verifyConditions(describe, accountObjData, accountRangeObj.getJSONObject("value"));
            }
        }
        return true;
    }

    /**
     * 根据价目表过期时期验证是否可用，true代表可用，false代表不可用
     */
    public boolean validatePriceBookUsableByExpiry(IObjectData priceBookData) {
        Long startDateTime = priceBookData.get(PriceBookConstants.Field.STARTDATE.getApiName(), Long.class);
        Long endDate = priceBookData.get(PriceBookConstants.Field.ENDDATE.getApiName(), Long.class);
        Long nowDateTime = System.currentTimeMillis();
        if (startDateTime != null && startDateTime > nowDateTime) {
            return false;
        }
        if (endDate != null && endDate < nowDateTime) {
            return false;
        }
        return true;
    }

}
