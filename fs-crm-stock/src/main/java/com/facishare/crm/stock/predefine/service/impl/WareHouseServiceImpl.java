package com.facishare.crm.stock.predefine.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.facishare.crm.stock.predefine.manager.SaleOrderManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.crm.stock.predefine.service.dto.OrderWarehouseType;
import com.facishare.crm.stock.predefine.service.model.*;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.crm.stock.predefine.service.WareHouseService;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * Created by linchf on 2018/1/12.
 */
@Slf4j(topic = "stockAccess")
@Component
public class WareHouseServiceImpl implements WareHouseService {

    @Autowired
    private WareHouseManager warehouseManager;

    @Resource
    private StockManager stockManager;

    @Resource
    private SaleOrderManager saleOrderManager;

    @Override
    public WareHouseDetailModel.Result queryDownValid(ServiceContext serviceContext, WareHouseDetailModel.Arg arg) {
        WareHouseDetailModel.Result result = new WareHouseDetailModel.Result();

        String accountId = arg.getAccountId();
        String productId = arg.getProductId();
        User user = serviceContext.getUser();

        //参数校验
        Preconditions.checkNotNull(accountId);
        Preconditions.checkNotNull(productId);

        //查询适用仓库
        List<IObjectData> validList = warehouseManager.queryValidByAccountId(user, accountId, null);
        Map<String, List<WarehouseVO>> data = warehouseManager.queryDownValidByIds(user, Arrays.asList(productId), validList);
        result.setWarehouses(data.get(productId));
        return result;
    }

    @Override
    public QueryDownValidByIdsModel.Result queryDownValidByIds(ServiceContext serviceContext, QueryDownValidByIdsModel.Arg arg) {
        String accountId = arg.getAccountId();
        User user = serviceContext.getUser();
        List<String> productIds = arg.getProductIds();

        Preconditions.checkNotNull(accountId);
        if (CollectionUtils.isEmpty(productIds)) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "产品ID列表不能为空");
        }

        QueryDownValidByIdsModel.Result result = new QueryDownValidByIdsModel.Result();
        //查询适用仓库
        List<IObjectData> validList = warehouseManager.queryValidByAccountId(user, accountId, null);
        if (CollectionUtils.isEmpty(validList)) {
            return result;
        }

        Map<String, List<WarehouseVO>> data = warehouseManager.queryDownValidByIds(user, productIds, validList);
        log.debug("WareHouseServiceImpl.queryDownValidByIds.user[{}], arg[{}], result[{}]", user, arg, result);
        if (data.isEmpty()) {
            return result;
        }

        if (stockManager.isAllWarehouseOrder(user.getTenantId())) {
            result.setOrderWarehouseType(OrderWarehouseType.OrderWarehouseTypeEnum.ALL_WAREHOUSE.getStatus());
        } else {
            result.setOrderWarehouseType(OrderWarehouseType.OrderWarehouseTypeEnum.SINGLE_WAREHOUSE.getStatus());
        }
        result.setData(data);
        return result;
    }

    @Override
    public QueryUpValidWarehouseModel.Result queryUpValid(ServiceContext serviceContext, QueryUpValidWarehouseModel.Arg arg) {
        Preconditions.checkNotNull(arg.getAccountId());

        Map<String, Object> result = Maps.newHashMap();

        List<IObjectData> validList = warehouseManager.queryValidByAccountId(serviceContext.getUser(), arg.getAccountId(), null);

        Optional<IObjectData> defaultWarehouse = validList.stream().filter(warehouse -> Boolean.valueOf(warehouse.get(WarehouseConstants.Field.Is_Default.apiName).toString())).findFirst();

        if (defaultWarehouse.isPresent()) {
            result.put("defaultId", defaultWarehouse.get().getId());
            result.put("defaultName", defaultWarehouse.get().getName());
        } else {
            if (validList.size() == 1) {
                result.put("defaultId", validList.get(0).getId());
                result.put("defaultName", validList.get(0).getName());
            }
        }

        return new QueryUpValidWarehouseModel.Result(result);
    }

    @Override
    public QueryListWarehouseModel.Result queryList(ServiceContext serviceContext) {
        QueryListWarehouseModel.Result result = new QueryListWarehouseModel.Result();
        result.setWarehouseVOs(warehouseManager.queryList(serviceContext.getTenantId()));
        return result;
    }

    @Override
    public ModifyWarehouseNotRequiredModel.Result modifySalesOrderWarehouseNotRequired(ServiceContext serviceContext) {
        ModifyWarehouseNotRequiredModel.Result result = new ModifyWarehouseNotRequiredModel.Result();

        String tenantIds = ConfigCenter.ENABLE_STOCK_TENANT_IDS;
        if (Objects.equals(ConfigCenter.SUPPER_ADMIN_ID, serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            if (!StringUtils.isBlank(tenantIds)) {
                List<String> tenantIdList = Arrays.asList(tenantIds.split(";"));

                List<String> failedList = Lists.newArrayList();
                for (String tenantId : tenantIdList) {
                    try {
                        if (stockManager.isStockEnable(tenantId)) {
                            log.info("modifySalesOrderWarehouseNotRequired start. tenantId[{}]", tenantId);
                            saleOrderManager.modifySalesOrderWarehouseNotRequired(new User(tenantId, User.SUPPER_ADMIN_USER_ID));
                            log.info("modifySalesOrderWarehouseNotRequired end. tenantId[{}]", tenantId);
                        }
                    } catch (Exception e) {
                        log.warn("modifySalesOrderWarehouseNotRequired failed. tenantId[{}]", tenantId, e);
                        failedList.add(tenantId);
                    }
                }
                String failedLists = failedList.stream().collect(Collectors.joining(","));
                result.setResult("success. failedLists：" + failedLists);
            }
        } else {
            log.warn("modifySalesOrderWarehouseNotRequired failed. authorized failed. user[{}], tenantIds[{}]", serviceContext.getUser(), tenantIds);
            result.setResult("modifySalesOrderWarehouseNotRequired failed. authorized failed.");
        }
        return result;
    }
}
