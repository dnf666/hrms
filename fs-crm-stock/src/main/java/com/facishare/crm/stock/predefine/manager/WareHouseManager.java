package com.facishare.crm.stock.predefine.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.rest.dto.CheckBulkAddOrderModel;
import com.facishare.crm.rest.dto.QueryProductByIds;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.enums.WarehouseIsEnableEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.SimpleWarehouseVO;
import com.facishare.crm.stock.predefine.service.dto.StockViewType;
import com.facishare.crm.stock.predefine.service.model.WarehouseVO;
import com.facishare.crm.util.ObjectFieldConstantsUtil;
import com.facishare.crm.util.RangeVerify;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.ObjectAPINameMapping;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j(topic = "stockAccess")
@Service
public class WareHouseManager extends CommonManager{
    @Resource
    private ServiceFacade serviceFacade;

    @Resource
    private ProductManager productManager;

    @Resource
    private StockConfigManager stockConfigManager;

    @Resource
    private StockManager stockManager;

    private static final BigDecimal SAFETY_STOCK_IS_NULL = BigDecimal.valueOf(-10);

    public List<SimpleWarehouseVO> queryList(String tenantId) {
        List<IFilter> filters = Lists.newArrayList();

        //正常生命状态
        SearchUtil.fillFilterEq(filters, ObjectLifeStatus.LIFE_STATUS_API_NAME, ObjectLifeStatus.NORMAL.getCode());
        QueryResult<IObjectData> queryResult = searchQuery(new User(tenantId, User.SUPPER_ADMIN_USER_ID), WarehouseConstants.API_NAME, filters, Lists.newArrayList(), 0, 10000, 0);

        List<SimpleWarehouseVO> simpleWarehouseVOs = Lists.newArrayList();
        simpleWarehouseVOs = queryResult.getData().stream().map(warehouseObj -> SimpleWarehouseVO.builder().id(warehouseObj.getId()).name(warehouseObj.getName()).build()).collect(Collectors.toList());
        return simpleWarehouseVOs;
    }

    public List<IObjectData> queryTestList(String tenantId) {
        List<IFilter> filters = Lists.newArrayList();

        //正常生命状态
        SearchUtil.fillFilterEq(filters, ObjectLifeStatus.LIFE_STATUS_API_NAME, ObjectLifeStatus.NORMAL.getCode());
        QueryResult<IObjectData> queryResult = searchQuery(new User(tenantId, User.SUPPER_ADMIN_USER_ID), WarehouseConstants.API_NAME, filters, Lists.newArrayList(), 0, 10000, 0);
        return queryResult.getData();
    }

    public List<IObjectData> queryEnable(User user, SearchTemplateQuery query) {
        List<IFilter> filters = Lists.newArrayList();
        List orders = Lists.newArrayList();
        if (query != null) {
            if (query.getOffset() != 0) {
                return Lists.newArrayList();
            }
            filters = query.getFilters();
            orders = query.getOrders();
        }

        SearchUtil.fillFiltersWithUser(user, filters);
        //启用状态
        SearchUtil.fillFilterEq(filters, WarehouseConstants.Field.Is_Enable.getApiName(), WarehouseIsEnableEnum.ENABLE.value);
        //正常生命状态
        SearchUtil.fillFilterEq(filters, ObjectLifeStatus.LIFE_STATUS_API_NAME, ObjectLifeStatus.NORMAL.getCode());

        QueryResult<IObjectData> queryResult = searchQuery(user, WarehouseConstants.API_NAME, filters, orders, 0, 2000, 0);

        return queryResult.getData();
    }


    //根据客户id，查询适用仓库列表
    public List<IObjectData> queryValidByAccountId(User user, String accountId, SearchTemplateQuery query) {
        if (query != null) {
            if (query.getOffset() != 0) {
                return Lists.newArrayList();
            }
        }
        IObjectDescribe accountDescribe = serviceFacade.findObject(user.getTenantId(), ObjectAPINameMapping.Account.getApiName());

        IObjectData accountObjData = serviceFacade.findObjectData(user, accountId, ObjectAPINameMapping.Account.getApiName());
        if (accountObjData == null) {
            log.error("accountObjData is blank,tenantId {},accountId {}", user.getTenantId(), accountId);
            throw new ValidateException("当前客户不存在！");
        }


        List<IObjectData> dataList = queryEnable(user, query);
        List<IObjectData> filteredData = new ArrayList<>();
        Iterator iterator = dataList.iterator();
        while (iterator.hasNext()) {
            IObjectData objectData = (IObjectData) iterator.next();
            if (validateWarehouse(accountDescribe, accountObjData, objectData)) {
                filteredData.add(objectData);
            }
        }

        return filteredData;
    }

    private boolean validateWarehouse(IObjectDescribe accountDescribe, IObjectData accountObjData, IObjectData warehouseData) {
        // 根据当前客户的适用范围过滤符合条件的仓库
        Object customerRange = warehouseData.get(WarehouseConstants.Field.Account_range.apiName);
        if (Objects.nonNull(customerRange) && StringUtils.isNotBlank(customerRange.toString())) {
            JSONObject accountRangeObj = JSON.parseObject(customerRange.toString());
            if (accountRangeObj != null && !"noCondition".equals(accountRangeObj.get("type").toString())) {
                return RangeVerify.verifyConditions(accountDescribe, accountObjData, accountRangeObj.getJSONObject("value"));
            }
        }
        return true;
    }

    public void addBeforeCheck(String tenantId, IObjectData objectData) {
        if (Objects.equals(WarehouseIsEnableEnum.DISABLE.value, objectData.get(WarehouseConstants.Field.Is_Enable.apiName).toString())) {
            //新增的是停用的
            if (Objects.equals(Boolean.TRUE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "默认仓库不可停用");
            }
        }

        List<IObjectData> warehouses = queryList(tenantId, true, null);
        if (CollectionUtils.isEmpty(warehouses)) {
            //没有可用的仓库 需设置为默认仓库
            if (Objects.equals(Boolean.FALSE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "仓库列表暂无可用的默认仓库，请将此仓库设为默认仓库");
            }
        }
    }


    public void addAfterChange(User user, IObjectData objectData) {
        if (Objects.equals(Boolean.TRUE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())
                && Objects.equals(WarehouseIsEnableEnum.ENABLE.value, objectData.get(WarehouseConstants.Field.Is_Enable.apiName).toString())) {
            List<IFilter> filters = Lists.newArrayList();
            //未生效和正常状态数据
            SearchUtil.fillFilterIn(filters, ObjectLifeStatus.LIFE_STATUS_API_NAME, Arrays.asList(ObjectLifeStatus.NORMAL.getCode(), ObjectLifeStatus.INEFFECTIVE.getCode()));

            List<IObjectData> warehouses = queryList(user.getTenantId(), false, filters);
            warehouses.forEach(warehouse -> {
                if (Objects.equals(Boolean.TRUE.toString(), warehouse.get(WarehouseConstants.Field.Is_Default.apiName).toString())
                        && !Objects.equals(objectData.getId(), warehouse.getId())) {
                    warehouse.set(WarehouseConstants.Field.Is_Default.apiName, Boolean.FALSE.toString());
                    serviceFacade.updateObjectData(user, warehouse);
                }
            });
        }

    }

    public void editBefore(String tenantId, IObjectData objectData) {

        if (Objects.equals(WarehouseIsEnableEnum.DISABLE.value, objectData.get(WarehouseConstants.Field.Is_Enable.apiName).toString())) {
            //修改的是停用的
            if (Objects.equals(Boolean.TRUE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "默认仓库不可停用");
            }

            if (!CollectionUtils.isEmpty(queryStock(tenantId, objectData.getId()))) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "仓库已关联库存记录，不可停用");
            }
        }
        List<IObjectData> warehouses = queryList(tenantId, true, null);
        if (CollectionUtils.isEmpty(warehouses)) {
            //没有可用的仓库
            if (Objects.equals(Boolean.FALSE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "仓库列表暂无可用的默认仓库，请将此仓库设为默认仓库");
            }
        } else {
            if (Objects.equals(Boolean.FALSE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())) {
                List<String> defaultIds = warehouses.stream().filter(warehouse -> Objects.equals(Boolean.TRUE.toString(), warehouse.get(WarehouseConstants.Field.Is_Default.apiName).toString()))
                        .map(warehouse -> warehouse.get("_id").toString()).collect(Collectors.toList());
                if (defaultIds.contains(objectData.getId())) {
                    throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "必须存在一条默认仓库记录");
                }
            }
        }

    }

    public void editAfterChange(User user, IObjectData objectData) {
        if (Objects.equals(Boolean.TRUE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())
                && Objects.equals(WarehouseIsEnableEnum.ENABLE.value, objectData.get(WarehouseConstants.Field.Is_Enable.apiName).toString())) {
            List<IFilter> filters = Lists.newArrayList();
            //未生效和正常状态数据
            SearchUtil.fillFilterIn(filters, ObjectLifeStatus.LIFE_STATUS_API_NAME, Arrays.asList(ObjectLifeStatus.NORMAL.getCode(), ObjectLifeStatus.INEFFECTIVE.getCode()));

            List<IObjectData> warehouses = queryList(user.getTenantId(), false, filters);
            warehouses.forEach(warehouse -> {
                if (Objects.equals(Boolean.TRUE.toString(), warehouse.get(WarehouseConstants.Field.Is_Default.apiName).toString())
                        && !Objects.equals(objectData.getId(), warehouse.getId())) {
                    warehouse.set(WarehouseConstants.Field.Is_Default.apiName, Boolean.FALSE.toString());
                    serviceFacade.updateObjectData(user, warehouse);
                }
            });
        }
    }

    public void invalidBefore(String tenantId, IObjectData objectData) {
        if (!CollectionUtils.isEmpty(queryStock(tenantId, objectData.getId()))) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, objectData.getName() + "已关联库存记录，不能作废");
        }

        if (Objects.equals(WarehouseIsEnableEnum.ENABLE.value, objectData.get(WarehouseConstants.Field.Is_Enable.apiName).toString())) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, objectData.getName() + "未停用，" + "请先停用再作废");
        }

        if (Objects.equals(Boolean.TRUE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, objectData.getName() + "为默认仓库，" + "默认仓库不能作废");
        }
    }

    public void flowCompleteAfter(User user, IObjectData objectData) {
        if (Objects.equals(Boolean.TRUE.toString(), objectData.get(WarehouseConstants.Field.Is_Default.apiName).toString())
                && Objects.equals(WarehouseIsEnableEnum.ENABLE.value, objectData.get(WarehouseConstants.Field.Is_Enable.apiName).toString())) {
            List<IFilter> filters = Lists.newArrayList();
            //未生效和正常状态数据
            SearchUtil.fillFilterIn(filters, ObjectLifeStatus.LIFE_STATUS_API_NAME, Arrays.asList(ObjectLifeStatus.NORMAL.getCode(), ObjectLifeStatus.INEFFECTIVE.getCode()));

            List<IObjectData> warehouses = queryList(user.getTenantId(), false, filters);
            warehouses.forEach(warehouse -> {
                if (Objects.equals(Boolean.TRUE.toString(), warehouse.get(WarehouseConstants.Field.Is_Default.apiName).toString())
                        && !Objects.equals(objectData.getId(), warehouse.getId())) {
                    warehouse.set(WarehouseConstants.Field.Is_Default.apiName, Boolean.FALSE.toString());
                    serviceFacade.updateObjectData(user, warehouse);
                }
            });
        }
    }

    /**
     * 批量导入销售订单  校验仓库和客户
     * @param user 用户
     * @param args 参数
     * @return result
     */
    public CheckBulkAddOrderModel.Result checkWarehouseAndCustomerId(User user, List<CheckBulkAddOrderModel.Arg> args) {
        CheckBulkAddOrderModel.Result result = new CheckBulkAddOrderModel.Result();
        List<CheckBulkAddOrderModel.DetailResult> successResult = Lists.newArrayList();
        List<CheckBulkAddOrderModel.DetailResult> failedResult = Lists.newArrayList();

        IObjectDescribe accountDescribe = serviceFacade.findObject(user.getTenantId(), ObjectAPINameMapping.Account.getApiName());

        List<String> customerIds = args.stream().map(CheckBulkAddOrderModel.Arg::getCustomerId).distinct().collect(Collectors.toList());
        List<String> warehouseNames = args.stream().map(CheckBulkAddOrderModel.Arg::getWarehouseName).distinct().collect(Collectors.toList());

        //查出客户
        List<IObjectData> customers = serviceFacade.findObjectDataByIds(user.getTenantId(), customerIds, ObjectAPINameMapping.Account.getApiName());

        Map<String, IObjectData> customerMap = customers.stream().collect(Collectors.toMap(customer -> customer.getId(), customer -> customer));

        //查出仓库
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, WarehouseConstants.Field.Name.getApiName(), warehouseNames);
        List<IObjectData> warehouses = queryList(user.getTenantId(), true, filters);
        Map<String, IObjectData> warehouseMap = warehouses.stream().collect(Collectors.toMap(IObjectData::getName, warehouse -> warehouse));

        //校验客户和仓库是否适用
        args.forEach(arg -> {
            CheckBulkAddOrderModel.DetailResult detailResult = new CheckBulkAddOrderModel.DetailResult();
            detailResult.setTradeId(arg.getTradeId());
            detailResult.setId(arg.getId());
            detailResult.setErrCode(StockErrorCode.BUSINESS_ERROR.getStringCode());
            detailResult.setIsSalesOrderFail(true);
            detailResult.setTradeProductId(arg.getTradeProductId());

            if (customerMap.get(arg.getCustomerId()) == null) {
                log.warn("checkWarehouseAndCustomerId. customer unavailable. customerId[{}]", arg.getCustomerId());
                detailResult.setErrMessage("客户不存在，id:" + arg.getCustomerId());
                failedResult.add(detailResult);
                return;
            }
            //校验仓库是否存在
            if (warehouseMap.get(arg.getWarehouseName()) == null) {
                log.warn("checkWarehouseAndCustomerId. warehouse unavailable. warehouseName[{}]", arg.getWarehouseName());
                detailResult.setErrMessage("仓库不存在，仓库名:" + arg.getWarehouseName());
                failedResult.add(detailResult);
                return;
            }

            if (!validateWarehouse(accountDescribe, customerMap.get(arg.getCustomerId()), warehouseMap.get(arg.getWarehouseName()))) {
                log.warn("checkWarehouseAndCustomerId. customer and warehouse unsatisfied. customerId[{}], warehouseName[{}]", arg.getCustomerId(), arg.getWarehouseName());
                detailResult.setErrMessage("客户不适用该仓库");
                failedResult.add(detailResult);
                return;
            }
            detailResult.setErrCode(StockErrorCode.OK.getStringCode());
            detailResult.setIsSalesOrderFail(false);
            detailResult.setWarehouseId(warehouseMap.get(arg.getWarehouseName()).getId());
            successResult.add(detailResult);
        });

        result.setFailedResult(failedResult);
        result.setSuccessResult(successResult);
        return result;
    }

    /**
     * 批量导入退货单 校验仓库
     * @param user 用户
     * @param args 参数
     * @return result
     */
    public CheckBulkAddOrderModel.Result checkWarehouse(User user, List<CheckBulkAddOrderModel.Arg> args) {
        CheckBulkAddOrderModel.Result result = new CheckBulkAddOrderModel.Result();
        List<CheckBulkAddOrderModel.DetailResult> successResult = Lists.newArrayList();
        List<CheckBulkAddOrderModel.DetailResult> failedResult = Lists.newArrayList();

        List<String> warehouseNames = args.stream().map(CheckBulkAddOrderModel.Arg::getWarehouseName).distinct().collect(Collectors.toList());
        //查出仓库
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, WarehouseConstants.Field.Name.getApiName(), warehouseNames);
        List<IObjectData> warehouses = queryList(user.getTenantId(), true, filters);
        Map<String, IObjectData> warehouseMap = warehouses.stream().collect(Collectors.toMap(IObjectData::getName, warehouse -> warehouse));

        args.forEach(arg -> {
            CheckBulkAddOrderModel.DetailResult detailResult = new CheckBulkAddOrderModel.DetailResult();
            detailResult.setDataId(arg.getTradeId());
            detailResult.setId(arg.getId());
            detailResult.setErrCode(StockErrorCode.BUSINESS_ERROR.getStringCode());
            detailResult.setIsReturnedGoodsInvoiceFail(true);

            //校验仓库是否存在
            if (warehouseMap.get(arg.getWarehouseName()) == null) {
                log.warn("checkWarehouse. warehouse unavailable. warehouseName[{}]", arg.getWarehouseName());
                detailResult.setErrMessage("仓库不存在，仓库名:" + arg.getWarehouseName());
                failedResult.add(detailResult);
                return;
            }

            detailResult.setErrCode(StockErrorCode.OK.getStringCode());
            detailResult.setIsReturnedGoodsInvoiceFail(false);
            detailResult.setWarehouseId(warehouseMap.get(arg.getWarehouseName()).getId());
            detailResult.setProductId(arg.getProductId());
            successResult.add(detailResult);
        });
        result.setSuccessResult(successResult);
        result.setFailedResult(failedResult);
        return result;
    }

    private List<IObjectData> queryList(String tenantId, boolean isValid, List<IFilter> filters) {
        if (filters == null) {
            filters = Lists.newArrayList();
        }

        if (isValid) {
            //启用状态
            SearchUtil.fillFilterEq(filters, WarehouseConstants.Field.Is_Enable.getApiName(), WarehouseIsEnableEnum.ENABLE.value);
            //正常生命状态
            SearchUtil.fillFilterEq(filters, ObjectLifeStatus.LIFE_STATUS_API_NAME, ObjectLifeStatus.NORMAL.getCode());
        }

        QueryResult<IObjectData> queryResult = searchQuery(new User(tenantId, User.SUPPER_ADMIN_USER_ID), WarehouseConstants.API_NAME, filters, Lists.newArrayList(), 0, 2000, 0);

        return queryResult.getData();
    }

    private List<IObjectData> queryStock(String tenantId, String warehouseId) {
        List<IFilter> filters = Lists.newArrayList();
        //启用状态
        SearchUtil.fillFilterEq(filters, StockConstants.Field.Warehouse.getApiName(), warehouseId);
        //正常生命状态
        SearchUtil.fillFilterEq(filters, ObjectLifeStatus.LIFE_STATUS_API_NAME, ObjectLifeStatus.NORMAL.getCode());

        QueryResult<IObjectData> queryResult = searchQuery(new User(tenantId, User.SUPPER_ADMIN_USER_ID), StockConstants.API_NAME, filters, Lists.newArrayList(), 0, 1, 0);

        return queryResult.getData();
    }

    /**
     * 查询订货通下游，在适用仓库中一批产品库存
     * @param user 用户
     * @param validList 适用仓库
     * @param productIds 产品列表
     * @return 仓库和库存信息
     */
    public Map<String, List<WarehouseVO>> queryDownValidByIds(User user, List<String> productIds, List<IObjectData> validList) {
        Map<String, List<WarehouseVO>> data = Maps.newHashMap();

        //查询适用仓库Id
        List<String> warehouseIds = validList.stream().map(warehouse -> warehouse.get("_id").toString()).collect(Collectors.toList());

        //查询库存显示类型：精确显示/模糊显示/不显示
        StockViewType.StockViewTypeEnum stockViewTypeEnum = stockConfigManager.getStockViewType(user.getTenantId());

        //查询安全库存
        List<QueryProductByIds.ProductVO> queryProductResult = productManager.queryProductByIds(user, productIds);

        //<产品id, 安全库存>
        Map<String, BigDecimal> productId2safetyStockMap = Maps.newHashMap();

        for (QueryProductByIds.ProductVO productVO : queryProductResult) {
            if (Objects.isNull(productVO.getSafetyStock())) {
                productId2safetyStockMap.put(productVO.getId(), SAFETY_STOCK_IS_NULL);
            } else {
                productId2safetyStockMap.put(productVO.getId(), productVO.getSafetyStock());
            }
        }

        //根据仓库编号和产品ID列表获取库存信息
        List<IObjectData> queryStockResult = stockManager.queryStocksByWarehouseIdsAndProductIds(user, productIds, warehouseIds);
        log.info("stockManager.queryStocksByWarehouseIdsAndProductIds.user[{}], productIds[{}], warehouseId[{}], result[{}]", user, productIds, warehouseIds, queryStockResult);

        if (CollectionUtils.isEmpty(queryStockResult)) {
            log.warn("stockManger.queryStockByWarehouseIds result is null, user[{}], productIds[{}], warehouseIds[{}]", user, productIds, warehouseIds);
            return data;
        }

        //查询是否合并仓库订货
        if (stockManager.isAllWarehouseOrder(user.getTenantId())) {
            //<产品Id, 所有适用仓库可用库存总和>
            Map<String, BigDecimal> productId2SumAvailableStock = stockManager.sumAvailableStockNum(queryStockResult);

            productId2SumAvailableStock.entrySet().forEach(entry -> {
                WarehouseVO warehouseVO = new WarehouseVO();
                List<WarehouseVO> wareHouseDetails = Lists.newArrayList();

                String productId = entry.getKey();
                warehouseVO.setAccurateNum(entry.getValue());

                if (StockViewType.StockViewTypeEnum.FUZZY.equals(stockViewTypeEnum)) {
                    String fuzzyDescription = stockManager.buildFuzzyDescription(entry.getValue(), productId2safetyStockMap.get(productId));
                    warehouseVO.setFuzzyDescription(fuzzyDescription);
                } else {
                    warehouseVO.setFuzzyDescription("");
                }

                wareHouseDetails.add(warehouseVO);
                data.put(productId, wareHouseDetails);
            });
            return data;
        } else {
            Map<String, IObjectData> warehouseId2warehouseMap = validList.stream().collect(Collectors.toMap(warehouse -> warehouse.get("_id").toString(), Function.identity()));

            //<产品Id, 适用仓库的产品库存>
            Map<String, List<IObjectData>> productId2Stocks = queryStockResult.stream().collect(Collectors.groupingBy(s -> s.get(StockConstants.Field.Product.apiName, String.class)));

            productId2Stocks.entrySet().forEach(entry -> {
                String productId = entry.getKey();
                List<WarehouseVO> wareHouseDetails = Lists.newArrayList();
                List<IObjectData> stockList = entry.getValue();
                if (!CollectionUtils.isEmpty(stockList)) {
                    stockList.forEach(stock -> {
                        WarehouseVO wareHouseDetail = new WarehouseVO();
                        String warehouseId = stock.get(StockConstants.Field.Warehouse.apiName, String.class);
                        wareHouseDetail.setId(warehouseId);
                        wareHouseDetail.setName(warehouseId2warehouseMap.get(warehouseId).get(WarehouseConstants.Field.Name.apiName).toString());
                        //判断是否是默认仓库
                        wareHouseDetail.setIsDefaultWarehouse(warehouseId2warehouseMap.get(warehouseId).get(WarehouseConstants.Field.Is_Default.apiName, Boolean.class));

                        BigDecimal availableStock = stock.get(StockConstants.Field.AvailableStock.apiName, BigDecimal.class);
                        wareHouseDetail.setAccurateNum(availableStock);

                        if (StockViewType.StockViewTypeEnum.FUZZY.equals(stockViewTypeEnum)) {
                            String fuzzyDescription = stockManager.buildFuzzyDescription(availableStock, productId2safetyStockMap.get(productId));
                            wareHouseDetail.setFuzzyDescription(fuzzyDescription);
                        } else {
                            wareHouseDetail.setFuzzyDescription("");
                        }
                        wareHouseDetails.add(wareHouseDetail);
                    });
                    data.put(productId, wareHouseDetails);
                }
            });
            return data;
        }
    }

    //检查客户是否适用仓库
    public void checkCustomerSatisfied(User user, String customerId, String warehouseId) {
        IObjectDescribe accountDescribe = serviceFacade.findObject(user.getTenantId(), ObjectAPINameMapping.Account.getApiName());
        IObjectData accountObjData = serviceFacade.findObjectData(user, customerId, ObjectAPINameMapping.Account.getApiName());
        IObjectData warehouseObjData = serviceFacade.findObjectData(user, warehouseId, WarehouseConstants.API_NAME);
        if (accountDescribe == null) {
            log.warn("checkCustomerSatisfied failed. user[{}], customerId[{}], warehouseId[{}]", user, customerId, warehouseId);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "客户对象描述不存在");
        }
        if (accountObjData == null) {
            log.warn("checkCustomerSatisfied failed. user[{}], customerId[{}], warehouseId[{}]", user, customerId, warehouseId);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "客户不存在");
        }
        if (warehouseObjData == null) {
            log.warn("checkCustomerSatisfied failed. user[{}], customerId[{}], warehouseId[{}]", user, customerId, warehouseId);
            throw new StockBusinessException(StockErrorCode.WAREHOUSE_UN_SATISFIED, "仓库不存在");
        }

        if (Objects.equals(WarehouseIsEnableEnum.DISABLE.value, warehouseObjData.get(WarehouseConstants.Field.Is_Enable.getApiName(), String.class))) {
            log.warn("checkCustomerSatisfied failed. user[{}], customerId[{}], warehouseId[{}]", user, customerId, warehouseId);
            throw new StockBusinessException(StockErrorCode.WAREHOUSE_UN_SATISFIED, "仓库已停用");
        }

        if (!validateWarehouse(accountDescribe, accountObjData, warehouseObjData)) {
            log.warn("salesOrder checkCustomerSatisfied failed. user[{}], customerId[{}], warehouseId[{}]", user, customerId, warehouseId);
            throw new StockBusinessException(StockErrorCode.WAREHOUSE_UN_SATISFIED, "客户不适用于该仓库");
        }
    }

    public void checkWarehouseEnable(User user, String warehouseId) {
        IObjectData warehouseObjData = serviceFacade.findObjectData(user, warehouseId, WarehouseConstants.API_NAME);
        if (warehouseObjData == null) {
            log.warn("checkWarehouseEnable failed. user[{}], warehouseId[{}]", user, warehouseId);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "仓库不存在");
        }

        if (Objects.equals(WarehouseIsEnableEnum.DISABLE.value, warehouseObjData.get(WarehouseConstants.Field.Is_Enable.getApiName(), String.class))) {
            log.warn("checkWarehouseEnable failed. user[{}], warehouseId[{}]", user, warehouseId);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "仓库已停用");
        }
    }

    public void modifyArg(String tenantId, BaseObjectSaveAction.Arg arg) {
        ObjectDataDocument objectData = arg.getObjectData();
        if (objectData == null) {
            throw new ValidateException("对象不能为空");
        }

        if (arg.getObjectData().get(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID) == null) {
            IObjectDescribe describe = serviceFacade.findObject(tenantId, WarehouseConstants.API_NAME);
            if (describe == null) {
                throw new ValidateException("查询不到对象");
            }
            arg.getObjectData().put(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID, describe.getId());
            arg.getObjectData().put(ObjectFieldConstantsUtil.FIELD_DESCRIBE_API_NAME, describe.getApiName());
        }
        if (arg.getObjectData().get(WarehouseConstants.Field.Account_range.apiName) == null) {
            arg.getObjectData().put(WarehouseConstants.Field.Account_range.apiName, "{\"type\":\"noCondition\",\"value\":\"ALL\"}");
        }
        arg.setObjectData(objectData);
    }
}

