package com.facishare.crm.stock.predefine.service.impl;

import com.facishare.common.proxy.helper.StringUtils;
import com.facishare.crm.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.constants.RequisitionNoteProductConstants;
import com.facishare.crm.manager.DeliveryNoteObjManager;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.FunctionProxy;
import com.facishare.crm.rest.dto.DelFuncModel;
import com.facishare.crm.rest.dto.QueryProductByIds;
import com.facishare.crm.rest.dto.SyncTenantSwitchModel;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.enums.StockTypeEnum;
import com.facishare.crm.stock.enums.WarehouseIsEnableEnum;
import com.facishare.crm.stock.enums.YesOrNoEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.predefine.manager.*;
import com.facishare.crm.stock.predefine.service.StockService;
import com.facishare.crm.stock.predefine.service.dto.*;
import com.facishare.crm.stock.predefine.service.model.*;
import com.facishare.crm.stock.task.ModifySalesOrderLayoutTask;
import com.facishare.crm.stock.util.CommonThreadPoolUtils;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.privilege.dto.AuthContext;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j(topic = "stockAccess")
@Component
public class StockServiceImpl implements StockService {

    @Resource
    private InitManager initManager;

    @Resource
    private StockManager stockManager;

    @Resource
    private StockConfigManager stockConfigManager;

    @Resource
    private WareHouseManager warehouseManager;

    @Resource
    private ProductManager productManager;

    @Resource
    private CrmRestApi crmRestApi;

    @Resource
    private DeliveryNoteObjManager deliveryNoteObjManager;

    @Resource
    private FunctionProxy functionProxy;

    private static final BigDecimal SAFETY_STOCK_IS_NULL = BigDecimal.valueOf(-10);

    @Override
    public QueryByProductsIdModel.Result queryByProductsId(ServiceContext serviceContext, QueryByProductsIdModel.Arg arg) {
        //1、校验身份
        QueryByProductsIdModel.Result result = new QueryByProductsIdModel.Result();
        String accountId = arg.getAccountId();
        List<String> productIds = arg.getProductIds();

        Preconditions.checkNotNull(accountId);

        if (CollectionUtils.isEmpty(productIds)) {
            return result;
        }

        //2、查询用户适用的仓库列表
        QueryByProductsIdModel.Warehouse warehouse = new QueryByProductsIdModel.Warehouse();
        List<IObjectData> validWarehouses = warehouseManager.queryValidByAccountId(serviceContext.getUser(), accountId, null);
        if (CollectionUtils.isEmpty(validWarehouses)) {
            throw new StockBusinessException(StockErrorCode.WAREHOUSE_NOT_EXIST);
        }
        String warehouseId;
        String warehouseName;
        Optional<IObjectData> defaultWarehouseOpt = validWarehouses.stream().filter(objectData ->
                Objects.equals(WarehouseIsEnableEnum.ENABLE.value, objectData.get(WarehouseConstants.Field.Is_Enable.apiName, String.class))
                        && objectData.get(WarehouseConstants.Field.Is_Default.apiName, Boolean.class)).findFirst();

        //2.1、查找默认仓库
        if (defaultWarehouseOpt.isPresent()) {
            warehouseId = defaultWarehouseOpt.get().get("_id").toString();
            warehouseName = defaultWarehouseOpt.get().get(WarehouseConstants.Field.Name.apiName, String.class);
            warehouse.setId(warehouseId);
            warehouse.setName(warehouseName);
        } else {
            log.warn("User have not default Warehouse, user[{}]", serviceContext.getUser());
            //2.2、没有默认仓库返回适用仓库列表的第一个元素
            warehouseId = validWarehouses.get(0).get("_id").toString();
            warehouseName = validWarehouses.get(0).get(WarehouseConstants.Field.Name.apiName, String.class);
            warehouse.setId(warehouseId);
            warehouse.setName(warehouseName);
        }

        //3、根据仓库编号和产品ID获取库存信息
        List<IObjectData> queryProductsResult = stockManager.queryByWarehouseIdAndProductIds(serviceContext.getUser(), warehouseId, productIds);
        Map<String, BigDecimal> productId2Available = queryProductsResult.stream().
                collect(Collectors.toMap(e -> e.get(StockConstants.Field.Product.apiName, String.class),
                        e -> e.get(StockConstants.Field.AvailableStock.apiName, BigDecimal.class)));
        //4、查询库存显示规则
        StockViewType.StockViewTypeEnum stockViewTypeEnum = stockConfigManager.getStockViewType(serviceContext.getTenantId());
        List<QueryByProductsIdModel.ProductInfo> productInfos = buildProductInfoList(serviceContext.getUser(),
                productIds, productId2Available, stockViewTypeEnum);


        result.setStocks(productInfos);
        result.setWarehouse(warehouse);

        return result;
    }

    private List<QueryByProductsIdModel.ProductInfo> buildProductInfoList(User user, List<String> productIds,
                                                                          Map<String, BigDecimal> availableMap,
                                                                          StockViewType.StockViewTypeEnum type) {
        List<QueryByProductsIdModel.ProductInfo> result = Lists.newArrayList();

        if (CollectionUtils.isEmpty(productIds) || availableMap.isEmpty()) {
            return result;
        }

        for (String productId : productIds) {
            if (!availableMap.containsKey(productId)) {
                continue;
            }

            if (StockViewType.StockViewTypeEnum.NO.equals(type) ||
                    StockViewType.StockViewTypeEnum.ACCURATE.equals(type)) {
                //3.1、设置精确值
                result.add(new QueryByProductsIdModel.ProductInfo(productId, availableMap.get(productId), ""));
            } else if (StockViewType.StockViewTypeEnum.FUZZY.equals(type)) {
                //5、查询安全库存
                List<QueryProductByIds.ProductVO> queryProductResult = productManager.queryProductByIds(user, productIds);

                Map<String, BigDecimal> safetyStockMap =
                        queryProductResult.stream().map(productVO -> {
                            if (Objects.isNull(productVO.getSafetyStock())) {
                                productVO.setSafetyStock(SAFETY_STOCK_IS_NULL);
                            }
                            return productVO;
                        }).collect(Collectors.toMap(QueryProductByIds.ProductVO::getId, QueryProductByIds.ProductVO::getSafetyStock));

                result.add(new QueryByProductsIdModel.ProductInfo(productId, availableMap.get(productId),
                        stockManager.buildFuzzyDescription(availableMap.get(productId), safetyStockMap.get(productId))));
            }
        }

        return result;
    }

    @Override
    public SaveStockConfigModel.Result saveStockConfig(ServiceContext serviceContext, SaveStockConfigModel.Arg arg) {
        Preconditions.checkNotNull(arg.getStockViewType());
        Preconditions.checkNotNull(arg.getValidateOrderType());

        //1、是否是Crm管理员

        //2、校验库存是否开启
        Boolean hasSwitched = stockManager.isStockEnable(serviceContext.getUser().getTenantId());
        if (!hasSwitched) {
            throw new StockBusinessException(StockErrorCode.STOCK_NOT_ENABLE);
        }

        //3、保存库存设置
        SaveStockConfigModel.Result result = new SaveStockConfigModel.Result();
        result.setIsSuccess(true);

        if (!StringUtils.isBlank(arg.getValidateOrderType())) {
            stockConfigManager.updateOrderCheckType(serviceContext.getUser(),
                    OrderCheckType.OrderCheckTypeEnum.valueOf(Integer.valueOf(arg.getValidateOrderType())));
        }

        if (!StringUtils.isBlank(arg.getStockViewType())) {
            stockConfigManager.updateStockViewType(serviceContext.getUser(),
                    StockViewType.StockViewTypeEnum.valueOf(Integer.valueOf(arg.getStockViewType())));

        }

        if (!StringUtils.isBlank(arg.getOrderWarehouseType())) {
            stockConfigManager.updateOrderWarehouseType(serviceContext.getUser(),
                    OrderWarehouseType.OrderWarehouseTypeEnum.valueOf(Integer.valueOf(arg.getOrderWarehouseType())));
        }

        if (!StringUtils.isBlank(arg.getStockWarningType())) {
            stockConfigManager.updateStockWarningType(serviceContext.getUser(),
                    StockWarningType.StockWarningTypeEnum.valueOf(Integer.valueOf(arg.getStockWarningType())));
        }

        if (!StringUtils.isBlank(arg.getIsNotShowZeroStockType())) {
            stockConfigManager.updateIsNotShowZeroStock(serviceContext.getUser(),
                    YesOrNoEnum.valueOf(Integer.valueOf(arg.getIsNotShowZeroStockType())));
        }

        return result;
    }

    @Override
    public StockType.EnableStockResult enableStock(ServiceContext serviceContext) throws MetadataServiceException {
        String disableStockTenantIds = ConfigCenter.DISABLE_STOCK_TENANT_IDS;
        List<String> disableStockTenantIdList = Arrays.asList(disableStockTenantIds.split(";"));
        //临时关闭库存的企业 再次开启库存做拦截
        if (disableStockTenantIdList.contains(serviceContext.getTenantId())) {
            throw new StockBusinessException(StockErrorCode.INIT_ERROR, "库存模块已通过特殊渠道关闭，如需开启请联系纷享客服：4001869000。");
        }

        User user = serviceContext.getUser();

        StockType.EnableStockResult enableStockResult = new StockType.EnableStockResult();
        //1、是否crm管理员

        //2、ERP库存是否开启
        if (stockManager.isErpStockEnable(serviceContext.getUser())) {
            throw new StockBusinessException(StockErrorCode.INIT_ERROR, "ERP库存已经开启，不能开启纷享库存");
        }


        //3.获取企业配置信息
        if (stockManager.isStockEnable(serviceContext.getTenantId())) {
            throw new StockBusinessException(StockErrorCode.INIT_ERROR, "库存已经开启");
        }

        //4.校验对象重名
        Set<String> existDisplayNames = initManager.checkExistDisplayName(serviceContext.getTenantId());
        if (!CollectionUtils.isEmpty(existDisplayNames)) {
            String existDisplayNameString = StringUtils.join(existDisplayNames, "、");
            log.warn("init stock failed. existDisplayNames[{}]", existDisplayNameString);
            throw new StockBusinessException(StockErrorCode.INIT_ERROR, existDisplayNameString + "对象名称已存在");
        }

        boolean isSuccess = false;
        //对象初始化
        try {
            initManager.init(serviceContext.getUser());
            log.info("init stock describe success.");

            initManager.initOutboundDeliveryNote(serviceContext);

            //更新发货单字段
            deliveryNoteObjManager.addFieldForOpenStock(serviceContext.getUser());
            log.info("stock deliveryNote addField success.");

            //更新开关
            stockConfigManager.insertOrUpdateStockSwitch(serviceContext.getUser(), StockType.StockSwitchEnum.ENABLE, true);
            enableStockResult.setEnableStatus(StockType.StockSwitchEnum.ENABLE.getStatus());
            enableStockResult.setMessage("开启成功");
            isSuccess = true;
        } catch (Exception e) {
            //初始化失败 更新失败状态
            log.warn("stock init failed.");
            stockConfigManager.insertOrUpdateStockSwitch(serviceContext.getUser(), StockType.StockSwitchEnum.FAILED, true);
            throw e;
        } finally {
            //通知更新SFA老对象字段
            if (isSuccess) {
                log.info("stock notifySFA");
                notifySFA(serviceContext);
                //修改订单layout 订货仓库改为非必填
                ModifySalesOrderLayoutTask task = new ModifySalesOrderLayoutTask(user);
                CommonThreadPoolUtils.getScheduledExecutorService().schedule(task, 3000, TimeUnit.MILLISECONDS);
            }

        }

        return enableStockResult;
    }

    @Override
    public QueryStockConfigModel.Result queryStockConfig(ServiceContext serviceContext) {
        QueryStockConfigModel.Result result = new QueryStockConfigModel.Result();

        //获取库存开关 默认不能开启
        StockType.StockSwitchEnum stockSwitchEnum = stockConfigManager.getStockSwitch(serviceContext.getUser().getTenantId());
        switch (stockSwitchEnum) {
            case ENABLE: {
                result.setEnable(true);
            }
        }

        //获取订单检查配置  默认库存不足，不允许提交订单
        OrderCheckType.OrderCheckTypeEnum orderCheckTypeEnum = stockConfigManager.getOrderCheckType(serviceContext.getUser());
        result.setValidateOrderType(orderCheckTypeEnum.getStringStatus());

        //获取库存显示配置 默认不显示库存
        StockViewType.StockViewTypeEnum stockViewTypeEnum = stockConfigManager.getStockViewType(serviceContext.getTenantId());
        result.setStockViewType(stockViewTypeEnum.getStringStatus());

        //获取订货类型  默认单一仓库订货
        OrderWarehouseType.OrderWarehouseTypeEnum orderWarehouseTypeEnum = stockConfigManager.getOrderWarehouseType(serviceContext.getTenantId());
        result.setOrderWarehouseType(orderWarehouseTypeEnum.getStringStatus());

        //获取库存预警
        StockWarningType.StockWarningTypeEnum stockWarningTypeEnum = stockConfigManager.getStockWarningType(serviceContext.getTenantId());
        result.setStockWarningType(stockWarningTypeEnum.getStringStatus());

        //获取是否展示库存为0的数据
        YesOrNoEnum isNotShowZeroStockEnum = stockConfigManager.getIsNotShowZeroStock(serviceContext.getTenantId());
        result.setIsNotShowZeroStockType(isNotShowZeroStockEnum.getStringStatus());

        return result;
    }

    @Override
    public CleanStockFuncModel.Result cleanStockFunc(ServiceContext serviceContext, CleanStockFuncModel.Arg arg) {
        log.info("cleanStockFunc begin. serviceContext[{}], arg[{}]", serviceContext, arg);
        DelFuncModel.Arg funcArg = new DelFuncModel.Arg();
        funcArg.setAuthContext(AuthContext.builder().tenantId(arg.getTenantId()).userId(User.SUPPER_ADMIN_USER_ID).appId("CRM").build());

        List<String> objectApiNameList = Arrays.asList(WarehouseConstants.API_NAME, GoodsReceivedNoteConstants.API_NAME, GoodsReceivedNoteProductConstants.API_NAME, StockConstants.API_NAME);
        objectApiNameList.forEach(apiName -> {
            List<String> funcSet = getFuncList(apiName);
            funcArg.setFuncSet(funcSet);
            DelFuncModel.Result result = functionProxy.batchDelFunc(funcArg);
            log.info("batchDelFunc " + apiName + ". arg[{}], result[{}]", funcArg, result);
        });

        return new CleanStockFuncModel.Result();
    }

    @Override
    public IsStockEnableModel.Result isStockEnable(ServiceContext serviceContext) {
        IsStockEnableModel.Result result = new IsStockEnableModel.Result();
        result.setIsEnable(stockManager.isStockEnable(serviceContext.getTenantId()));
        return result;
    }

    @Override
    public QueryStockStatusModel.Result queryEnabledStockType(ServiceContext serviceContext) {
        QueryStockStatusModel.Result result = new QueryStockStatusModel.Result();

        if (stockManager.isStockEnable(serviceContext.getTenantId()) || deliveryNoteObjManager.isDeliveryNoteEnable(serviceContext.getTenantId())) {
            result.setStockType(StockTypeEnum.FS_ENABLED.value);
        } else if (stockManager.isErpStockEnable(serviceContext.getUser())) {
            result.setStockType(StockTypeEnum.ERP_ENABLED.value);
        }

        if (result.getStockType() == null) {
            result.setStockType(StockTypeEnum.ALL_DISABLED.value);
        }
        return result;
    }

    //    @Override
//    public CleanStockDescribeModel.Result cleanStockDescribe(ServiceContext serviceContext, CleanStockDescribeModel.Arg arg) {
//        log.info("cleanStockDescribe begin. serviceContext[{}], arg[{}]", serviceContext, arg);
//        List<String> objectApiNameList = Arrays.asList(WarehouseConstants.API_NAME, GoodsReceivedNoteConstants.API_NAME, GoodsReceivedNoteProductConstants.API_NAME, StockConstants.API_NAME);
//
//        objectApiNameList.forEach(apiName ->
//            describeLogicService.deleteDescribe(new User(arg.getTenantId(), User.SUPPER_ADMIN_USER_ID), apiName));
//
//        return new CleanStockDescribeModel.Result();
//    }

    private void notifySFA(ServiceContext serviceContext) {
        SyncTenantSwitchModel.Arg arg = new SyncTenantSwitchModel.Arg();
        arg.setKey("30");    //库存
        arg.setValue("1");   //1-true,0-false   0不好使。只能传1.因为不可停用。

        Map<String, String> headers = StockUtils.getHeaders(serviceContext.getTenantId(), serviceContext.getUser().getUserId());
        SyncTenantSwitchModel.Result crmResult = crmRestApi.syncTenantSwitch(arg, headers);
        log.info("sync stock status, arg:{}, headers:{}, result:{}", arg, headers, crmResult);
    }

    private List<String> getFuncList(String apiName) {
        return Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return apiName;
            } else {
                return apiName + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
    }

    @Override
    public QueryAvailableStocksModel.Result querySalesOrderProductAvailableStock(ServiceContext serviceContext, QueryAvailableStocksModel.Arg arg) {
        QueryAvailableStocksModel.Result result = new QueryAvailableStocksModel.Result();

        User user = serviceContext.getUser();
        Preconditions.checkNotNull(arg.getCustomerId());
        if (CollectionUtils.isEmpty(arg.getProductIds())) {
            return result;
        }

        //查询是否为单一仓库订货
        if (!stockManager.isAllWarehouseOrder(serviceContext.getTenantId())) {
            //获取订货仓库Id
            String warehouseId = arg.getWarehouseId();
            if (StringUtils.isBlank(warehouseId)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "订货仓库不能为空");
            }

            List<IObjectData> stockList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, arg.getProductIds());
            if (CollectionUtils.isEmpty(stockList)) {
                return result;
            }

            Map<String, BigDecimal> data = stockList.stream().collect(Collectors.toMap(s -> s.get(StockConstants.Field.Product.apiName, String.class),
                    s -> s.get(StockConstants.Field.AvailableStock.apiName, BigDecimal.class)));

            result.setData(data);
            return result;
        } else {
            //查询客户的适用仓库列表
            List<IObjectData> validList = warehouseManager.queryValidByAccountId(user, arg.getCustomerId(), null);
            if (org.apache.commons.collections.CollectionUtils.isEmpty(validList)) {
                return result;
            }

            //查询适用仓库Id
            List<String> warehouseIds = validList.stream().map(warehouse -> warehouse.get("_id").toString()).collect(Collectors.toList());

            //根据仓库编号和产品ID列表获取库存信息
            List<IObjectData> stockList = stockManager.queryStocksByWarehouseIdsAndProductIds(user, arg.getProductIds(), warehouseIds);
            log.info("stockManager.queryStocksByWarehouseIdsAndProductIds.user[{}], productIds[{}], warehouseId[{}], result[{}]", user, arg.getProductIds(), warehouseIds, stockList);

            if (CollectionUtils.isEmpty(stockList)) {
                return result;
            }
            // 获取产品的可用库存
            //<产品Id, 所有适用仓库可用库存总和>
            Map<String, BigDecimal> data = stockManager.sumAvailableStockNum(stockList);
            result.setData(data);
            return result;
        }
    }

    @Override
    public CheckStockWarningModel.Result checkStockWarning(CheckStockWarningModel.Arg arg) {
        log.info("stockService checkStockWarning start. tenantId[{}]", arg.getTenantId());
        stockManager.checkStockAndSetRemindRecord(arg.getTenantId());
        return new CheckStockWarningModel.Result();
    }

    @Override
    public IsShowStockWarningMenuModel.Result isShowStockWarningMenu(ServiceContext serviceContext) {
        IsShowStockWarningMenuModel.Result result = new IsShowStockWarningMenuModel.Result();
        result.setIsShow(stockManager.isShowStockWarningMenu(serviceContext.getUser()));
        return result;
    }

    @Override
    public CloseStockSwitchModel.Result closeStockSwitch(ServiceContext serviceContext, CloseStockSwitchModel.Arg arg) {
        log.info("closeStockSwitch begin. user[{}], arg[{}]", serviceContext.getUser(), arg);

        CloseStockSwitchModel.Result result = new CloseStockSwitchModel.Result();

        if (Objects.equals(ConfigCenter.SUPPER_ADMIN_ID, serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            stockConfigManager.insertOrUpdateStockSwitch(new User(arg.getTenantId(), User.SUPPER_ADMIN_USER_ID), StockType.StockSwitchEnum.UNABLE, false);
            log.info("closeStockSwitch success. arg[{}]", arg);
        } else {
            log.warn("closeStockSwitch failed. authorized failed. user[{}], arg[{}]", serviceContext.getUser(), arg);
            result.setResult("closeStockSwitch failed. authorized failed.");
        }
        return result;
    }

    @Override
    public ModifyStockModel.Result modifyStock(ServiceContext serviceContext, ModifyStockModel.Arg arg) {
        log.info("modifyStock begin. user[{}], arg[{}]", serviceContext.getUser(), arg);

        ModifyStockModel.Result result = new ModifyStockModel.Result();

        if (Objects.equals(ConfigCenter.SUPPER_ADMIN_ID, serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            if (StringUtils.isBlank(arg.getTenantId())) {
                result.setResult("修改失败，企业id不能为空");
                return result;
            }

            if (StringUtils.isBlank(arg.getStockId())) {
                result.setResult("修改失败，库存id不能为空");
                return result;
            }

            if (StringUtils.isBlank(arg.getModifyBlockedStock()) && StringUtils.isBlank(arg.getModifyRealStock())) {
                result.setResult("修改失败，修改冻结库存数量与修改实际库存数量不能均为空");
                return result;
            }
            try {
                stockManager.modifyStock(arg.getTenantId(), arg.getStockId(), arg.getModifyBlockedStock(), arg.getModifyRealStock());
            } catch (StockBusinessException e) {
                log.warn("modifyStock failed. arg[{}]", arg, e);
                result.setResult("修改失败，" + e.getMessage());
                return result;
            } catch (Exception ex) {
                log.warn("modifyStock failed. arg[{}]", arg, ex);
                result.setResult("修改失败，系统异常");
            }
        } else {
            log.warn("modifyStock failed. authorized failed. user[{}], arg[{}]", serviceContext.getUser(), arg);
            result.setResult("修改失败，身份验证失败");
        }
        return result;
    }

    @Override
    public AddOrUpdateFieldModel.Result updateField(ServiceContext serviceContext) {
        AddOrUpdateFieldModel.Result result = new AddOrUpdateFieldModel.Result();

        String enableStockTenantIds = ConfigCenter.ENABLE_STOCK_TENANT_IDS;

        if (StringUtils.isEmpty(enableStockTenantIds)) {
            return result;
        }

        if (!ConfigCenter.SUPPER_ADMIN_ID.equals(serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            log.warn("addFieldAndData fail. user[{}]", serviceContext.getUser());
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "you haven't permission");
        }

        List<String> tenantIds = Lists.newArrayList(enableStockTenantIds.split(";"));

        for (String tenantId : tenantIds) {
            log.info("--------------------- updateField tenantId:{}", tenantId);
            //校验是否开启库存
            if (stockManager.isStockEnable(tenantId)) {
                User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
                final List<String> apiNameList = Lists.newArrayList(StockConstants.API_NAME,
                        DeliveryNoteProductObjConstants.API_NAME, GoodsReceivedNoteProductConstants.API_NAME,
                        OutboundDeliveryNoteProductConstants.API_NAME, RequisitionNoteProductConstants.API_NAME);

                //仓库更新描述和layout
                try {
                    apiNameList.forEach(apiName -> stockManager.updateFields(user, apiName));
                    log.info("--------------------- updateField success! tenantId:{}", tenantId);
                } catch (Exception e) {
                    log.warn("stock updateField failed. tenantId[{}]", tenantId, e);
                }
            } else {
                log.info("--------------------- you should enable stock firstly. tenantId:{}", tenantId);
            }
        }
        return result;
    }

    @Override
    public QueryDescribeFieldModel.Result<Map<String, List<String>>> queryGoodsReceivedNoteDescribeField(ServiceContext serviceContext) {
        QueryDescribeFieldModel.Result<Map<String, List<String>>> result = new QueryDescribeFieldModel.Result<>();
        Map<String, List<String>> data = Maps.newHashMap();

        String enableStockTenantIds = ConfigCenter.ENABLE_STOCK_TENANT_IDS;
        final Set<String> requiredFieldSet = Sets.newHashSet(GoodsReceivedNoteConstants.Field.Name.apiName,
                GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName, GoodsReceivedNoteConstants.Field.GoodsReceivedDate.apiName,
                GoodsReceivedNoteConstants.Field.Warehouse.apiName, "life_status", "owner", "tenant_id", "object_describe_id", "object_describe_api_name");

        if (StringUtils.isEmpty(enableStockTenantIds)) {
            return result;
        }

        if (!ConfigCenter.SUPPER_ADMIN_ID.equals(serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            log.warn("queryGoodsReceivedNoteDescribeField fail. user[{}]", serviceContext.getUser());
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "you haven't permission");
        }

        List<String> tenantIds = Lists.newArrayList(enableStockTenantIds.split(";"));

        for (String tenantId : tenantIds) {
            log.info("--------------------- queryField tenantId:{}", tenantId);
            //校验是否开启库存
            if (stockManager.isStockEnable(tenantId)) {
                try {
                    IObjectDescribe objectDescribe = initManager.findObjectDescribeByTenantIdAndDescribeApiName(tenantId, GoodsReceivedNoteConstants.API_NAME);
                    List<String> fieldList = objectDescribe.getFieldDescribes().stream().filter(IFieldDescribe::isRequired).
                            filter(field -> !requiredFieldSet.contains(field.getApiName())).
                            map(IFieldDescribe::getLabel).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(fieldList))  {
                        data.put(tenantId, fieldList);
                    }
                    log.info("--------------------- queryField success! tenantId:{}", tenantId);
                } catch (Exception e) {
                    log.warn("--------------------- queryField exception! tenantId:{}", tenantId, e);
                }
            } else {
                log.info("--------------------- you should enable stock firstly. tenantId:{}", tenantId);
            }
        }
        result.setData(data);
        return result;
    }

}
