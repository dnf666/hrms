package com.facishare.crm.sfa.predefine.service;

import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.mq.ProductEvent;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.TeamMember;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ObjectData;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@ServiceModule("pricebook_standard")
@Service
@Slf4j
public class PriceBookStandardService {
    @Autowired
    PriceBookService priceBookService;
    @Autowired
    ServiceFacade serviceFacade;
    @Autowired
    private ObjectDataServiceImpl objectDataService;
    @Autowired
    private ThreadPoolTaskExecutor executor;

    @ServiceMethod("update_price_product")
    public boolean updateStandardPriceBookProduct(JSONObject object, ServiceContext context) {
        return doUpdateStandardPriceBookProduct(context.getTenantId(), object.getString("productId"), object.getString("actionCode"));
    }

    public boolean doUpdateStandardPriceBookProduct(String tenantId, String productId, String actionCode) {
        IObjectData standardPriceBook = priceBookService.getStandardPriceBook(new User(tenantId, ProductEvent.SUPER_ADMIN_USER_ID));

        if (standardPriceBook == null) {
            log.error("standard priceBook is not exists,tenantI1d {},productId {},actionCode {}", tenantId, productId, actionCode);
            return false;
        }

        User user = new User(tenantId, getOwnerId(standardPriceBook.get(UdobjConstants.OWNER_API_NAME)));

        String priceBookId = standardPriceBook.getId();
        IObjectData priceBookProduct = priceBookService.getPriceBookProduct(user, priceBookId, productId);
        if (ProductEvent.ADD_ACTION.equals(actionCode)) {
            if (priceBookProduct != null) {
                log.info("standard priceBook product has exist，skip ，tenantId {},productId {},actionCode {}", user.getTenantId(), productId, actionCode);
                return true;
            }

            IObjectData productObj = null;
            try {
                productObj = serviceFacade.findObjectData(ActionContextExt.of(user).pgDbType().getContext(), productId, Utils.PRODUCT_API_NAME);
                //验证产品是否存在，不存在则返回false
            } catch (Exception ex) {
                log.error("ObjectDataException occurs", ex);
                return false;
            }
            if (productObj == null) {
                log.debug("product has not exist ，tenantId {},productId {},actionCode {}", user.getTenantId(), productId, actionCode);
                return false;
            }
            //处理对象
            IObjectData objectData = new ObjectData();
            //标准价目表的价目表产品id=产品id+tenantId
            objectData.setId(productId + tenantId);
            objectData.set(PriceBookConstants.ProductField.PRICEBOOKID.getApiName(), priceBookId);
            objectData.set(PriceBookConstants.ProductField.PRODUCTID.getApiName(), productId);
            IObjectDescribe priceBookProductDescribe = serviceFacade.findObject(user.getTenantId(), PriceBookConstants.API_NAME_PRODUCT);
            fillDefaultObject(user, priceBookProductDescribe, objectData);

            try {
                if (serviceFacade.saveObjectData(user, objectData) == null) {
                    return false;
                }
            } catch (MetaDataBusinessException e) {
                log.warn("synchronized failed.", e);
                return Boolean.FALSE;
            }
        } else if (ProductEvent.DELETE_ACTION.equals(actionCode)) {
            if (priceBookProduct == null) {
                log.info("standard priceBook product is not exist，skip ，tenantId {},productId {},actionCode {}", user.getTenantId(), productId, actionCode);
                return true;
            }
            IObjectData productObj = null;
            try {
                productObj = serviceFacade.findObjectData(ActionContextExt.of(user).pgDbType().getContext(), productId, Utils.PRODUCT_API_NAME);
            } catch (MetaDataBusinessException e) {
                log.info(e.getMessage(), e);
            }
            //如果产品还没有被删除，则返回失败
            if (productObj != null) {
                log.error("product is exist,delete failed ，tenantId {},productId {},actionCode {}", user.getTenantId(), productId, actionCode);
                return false;
            }
            String result = serviceFacade.bulkInvalidAndDeleteWithSuperPrivilege(Arrays.asList(priceBookProduct), user);
            if (StringUtils.isNotBlank(result)) {
                log.error("delete priceBook product failed,tenantId {},productId {},failReason {}", user.getTenantId(), productId, result);
                return false;
            }
        }
        return true;
    }


    private String getOwnerId(Object ownerIdTmp) {
        String ownerId;
        if (ownerIdTmp == null) {
            ownerId = ProductEvent.SUPER_ADMIN_USER_ID;
        } else {
            List<String> ownerIds = ((List<String>) ownerIdTmp);
            if (CollectionUtils.isNotEmpty(ownerIds)) {
                ownerId = ownerIds.get(0);
            } else {
                ownerId = ProductEvent.SUPER_ADMIN_USER_ID;
            }
        }
        return ownerId;
    }

    @ServiceMethod("check_or_init_standard_pricebook")
    public Map checkOrInitStandardPricebook(ServiceContext context) {
        Map map = Maps.newHashMap();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean result = false;
                String tenantId = context.getTenantId();
                try {
                    log.info("fs-crm:updatePricebook:start,tenantId {}", tenantId);
                    result = initStandardPriceProduct(tenantId, true);
                    if (result) {
                        log.info("fs-crm:updatePricebook:success,tenantId {}", tenantId);
                    } else {
                        log.error("fs-crm:updatePricebook:result  error,tenantId {}", tenantId);
                    }
                } catch (Exception e) {
                    log.error("fs-crm:updatePricebook:exception error,tenantId {}", tenantId, e);
                }
            }
        }).start();
        map.put("result", true);
        return map;
    }

    @ServiceMethod("init_pricebook")
    public Map initStandardPrice(JSONObject obj, ServiceContext context) {
        Map map = Maps.newHashMap();
        Boolean initProduct = obj.getBoolean("initProduct") != null ? obj.getBoolean("initProduct") : true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String tenantId : obj.getString("tenantIds").split(",")) {
                    // TODO: 2017/12/21 改为单线程刷库
//            executor.execute(() -> {
                    Boolean result = false;
                    try {
                        log.info("fs-crm:initStandardPrice:start,tenantId {}", tenantId);
                        result = initStandardPriceProduct(tenantId, initProduct);
                        if (result) {
                            log.info("fs-crm:initStandardPrice:success,tenantId {}", tenantId);
                        } else {
                            log.error("fs-crm:initStandardPrice:result  error,tenantId {}", tenantId);
                        }
                    } catch (Exception e) {
                        log.error("fs-crm:initStandardPrice:exception error,tenantId {}", tenantId, e);
                    }
//            });
                    map.put(tenantId, "正在处理中");
                }
            }
        }).start();
        return map;
    }


    public boolean initStandardPriceProduct(String tenantId, Boolean initProduct) {
        User user = new User(tenantId, ProductEvent.SUPER_ADMIN_USER_ID);
        IObjectData standardPriceBook = priceBookService.getStandardPriceBook(user);
        if (standardPriceBook == null) {
            standardPriceBook = initStandardPriceBook(tenantId);
        }
        if (!initProduct) {
            return true;
        }
        //如果发现标准价目表同步失败
        if (standardPriceBook == null) {
            log.error("standard priceBook init error,tenantId {}", tenantId);
            return false;
        }
        //获取当前标准价目表下已有的产品列表
        String priceBookId = standardPriceBook.getId();
        String salesOrderDiscount = null;
        try {
            salesOrderDiscount = getSalesOrderDisount(tenantId);
        } catch (Exception e) {
            log.warn("getSalesOrderDisount error,tenantId {}", tenantId, e);
        }
        //分页处理企业所有的产品
        Integer offset = 0, limit = 500, count = 0;
        IObjectDescribe priceProductDescribe = serviceFacade.findObject(user.getTenantId(), PriceBookConstants.API_NAME_PRODUCT);
        boolean flag = true;
        while (true) {
            QueryResult<IObjectData> productResult = priceBookService.findProductNotInPriceBook(user, priceBookId, Lists.newLinkedList(), offset, limit);
            if (productResult.getData().size() <= 0) {
                break;
            }
            count += productResult.getData().size();
            List<IObjectData> addPriceProductList = Lists.newLinkedList();
            for (IObjectData productObj : productResult.getData()) {
                IObjectData objectData = new ObjectData();
                //标准价目表的价目表产品id=产品id+tenantId
                objectData.setId(productObj.getId() + tenantId);
                objectData.set(PriceBookConstants.ProductField.PRICEBOOKID.getApiName(), priceBookId);
                objectData.set(PriceBookConstants.ProductField.PRODUCTID.getApiName(), productObj.getId());
                fillDefaultObject(user, priceProductDescribe, objectData);
                if (StringUtils.isNotBlank(salesOrderDiscount)) {
                    objectData.set("discount", salesOrderDiscount);
                }
                addPriceProductList.add(objectData);
            }
            List<IObjectData> dataList = serviceFacade.bulkSaveObjectData(addPriceProductList, user);
            if (CollectionUtils.isEmpty(dataList) || addPriceProductList.size() != dataList.size()) {
                flag = false;
            }
            //底层的查询是有缓存的，同一线程同一查询参数返回的结果数据是一样的,所以此处在每次查询时，改变查询数据的页数据条数
            limit++;
        }
        log.info("fs-crm:initStandardPrice:count ,tenantId {},count {}", tenantId, count);
        return flag;
    }

    public IObjectData initStandardPriceBook(String tenantId) {
        User user = new User(tenantId, ProductEvent.SUPER_ADMIN_USER_ID);
        //处理对象
        IObjectDescribe objectDescribe = serviceFacade.findObject(user.getTenantId(), PriceBookConstants.API_NAME);
        IObjectData objectData = new ObjectData();
        objectData.set("name", "标准价目表");
        objectData.set(PriceBookConstants.Field.ACTIVESTATUS.getApiName(), PriceBookConstants.ActiveStatus.ON.getStatus());
        objectData.set("dept_range", Arrays.asList("999999"));
        objectData.set("account_range", "{\"type\":\"noCondition\",\"value\":\"ALL\"}");
        objectData.set("is_standard", "true");
        fillDefaultObject(user, objectDescribe, objectData);
        return serviceFacade.saveObjectData(user, objectData);
    }

    public String getSalesOrderDisount(String tenantId) {
        //获取订单产品的描述
        IObjectDescribe salesOrderProductDescribe = serviceFacade.findObject(tenantId, Utils.SALES_ORDER_PRODUCT_API_NAME);
        IFieldDescribe discountDescribe = salesOrderProductDescribe.getFieldDescribe("discount");
        Object discountDefault = discountDescribe.getDefaultValue();
        if (discountDefault != null && StringUtils.isNumeric(discountDefault.toString())) {
            return discountDefault.toString();
        }
        return null;
    }

    private void fillDefaultObject(User user, IObjectDescribe objectDescribe, IObjectData objectData) {
        //默认信息
        objectData.setTenantId(user.getTenantId());
        objectData.setCreatedBy(user.getUserId());
        objectData.setLastModifiedBy(user.getUserId());
        objectData.set(UdobjConstants.LIFE_STATUS_API_NAME, UdobjConstants.LIFE_STATUS_VALUE_NORMAL);
        objectData.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
        //锁定状态设置成,为锁定
        objectData.set("lock_status", 0);
        //业务类型
        objectData.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
        objectData.set(IObjectData.DESCRIBE_ID, objectDescribe.getId());
        objectData.set(IObjectData.DESCRIBE_API_NAME, objectDescribe.getApiName());
        objectData.set(IObjectData.PACKAGE, "CRM");
        objectData.set(IObjectData.VERSION, objectDescribe.getVersion());
        //相关团队
        ObjectDataExt objectDataExt = ObjectDataExt.of(objectData);
        TeamMember teamMember = new TeamMember(user.getUserId(), TeamMember.Role.OWNER, TeamMember.Permission.READANDWRITE);
        objectDataExt.addTeamMembers(Lists.newArrayList(teamMember));
        //其他相关字段信息
        if (!objectDescribe.getApiName().equals(PriceBookConstants.API_NAME_PRODUCT)) {
            return;
        }
        //其他相关字段信息的默认信息
        List<String> list = Collections.singletonList("discount");
        list.forEach(k -> {
            Object kVal = objectData.get(k);
            if (kVal == null || StringUtils.isBlank(kVal.toString())) {
                IFieldDescribe field = objectDescribe.getFieldDescribe(k);
                Object defaultVal = field.getDefaultValue();
                if (defaultVal != null && StringUtils.isNotBlank(defaultVal.toString())) {
                    objectData.set(k, defaultVal);
                }
            }
        });
    }
}
