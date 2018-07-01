package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.constants.*;
import com.facishare.crm.describebuilder.FormFieldBuilder;
import com.facishare.crm.describebuilder.TableColumnBuilder;
import com.facishare.crm.rest.dto.QueryProductByIds;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.enums.FuzzyViewStockEnum;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.YesOrNoEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.predefine.service.dto.OrderCheckType;
import com.facishare.crm.stock.predefine.service.dto.OrderWarehouseType;
import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.crm.stock.predefine.service.model.CheckOrderModel;
import com.facishare.crm.stock.predefine.service.model.IsStockEnableModel;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.crm.stock.util.HttpUtil;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.common.util.StopWatch;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.TeamMember;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.describe.NumberFieldDescribe;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liangk
 */
@Service
@Slf4j(topic = "stockAccess")
public class StockManager extends CommonManager {
    @Resource
    private StockConfigManager stockConfigManager;

    @Resource
    private ProductManager productManager;

    @Resource
    private ServiceFacade serviceFacade;

    @Resource
    private InitManager initManager;

    @Resource
    private StockWarningJobManager stockWarningJobManager;

    @Resource
    private StockLogManager stockLogManager;

    @Resource
    private StockCalculateManager stockCalculateManager;

    public Boolean isShowStockWarningMenu(User user) {
        //查询库存是否开启
        if (!isStockEnable(user.getTenantId())) {
            return false;
        }
        if (!checkStockViewListPrivilege(user)) {
            return false;
        }
        return true;
    }


    public Boolean isNotShowZeroStock(String tenantId) {
        YesOrNoEnum yesOrNoEnum = stockConfigManager.getIsNotShowZeroStock(tenantId);
        switch (yesOrNoEnum) {
            case YES: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    /**
     * 判断Erp库存是否开启
     * @param user
     * @return
     */
    public Boolean isErpStockEnable(User user) {
        String checkErpStockEnableUrl = ConfigCenter.PAAS_FRAMEWORK_URL + "erp_stock_biz/service/is_erp_stock_enable";
        try {
            Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
            headers.put("Content-Type", "application/json");
            IsStockEnableModel.ResultVO resultVO = HttpUtil.post(checkErpStockEnableUrl, headers, null, IsStockEnableModel.ResultVO.class);
            if (resultVO != null && resultVO.getResult().getIsEnable()) {
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断库存是否开启
     *
     * @param tenantId 企业id
     * @return true:开启 false:未开启
     */
    public Boolean isStockEnable(String tenantId) {
        StockType.StockSwitchEnum stockSwitchEnum = stockConfigManager.getStockSwitch(tenantId);
        switch (stockSwitchEnum) {
            case ENABLE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public Boolean isAllWarehouseOrder(String tenantId) {
        OrderWarehouseType.OrderWarehouseTypeEnum orderWarehouseTypeEnum = stockConfigManager.getOrderWarehouseType(tenantId);
        switch (orderWarehouseTypeEnum) {
            case ALL_WAREHOUSE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public Boolean checkGoodsReceivedNoteAddPrivilege(User user) {
        return serviceFacade.funPrivilegeCheck(user, GoodsReceivedNoteConstants.API_NAME,
                ObjectAction.CREATE.getActionCode());
    }

    public Boolean checkGoodsReceivedNoteViewListPrivilege(User user) {
        return serviceFacade.funPrivilegeCheck(user, GoodsReceivedNoteConstants.API_NAME,
                ObjectAction.VIEW_LIST.getActionCode());
    }

    public Boolean checkStockViewListPrivilege(User user) {
        return serviceFacade.funPrivilegeCheck(user, StockConstants.API_NAME,
                ObjectAction.VIEW_LIST.getActionCode());
    }

    public List<String> queryGoodsSendingPersons(String tenantId) {
        return serviceFacade.queryRoleUsersByRoles(new User(tenantId, User.SUPPER_ADMIN_USER_ID), Arrays.asList(CommonConstants.GOODS_SENDING_PERSON_ROLE));
    }

    /**
     * 查询指定仓库和指定产品列表的库存信息。
     *
     * @param user 用户
     * @param wareHouseId 仓库id
     * @param productIds 产品id列表
     * @return 只返回仓库中包含产品列表中的产品详情
     */
    public List<IObjectData> queryByWarehouseIdAndProductIds(User user, String wareHouseId, List<String> productIds) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, StockConstants.Field.Warehouse.apiName, wareHouseId);
        SearchUtil.fillFilterIn(filters, StockConstants.Field.Product.apiName, productIds);
        QueryResult<IObjectData> queryResult = searchQuery(user, StockConstants.API_NAME, filters, Lists.newArrayList(), 0, 10000, 0);
        return queryResult.getData();
    }

    /**
     * 合并产品可用库存数量
     * @param stocks
     * @return
     */
    public Map<String, BigDecimal> sumAvailableStockNum(List<IObjectData> stocks) {
        Map<String, BigDecimal> productStockNumMap = new HashMap<>();

        if (CollectionUtils.isEmpty(stocks)) {
            return productStockNumMap;
        }
        stocks.forEach(stock -> {
            String productId = stock.get(StockConstants.Field.Product.apiName, String.class);
            BigDecimal productAvailableStockNum = new BigDecimal(stock.get(StockConstants.Field.AvailableStock.apiName, String.class));

            if (productStockNumMap.get(productId) != null) {
                productStockNumMap.put(productId, productStockNumMap.get(productId).add(productAvailableStockNum));
            } else {
                productStockNumMap.put(productId, productAvailableStockNum);
            }
        });
        return productStockNumMap;
    }


    /**
     * 查询指定产品和仓库列表的库存信息
     *
     * @param user 用户
     * @param productIds 产品id列表
     * @param warehouseIds 仓库列表
     * @return 返回库存信息
     */
    public List<IObjectData> queryStocksByWarehouseIdsAndProductIds(User user, List<String> productIds, List<String> warehouseIds) {

        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, StockConstants.Field.Warehouse.apiName, warehouseIds);
        SearchUtil.fillFilterIn(filters, StockConstants.Field.Product.apiName, productIds);
        QueryResult<IObjectData> queryResult = searchQuery(user, StockConstants.API_NAME, filters, Lists.newArrayList(), 0, 10000, 0);
        return queryResult.getData();
    }

    public List<IObjectData> queryStocksByTenantId(String tenantId, List<IFilter> filters, List<OrderBy> orders) {
        if (filters == null) {
            filters = Lists.newArrayList();
        }
        if (orders == null) {
            orders = Lists.newArrayList();
        }
        SearchUtil.fillFilterEq(filters, SystemConstants.Field.TennantID.apiName, tenantId);
        QueryResult<IObjectData> queryResult = searchQuery(new User(tenantId, User.SUPPER_ADMIN_USER_ID), StockConstants.API_NAME, filters, orders, 0, 10000, 0);
        return queryResult.getData();
    }


    public IObjectData buildStock(User user, String warehouseId, String productId, String amount) {
        return buildStockByStockAmount(user, warehouseId, productId, amount, "0", amount);
    }

    public IObjectData buildStockByStockAmount(User user, String warehouseId, String productId, String realStock, String blockedStock, String availableStock) {
        IObjectDescribe stockDescribe = serviceFacade.findObject(user.getTenantId(), StockConstants.API_NAME);

        IObjectData stock = new ObjectData();
        stock.set(StockConstants.Field.Product.apiName, productId);
        stock.set(StockConstants.Field.Warehouse.apiName, warehouseId);
        stock.set(StockConstants.Field.RealStock.apiName, realStock);
        stock.set(StockConstants.Field.AvailableStock.apiName, availableStock);
        stock.set(StockConstants.Field.BlockedStock.apiName, blockedStock);

        stock.setTenantId(user.getTenantId());
        stock.setCreatedBy(user.getUserId());
        stock.setLastModifiedBy(user.getUserId());
        stock.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
        stock.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
        stock.set(IObjectData.DESCRIBE_ID, stockDescribe.getId());
        stock.set(IObjectData.DESCRIBE_API_NAME, StockConstants.API_NAME);
        stock.set(IObjectData.PACKAGE, "CRM");
        stock.set(IObjectData.VERSION, stockDescribe.getVersion());

        //相关团队
        ObjectDataExt objectDataExt = ObjectDataExt.of(stock);
        TeamMember teamMember = new TeamMember(user.getUserId(), TeamMember.Role.OWNER, TeamMember.Permission.READANDWRITE);
        objectDataExt.setTeamMembers(Lists.newArrayList(teamMember));

        return objectDataExt.getObjectData();
    }

    //查询可用库存  查询产品信息
    public void checkAvailableStock(User user, List<IObjectData> stocks, Map<String, BigDecimal> productId2AmountMap, String warehouseId) {
        Map<String, BigDecimal> productId2AvailableStockMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName, String.class),
                stock -> stock.get(StockConstants.Field.AvailableStock.apiName, BigDecimal.class)));

        List<QueryProductByIds.ProductVO> productVOS = productManager.queryProductByIds(user, new ArrayList<>(productId2AmountMap.keySet()));
        if (CollectionUtils.isEmpty(productVOS)) {
            log.warn("checkAvailableStock failed, productIdList[{}] not exist", productId2AmountMap.keySet());
        }

        Map<String, QueryProductByIds.ProductVO> productId2ProductVOMap = Maps.newHashMap();
        if (!Objects.isNull(productVOS)) {
            productId2ProductVOMap = productVOS.stream().collect(Collectors.toMap(QueryProductByIds.ProductVO::getId, productVO -> productVO));
        }


        log.info("checkAvailableStock, warehouseId[{}], productId2AvailableMap[{}], productId2AmountMap[{}]", warehouseId, productId2AvailableStockMap, productId2AmountMap);

        String productName;
        for (String productId : productId2AmountMap.keySet()){
            productName = CollectionUtils.isEmpty(productId2ProductVOMap) ? "" : productId2ProductVOMap.get(productId).getProductName();
            //1、校验仓库是否包含全部订单产品
            if (!productId2AvailableStockMap.containsKey(productId)) {
                log.warn("stock haven't the product[{}]. user[{}], wareHouseId[{}]", productId, user, warehouseId);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "所选仓库没有" + productName + "产品");
            }

            //2、校验可用库存是否大于产品数量
            Boolean checkAvailable = productId2AmountMap.get(productId).compareTo(productId2AvailableStockMap.get(productId)) > 0;
            if (checkAvailable) {
                log.warn("the stock of product[{}] not enough. user[{}], wareHouseId[{}]", productId, user, warehouseId);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, productName + "产品可用库存不足");
            }
        }
    }

    //批量操作
    public void batchCheckAvailableStock(User user, List<IObjectData> stocks, Map<String, Map<String, BigDecimal>> warehouseProductAmountMap, List<String> productIds, boolean isSalesOrder) {
        if (isSalesOrder) {
            //库存不足也可提交订单
            OrderCheckType.OrderCheckTypeEnum orderCheckTypeEnum = stockConfigManager.getOrderCheckType(user);
            Boolean canSubmit = orderCheckTypeEnum.equals(OrderCheckType.OrderCheckTypeEnum.CANSUBMIT);
            if (canSubmit) {
                return;
            }
        }

        List<QueryProductByIds.ProductVO> productVOs = productManager.queryProductByIds(user, productIds);
        Map<String, QueryProductByIds.ProductVO> productVOMap = Maps.newHashMap();
        if (!Objects.isNull(productVOs)) {
            productVOMap = productVOs.stream().collect(Collectors.toMap(QueryProductByIds.ProductVO::getId, productVO -> productVO));
        }

        Map<String, List<IObjectData>> warehouseStockMap = stocks.stream().collect(Collectors.groupingBy(stock -> stock.get(StockConstants.Field.Warehouse.apiName, String.class)));

        for (String warehouseId : warehouseProductAmountMap.keySet()) {
            List<IObjectData> productStockList = warehouseStockMap.get(warehouseId);

            if (CollectionUtils.isEmpty(productStockList)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "产品可用库存不足");
            }

            Map<String, BigDecimal> productAmountMap = productStockList.stream().collect(Collectors.toMap(
                    stock -> stock.get(StockConstants.Field.Product.apiName, String.class),
                    stock -> stock.get(StockConstants.Field.AvailableStock.apiName, BigDecimal.class)));

            Map<String, BigDecimal> needProductAmountMap = warehouseProductAmountMap.get(warehouseId);

            for (String productId : needProductAmountMap.keySet()) {
                if (needProductAmountMap.get(productId).compareTo(productAmountMap.get(productId)) > 0) {
                    String productName = productVOMap.get(productId) != null ? productVOMap.get(productId).getProductName() : "";
                    throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, productName + "产品可用库存不足");
                }
            }
        }
    }


    //校验可用库存 自带产品信息
    public void checkAvailableStockWithProducts(User user, List<IObjectData> stocks, List<CheckOrderModel.CheckProduct> checkProducts, String warehouseId, boolean isSaleOrder, boolean isAllWarehouseOrder) {
        Map<String, BigDecimal> stockProductsMap;

        if (isAllWarehouseOrder) {
            stockProductsMap = sumAvailableStockNum(stocks);
        } else {
            stockProductsMap = stocks.stream().collect(Collectors.toMap(
                    stock -> stock.get(StockConstants.Field.Product.apiName, String.class),
                    stock -> stock.get(StockConstants.Field.AvailableStock.apiName, BigDecimal.class)));
        }

        OrderCheckType.OrderCheckTypeEnum orderCheckTypeEnum = stockConfigManager.getOrderCheckType(user);
        Boolean isPermit = orderCheckTypeEnum.equals(OrderCheckType.OrderCheckTypeEnum.CANSUBMIT);

        OrderWarehouseType.OrderWarehouseTypeEnum orderWarehouseTypeEnum = stockConfigManager.getOrderWarehouseType(user.getTenantId());

        checkProducts.forEach(product -> {
            //2.2、若库存不足不允许提交订单，则校验可用库存是否大于产品数量
            if (isSaleOrder) {
                if (!isPermit) {
                    if (!isAllWarehouseOrder) {
                        //校验仓库是否包含全部订单产品
                        if (!stockProductsMap.containsKey(product.getProductId())) {
                            log.warn("stock haven't the product[{}]. user[{}], wareHouseId[{}]",
                                    product, user, warehouseId);
                            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "所选仓库没有" + product.getProductName() + "产品" );
                        }
                    }
                    if (stockProductsMap.get(product.getProductId()) == null) {
                        log.warn("the stock of product[{}] not enough. user[{}], wareHouseId[{}]",
                                product, user, warehouseId);
                        throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, product.getProductName() + "产品可用库存不足");
                    }
                    Boolean isSatisfy = product.getProductNum().compareTo(stockProductsMap.get(product.getProductId())) <= 0;
                    if (!isSatisfy) {
                        log.warn("the stock of product[{}] not enough. user[{}], wareHouseId[{}]",
                                product, user, warehouseId);
                        throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, product.getProductName() + "产品可用库存不足");
                    }
                }
            } else {
                Boolean isSatisfy = product.getProductNum().compareTo(stockProductsMap.get(product.getProductId())) <= 0;
                if (!isSatisfy) {
                    log.warn("the stock of product[{}] not enough. user[{}], wareHouseId[{}]",
                            product,user, warehouseId);
                    throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, product.getProductName() + "产品可用库存不足");
                }
            }
        });

    }

    public boolean checkRealStock(User user, List<IObjectData> stocks, Map<String, BigDecimal> productAmountMap) {
        Map<String, BigDecimal> stockProductsMap = stocks.stream().collect(Collectors.toMap(
                stock -> stock.get(StockConstants.Field.Product.apiName, String.class),
                stock -> stock.get(StockConstants.Field.RealStock.apiName, BigDecimal.class)));

        return productAmountMap.keySet().stream().allMatch(productId -> productAmountMap.get(productId).compareTo(stockProductsMap.get(productId)) <= 0);
    }

    public boolean checkRealStockWithProducts(User user, List<IObjectData> stocks, List<CheckOrderModel.CheckProduct> checkProducts) {
        Map<String, BigDecimal> stockProductsMap = stocks.stream().collect(Collectors.toMap(
                stock -> stock.get(StockConstants.Field.Product.apiName, String.class),
                stock -> stock.get(StockConstants.Field.RealStock.apiName, BigDecimal.class)));

        return checkProducts.stream().allMatch(product -> product.getProductNum().compareTo(stockProductsMap.get(product.getProductId())) <= 0);
    }

    //补充安全库存字段
    public List<ObjectDataDocument> fillSafetyStock(User user, List<ObjectDataDocument> stocks) {

        if (!CollectionUtils.isEmpty(stocks)) {
            List<String> productIds = stocks.stream().map(stock ->
                    stock.toObjectData().get(StockConstants.Field.Product.apiName).toString()).distinct().collect(Collectors.toList());

            List<QueryProductByIds.ProductVO> productVOs = productManager.queryProductByIds(new User(user.getTenantId(), User.SUPPER_ADMIN_USER_ID), productIds);

            Map<String, BigDecimal> productSafetyStockMap = new HashMap<>();

            productVOs.forEach(productVO -> {
                if (productVO.getSafetyStock() != null) {
                    productSafetyStockMap.put(productVO.getId(), productVO.getSafetyStock());
                }
            });

            List<ObjectDataDocument> result = Lists.newArrayList();

            stocks.forEach(stock -> {
                IObjectData objectData = stock.toObjectData();
                String productId = objectData.get(StockConstants.Field.Product.apiName).toString();
                if (productSafetyStockMap.get(productId) != null) {
                    objectData.set(StockConstants.Field.SafetyStock.apiName, productSafetyStockMap.get(productId));
                }
                result.add(ObjectDataDocument.of(objectData));
            });
            return result;
        }
        return stocks;
    }

    public String buildFuzzyDescription(BigDecimal availableStock, BigDecimal safetyStock) {
        FuzzyViewStockEnum fuzzyViewStockEnum;
        if (safetyStock.compareTo(BigDecimal.ZERO) >= 0) {
            if (availableStock.compareTo(safetyStock) >= 0) {
                fuzzyViewStockEnum = FuzzyViewStockEnum.ENOUGH;
            } else if (availableStock.compareTo(safetyStock) < 0 && availableStock.compareTo(BigDecimal.ZERO) > 0) {
                fuzzyViewStockEnum = FuzzyViewStockEnum.SMALLAMOUNT;
            } else {
                fuzzyViewStockEnum = FuzzyViewStockEnum.STOCKOUT;
            }
        } else {
            if (availableStock.compareTo(BigDecimal.ZERO) > 0) {
                fuzzyViewStockEnum = FuzzyViewStockEnum.ENOUGH;
            } else {
                fuzzyViewStockEnum = FuzzyViewStockEnum.STOCKOUT;
            }
        }
        return fuzzyViewStockEnum.getValue();
    }

    public List<IObjectData> batchUpdate(User user, List<IObjectData> newStocks, List<StockLogDO> stockLogDOList) {
        //更新库存前 保存未生效的库存操作日志
        List<String> stockLogIds = stockLogManager.bulkSave(stockLogDOList);
        stockLogDOList = stockLogManager.queryByIds(stockLogIds);

        //更新库存
        List<IObjectData> stocks = serviceFacade.batchUpdate(newStocks, user);

        //更新库存成功后 更新库存操作日志状态
        stockLogManager.bulkUpdate(stockLogDOList);
        return stocks;
    }

    public List<IObjectData> bulkSave(User user, List<IObjectData> newStocks, List<StockLogDO> stockLogDOList) {
        //插入库存前 保存未生效的库存操作日志
        List<String> stockLogIds = stockLogManager.bulkSave(stockLogDOList);
        stockLogDOList = stockLogManager.queryByIds(stockLogIds);

        //插入库存
        List<IObjectData> stocks = serviceFacade.bulkSaveObjectData(newStocks, user);

        //插入库存成功后，更新库存操作日志状态和库存id
        Map<String, String> productStockIdMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName, String.class), stock -> stock.getId()));
        stockLogDOList.forEach(stockLogDO -> stockLogDO.setStockId(productStockIdMap.get(stockLogDO.getProductId())));
        stockLogManager.bulkUpdate(stockLogDOList);
        return stocks;
    }

    //校验库存并发送告警消息
    public void checkStockAndSetRemindRecord(String tenantId) {
        List<IObjectData> notEnoughStocks = queryStocksNotEnough(tenantId, null, null);
        if (!CollectionUtils.isEmpty(notEnoughStocks)) {
            List<String> targetIds = queryGoodsSendingPersons(tenantId);
            //设置飘数
            stockWarningJobManager.setRecordRemind(new User(tenantId, User.SUPPER_ADMIN_USER_ID), notEnoughStocks.size(), targetIds);
        }
    }

    public List<IObjectData> queryStocksNotEnough(String tenantId, List<IFilter> filters, List<OrderBy> orders) {
        StopWatch stopWatch = StopWatch.create("StockManager queryStockNotEnough:" + tenantId);
        List<IObjectData> stocks = queryStocksByTenantId(tenantId, filters, orders);
        List<String> productIds = stocks.stream().map(stock -> stock.get(StockConstants.Field.Product.apiName, String.class)).distinct().collect(Collectors.toList());
        List<IObjectData> notEnoughStocks = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(productIds)) {
            List<QueryProductByIds.ProductVO> productVOs = productManager.queryProductByIds(new User(tenantId, User.SUPPER_ADMIN_USER_ID), productIds);

            Map<String, BigDecimal> productSafetyStockMap = productVOs.stream().collect(Collectors.toMap(QueryProductByIds.ProductVO::getId, QueryProductByIds.ProductVO::getSafetyStock));
            if (CollectionUtils.isEmpty(stocks)) {
                return Lists.newArrayList();
            }

            stocks.forEach(stock -> {
                BigDecimal productSafetyStock = productSafetyStockMap.get(stock.get(StockConstants.Field.Product.apiName, String.class));
                if (productSafetyStock == null) {
                    productSafetyStock = BigDecimal.ZERO;
                }
                if (productSafetyStock.compareTo(stock.get(StockConstants.Field.AvailableStock.apiName, BigDecimal.class)) > 0) {
                    stock.set(StockConstants.Field.SafetyStock.apiName, String.valueOf(productSafetyStock));
                    notEnoughStocks.add(stock);
                }
            });
            stopWatch.log();
        }

        return notEnoughStocks;
    }

    public void modifyStock(String tenantId, String stockId, String modifyBlockedStock, String modifyRealStock) {
        User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
        IObjectData oldStock = findById(user, stockId, StockConstants.API_NAME);
        if (oldStock == null) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存记录不存在");
        }

        StockLogDO stockLogDO = StockLogDO.builder().stockId(oldStock.getId()).tenantId(oldStock.getTenantId()).userId(User.SUPPER_ADMIN_USER_ID)
                .productId(oldStock.get(StockConstants.Field.Product.apiName, String.class))
                .warehouseId(oldStock.get(StockConstants.Field.Warehouse.apiName, String.class)).build();

        IObjectData newStock = null;
        if (!StringUtils.isBlank(modifyBlockedStock)) {
            BigDecimal modifyBlockedStockNum = new BigDecimal(modifyBlockedStock);
            newStock = stockCalculateManager.addBlocked(user, oldStock, modifyBlockedStockNum);
            stockLogDO.setModifiedBlockedStockNum(modifyBlockedStock);
        }

        if (!StringUtils.isBlank(modifyRealStock)) {
            BigDecimal modifyRealStockNum = new BigDecimal(modifyRealStock);
            newStock = stockCalculateManager.addReal(user, oldStock, modifyRealStockNum);
            stockLogDO.setModifiedRealStockNum(modifyRealStock);
        }

        if (newStock != null) {
            stockLogDO.setAfterRealStock(newStock.get(StockConstants.Field.RealStock.apiName, String.class));
            stockLogDO.setAfterBlockedStock(newStock.get(StockConstants.Field.BlockedStock.apiName, String.class));
            stockLogDO.setModifiedTime(System.currentTimeMillis());
            stockLogDO.setOperateObjectType(StockOperateObjectTypeEnum.MANUAL_MODIFICATION.value);
            batchUpdate(user, Arrays.asList(newStock), Arrays.asList(stockLogDO));
        }
    }



/*    public void updateStockDescribeAndLayout(User user) {
        String apiName = StockConstants.API_NAME;

        initManager.updateObjectDescribe(user, apiName);
        initManager.updateObjectLayout(user, apiName, true, true);
    }*/

    public void addFieldDescribeAndLayout(User user) {
        String apiName = StockConstants.API_NAME;
        String tenantId = user.getTenantId();

        //添加库存describe、layout
        List<String> fieldApiNames = new ArrayList<>();
        fieldApiNames.add(StockConstants.Field.Category.apiName);
        fieldApiNames.add(StockConstants.Field.Product_Status.apiName);

        //查询describe
        IObjectDescribe objectDescribe = initManager.findObjectDescribeByTenantIdAndDescribeApiName(tenantId, apiName);

        //添加字段（添加之前，判断是否已存在对应的字段）
        initManager.addFieldDescribes(objectDescribe, apiName, fieldApiNames);

        initManager.addFieldLayouts(tenantId, apiName, fieldApiNames, true, true);

        updateStockDescribeConfig(user);
    }

    /**
     * 获取FormFields
     */
    public List<IFormField> getFormFields() {
        //基本信息
        List<IFormField> formFields = Lists.newArrayList();
        boolean readOnly = false;
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Name.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Product.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Product_Status.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Is_Give_Away.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Specs.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Unit.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Category.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());

        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.RealStock.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.BlockedStock.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.AvailableStock.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.SafetyStock.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Warehouse.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());

        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Employee.renderType).build());

        return formFields;
    }

    /**
     * 获取TableColumns （只是用在listLayout）
     */
    public List<ITableColumn> getTableColumns() {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Name.apiName).lableName(StockConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Product.apiName).lableName(StockConstants.Field.Product.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Product_Status.apiName).lableName(StockConstants.Field.Product_Status.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Specs.apiName).lableName(StockConstants.Field.Specs.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Unit.apiName).lableName(StockConstants.Field.Unit.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Category.apiName).lableName(StockConstants.Field.Category.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.RealStock.apiName).lableName(StockConstants.Field.RealStock.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.AvailableStock.apiName).lableName(StockConstants.Field.AvailableStock.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.BlockedStock.apiName).lableName(StockConstants.Field.BlockedStock.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Warehouse.apiName).lableName(StockConstants.Field.Warehouse.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        return tableColumns;
    }

    public void updateStockDescribeConfig(User user) {
        String apiName = StockConstants.API_NAME;
        //更新库存describe
        initManager.updateDescribeObjectConfig(user, apiName);
    }

    public void updateFields(User user, String apiName) {
        String tenantId = user.getTenantId();
        //更新仓库描述
        IObjectDescribe objectDescribe = initManager.findObjectDescribeByTenantIdAndDescribeApiName(tenantId, apiName);

        if (objectDescribe == null) {
            log.warn("objectDescribe does not exist. tenantId[{}]", tenantId);
            return;
        }

        List<IFieldDescribe> fieldDescribes = objectDescribe.getFieldDescribes();
        List<IFieldDescribe> numFieldDescribeList = Lists.newArrayList();

        if (Objects.equals(apiName, StockConstants.API_NAME)) {
            numFieldDescribeList = fieldDescribes.stream().filter(fieldDescribe ->
                    Objects.equals(fieldDescribe.getApiName(), StockConstants.Field.RealStock.apiName) ||
                            Objects.equals(fieldDescribe.getApiName(), StockConstants.Field.BlockedStock.apiName) ||
                            Objects.equals(fieldDescribe.getApiName(), StockConstants.Field.AvailableStock.apiName)).collect(Collectors.toList());
        } else if (Objects.equals(apiName, DeliveryNoteProductObjConstants.API_NAME)){
            numFieldDescribeList = fieldDescribes.stream().filter(fieldDescribe ->
                    Objects.equals(fieldDescribe.getApiName(), DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName) ||
                            Objects.equals(fieldDescribe.getApiName(), DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName) ||
                            Objects.equals(fieldDescribe.getApiName(), DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName) ||
                            Objects.equals(fieldDescribe.getApiName(), DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName)).collect(Collectors.toList());
        } else if (Objects.equals(apiName, GoodsReceivedNoteProductConstants.API_NAME)) {
            numFieldDescribeList = fieldDescribes.stream().filter(fieldDescribe ->
                    Objects.equals(fieldDescribe.getApiName(), GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName)).collect(Collectors.toList());
        } else if (Objects.equals(apiName, OutboundDeliveryNoteProductConstants.API_NAME)) {
            numFieldDescribeList = fieldDescribes.stream().filter(fieldDescribe ->
                    Objects.equals(fieldDescribe.getApiName(), OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName)).collect(Collectors.toList());
        } else {
            numFieldDescribeList = fieldDescribes.stream().filter(fieldDescribe ->
                    Objects.equals(fieldDescribe.getApiName(), RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName)).collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(numFieldDescribeList)) {
            return;
        }

        NumberFieldDescribe numberFieldDescribe;
        boolean needModifyField = false;
        for (IFieldDescribe iFieldDescribe : numFieldDescribeList) {
            numberFieldDescribe = (NumberFieldDescribe) iFieldDescribe;
            if (!Objects.equals(numberFieldDescribe.getDecimalPlaces(), ConfigCenter.NUMBER_FIELD_DECIMAL_PLACES)) {
                numberFieldDescribe.setDecimalPlaces(ConfigCenter.NUMBER_FIELD_DECIMAL_PLACES);
                needModifyField = true;
            }
        }

        log.info("apiName:[{}], needModifyField:[{}]", apiName, needModifyField);

        if (needModifyField) {
            initManager.replaceObjectDescribe(objectDescribe, true, false);
        }

    }
}
