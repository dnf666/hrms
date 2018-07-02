package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.FormFieldBuilder;
import com.facishare.crm.describebuilder.TableColumnBuilder;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.dao.StockLogDAO;
import com.facishare.crm.stock.enums.GoodsReceivedNoteRecordTypeEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.GoodsReceivedNoteProductVO;
import com.facishare.crm.stock.model.GoodsReceivedNoteVO;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.service.model.GoodsReceivedNoteProductModel;
import com.facishare.crm.util.ObjectFieldConstantsUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.TeamMember;
import com.facishare.paas.appframework.metadata.dto.SaveMasterAndDetailData;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by linchf on 2018/1/13.
 */

@Slf4j(topic = "stockAccess")
@Service
public class GoodsReceivedNoteManager extends CommonManager {
    @Resource
    private ServiceFacade serviceFacade;

    @Resource
    private StockManager stockManager;

    @Resource
    private StockLogDAO stockLogDAO;

    @Resource
    private InitManager initManager;

    @Resource
    private StockCalculateManager stockCalculateManager;

    /**
     * 更新或插入库存  入库单新建
     */
    public void insertOrUpdateStock(User user, IObjectData goodReceivedNoteObj, GoodsReceivedNoteProductModel.BuildProductResult result, StockOperateInfo info) {
        if (null == result) {
            return;
        }

        String warehouseId = goodReceivedNoteObj.get(GoodsReceivedNoteConstants.Field.Warehouse.apiName).toString();

        List<String> productIds = result.getProductIds();
        Map<String, BigDecimal> productId2NumMap = result.getProductId2NumMap();

        List<IObjectData> stockList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        //如果查询库存记录为空，全部为保存操作
        if (CollectionUtils.isEmpty(stockList)) {
            bulkSaveStock(user, warehouseId, productIds, productId2NumMap, info);
        } else {
            Map<String, IObjectData> id2stock = stockList.stream().collect(Collectors.toMap(i -> i.get(GoodsReceivedNoteProductConstants.Field.Product.apiName, String.class), i -> i));
            List<String> noStockOfProductId = productIds.stream().filter(productId -> !id2stock.containsKey(productId)).collect(Collectors.toList());

            //新增库存记录
            bulkSaveStock(user, warehouseId, noStockOfProductId, productId2NumMap, info);

            //更新库存记录
            List<IObjectData> updateStockList = id2stock.entrySet().stream().map(map -> stockCalculateManager.addReal(user, map.getValue(), productId2NumMap.get(map.getKey()))).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(updateStockList)) {

                List<StockLogDO> stockLogDOs = updateStockList.stream().map(newStock -> {
                    StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                    stockLogDO.setModifiedRealStockNum(String.valueOf(productId2NumMap.get(stockLogDO.getProductId())));
                    return stockLogDO;
                }).collect(Collectors.toList());

                stockManager.batchUpdate(user, updateStockList, stockLogDOs);

            }
        }
    }

    private void bulkSaveStock(User user, String warehouseId, List<String> productIds, Map<String, BigDecimal> productId2NumMap, StockOperateInfo info) {

        List<IObjectData> saveStockList = productIds.stream().map(productId ->
                stockManager.buildStock(user, warehouseId, productId, productId2NumMap.get(productId).toString())).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(saveStockList)) {

            List<StockLogDO> stockLogDOs = saveStockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedRealStockNum(String.valueOf(productId2NumMap.get(stockLogDO.getProductId())));
                return stockLogDO;
            }).collect(Collectors.toList());

            stockManager.bulkSave(user, saveStockList, stockLogDOs);
        }
    }

    public GoodsReceivedNoteProductModel.BuildProductResult buildGoodsReceivedNoteProduct(User user, IObjectData goodReceivedNoteObj) {
        GoodsReceivedNoteProductModel.BuildProductResult result = new GoodsReceivedNoteProductModel.BuildProductResult();

        //查找入库产品
        IObjectDescribe goodsReceivedNoteProductDescribe = serviceFacade.findObject(user.getTenantId(), GoodsReceivedNoteProductConstants.API_NAME);
        List<IObjectData> objectDataList = serviceFacade.findDetailObjectDataList(goodsReceivedNoteProductDescribe, goodReceivedNoteObj, user);
        log.info("GoodsReceivedNoteManager.buildGoodsReceivedNoteProduct. productData[{}], goodsReceivedNoteObj[{}]", objectDataList, goodReceivedNoteObj);
        if (!CollectionUtils.isEmpty(objectDataList)) {
            Map<String, BigDecimal> productId2NumMap = objectDataList.stream().collect(Collectors.toMap(data -> data.get(GoodsReceivedNoteProductConstants.Field.Product.apiName, String.class),
                    data -> (new BigDecimal(data.get(GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName).toString()))));

            List<String> productIds = objectDataList.stream().map(data -> data.get(GoodsReceivedNoteProductConstants.Field.Product.apiName, String.class)).collect(Collectors.toList());

            result.setProductId2NumMap(productId2NumMap);
            result.setProductIds(productIds);
            return result;
        }
        return null;
    }

    public void minusBlockedStock(User user, IObjectData goodReceivedNoteObj, GoodsReceivedNoteProductModel.BuildProductResult result, StockOperateInfo info) {
        if (null == result) {
            return;
        }

        List<String> productIds = result.getProductIds();
        String wareHouseId = goodReceivedNoteObj.get(GoodsReceivedNoteConstants.Field.Warehouse.apiName, String.class);
        List<IObjectData> stockDatas = stockManager.queryByWarehouseIdAndProductIds(user, wareHouseId, productIds);

        if (CollectionUtils.isEmpty(stockDatas)) {
            log.warn("No stock recording. minusBlockedStock failed! productIds[{}], wareHouseId[{}]", productIds, wareHouseId);
            return;
        }

        Map<String, BigDecimal> productId2ProductNumMap = result.getProductId2NumMap();
        Map<String, IObjectData> productId2stockMap = stockDatas.stream().collect(Collectors.toMap(d -> d.get(StockConstants.Field.Product.apiName, String.class), d -> d));

        log.debug("GoodsReceivedNoteManager.minusBlockedStock! GoodsReceivedNoteObj[{}]", goodReceivedNoteObj);

        List<IObjectData> stockList = Lists.newArrayList();
        productIds.forEach(productId -> {
            IObjectData stock = productId2stockMap.get(productId);
            if (stock != null) {
                //扣减冻结库存
                stockList.add(stockCalculateManager.minusBlocked(user, stock, productId2ProductNumMap.get(productId)));
            }
        });

        if (!CollectionUtils.isEmpty(stockList)) {

            List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedBlockedStockNum(String.valueOf(productId2ProductNumMap.get(stockLogDO.getProductId()).negate()));
                return stockLogDO;
            }).collect(Collectors.toList());

            //扣减冻结库存
            stockManager.batchUpdate(user, stockList, stockLogDOs);
        }

    }

    public void addBlockedStock(User user, IObjectData goodReceivedNoteObj, GoodsReceivedNoteProductModel.BuildProductResult result, StockOperateInfo info) {
        if (null == result) {
            return;
        }

        List<String> productIds = result.getProductIds();
        String wareHouseId = goodReceivedNoteObj.get(GoodsReceivedNoteConstants.Field.Warehouse.apiName, String.class);
        List<IObjectData> stockDatas = stockManager.queryByWarehouseIdAndProductIds(user, wareHouseId, productIds);

        if (CollectionUtils.isEmpty(stockDatas)) {
            log.warn("No stock recording. minusBlockedStock failed! productIds[{}], wareHouseId[{}]", productIds, wareHouseId);
            return;
        }

        Map<String, IObjectData> productId2stockMap = stockDatas.stream().collect(Collectors.toMap(d -> d.get(StockConstants.Field.Product.apiName, String.class), d -> d));
        Map<String, BigDecimal> productId2ProductNumMap = result.getProductId2NumMap();

        log.debug("GoodsReceivedNoteManager.addBlockedStock! GoodsReceivedNoteObj[{}]", goodReceivedNoteObj);

        //需校验库存
        stockManager.checkAvailableStock(user, stockDatas, productId2ProductNumMap, wareHouseId);

        List<IObjectData> stockList = Lists.newArrayList();
        productIds.forEach(productId -> {
            IObjectData stock = productId2stockMap.get(productId);
            if (stock != null) {
                //增加冻结库存
                stockList.add(stockCalculateManager.addBlocked(user, stock, productId2ProductNumMap.get(productId)));
            }
        });

        if (!CollectionUtils.isEmpty(stockList)) {

            List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedBlockedStockNum(String.valueOf(productId2ProductNumMap.get(stockLogDO.getProductId())));
                return stockLogDO;
            }).collect(Collectors.toList());

            //增加冻结库存
            stockManager.batchUpdate(user, stockList, stockLogDOs);
        }
    }


    public void minusRealStock(User user, IObjectData goodReceivedNoteObj, GoodsReceivedNoteProductModel.BuildProductResult result, StockOperateInfo info) {

        if (null == result) {
            return;
        }

        List<String> productIds = result.getProductIds();
        String wareHouseId = goodReceivedNoteObj.get(GoodsReceivedNoteConstants.Field.Warehouse.apiName, String.class);
        List<IObjectData> stockDatas = stockManager.queryByWarehouseIdAndProductIds(user, wareHouseId, productIds);

        if (CollectionUtils.isEmpty(stockDatas)) {
            log.warn("No stock recording. minusBlockedStock failed! productIds[{}], wareHouseId[{}]", productIds, wareHouseId);
            return;
        }

        Map<String, BigDecimal> productId2ProductNumMap = result.getProductId2NumMap();
        Map<String, IObjectData> productId2stockMap = stockDatas.stream().collect(Collectors.toMap(d -> d.get(StockConstants.Field.Product.apiName, String.class), d -> d));

        log.debug("GoodsReceivedNoteManager.minusRealStock! GoodsReceivedNoteObj[{}]", goodReceivedNoteObj);


        List<IObjectData> stockList = Lists.newArrayList();
        productIds.forEach(productId -> {
            IObjectData stock = productId2stockMap.get(productId);
            if (stock != null) {
                //扣减实际库存
                stockList.add(stockCalculateManager.minusReal(user, stock, productId2ProductNumMap.get(productId)));
            }
        });

        if (!CollectionUtils.isEmpty(stockList)) {

            List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedRealStockNum(String.valueOf(productId2ProductNumMap.get(stockLogDO.getProductId()).negate()));
                return stockLogDO;
            }).collect(Collectors.toList());

            stockManager.batchUpdate(user, stockList, stockLogDOs);
        }

    }

    public void minusBlockedRealStock(User user, IObjectData goodReceivedNoteObj, GoodsReceivedNoteProductModel.BuildProductResult result, StockOperateInfo info) {
        if (null == result) {
            return;
        }

        List<String> productIds = result.getProductIds();
        String wareHouseId = goodReceivedNoteObj.get(GoodsReceivedNoteConstants.Field.Warehouse.apiName, String.class);
        List<IObjectData> stockDatas = stockManager.queryByWarehouseIdAndProductIds(user, wareHouseId, productIds);

        if (CollectionUtils.isEmpty(stockDatas)) {
            log.warn("No stock recording. minusBlockedStock failed! productIds[{}], wareHouseId[{}]", productIds, wareHouseId);
            return;
        }

        Map<String, IObjectData> productId2stockMap = stockDatas.stream().collect(Collectors.toMap(d -> d.get(StockConstants.Field.Product.apiName, String.class), d -> d));
        Map<String, BigDecimal> productId2ProductNumMap = result.getProductId2NumMap();

        log.debug("GoodsReceivedNoteManager.minusBlockedRealStock! GoodsReceivedNoteObj[{}]", goodReceivedNoteObj);

        List<IObjectData> stockList = Lists.newArrayList();
        productIds.forEach(productId -> {
            IObjectData stock = productId2stockMap.get(productId);
            if (stock != null) {
                //扣减实际和冻结库存
                stockList.add(stockCalculateManager.minusBlockReal(user, stock, productId2ProductNumMap.get(productId)));
            }
        });

        if (!CollectionUtils.isEmpty(stockList)) {

            List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedBlockedStockNum(String.valueOf(productId2ProductNumMap.get(stockLogDO.getProductId()).negate()));
                stockLogDO.setModifiedRealStockNum(String.valueOf(productId2ProductNumMap.get(stockLogDO.getProductId()).negate()));
                return stockLogDO;
            }).collect(Collectors.toList());

            stockManager.batchUpdate(user, stockList, stockLogDOs);
        }
    }

    public void invalidBefore(User user, IObjectData objectData, GoodsReceivedNoteProductModel.BuildProductResult result) {
        String warehouseId = objectData.get(GoodsReceivedNoteConstants.Field.Warehouse.apiName, String.class);

        List<String> productIds = result.getProductIds();
        Map<String, BigDecimal> productMap = result.getProductId2NumMap();

        //查询产品库存信息
        List<IObjectData> stockObjectDataList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);

        //校验可用库存
        stockManager.checkAvailableStock(user, stockObjectDataList, productMap, warehouseId);
    }

    public void invalidAfter(User user, IObjectData objectData, GoodsReceivedNoteProductModel.BuildProductResult result) {
        String warehouseId = objectData.get(GoodsReceivedNoteConstants.Field.Warehouse.apiName, String.class);

        List<String> productIds = result.getProductIds();
        //<产品id, 入库数量>
        Map<String, BigDecimal> productId2AmountMap = result.getProductId2NumMap();

        //查询产品库存信息
        List<IObjectData> stockObjectDataList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        //<产品id, 实际库存>
        Map<String, BigDecimal> productId2RealStockMap = stockObjectDataList.stream().collect(Collectors.toMap(s -> s.get(StockConstants.Field.Product.apiName, String.class), s -> s.get(StockConstants.Field.RealStock.apiName, BigDecimal.class)));

        productId2AmountMap.entrySet().forEach(entry -> {
            if (productId2RealStockMap.containsKey(entry.getKey())) {
                if (0 > productId2RealStockMap.get(entry.getKey()).compareTo(entry.getValue())) {
                    log.error("GoodsReceivedNote invalid has a error that is real stock less than goodsReceivedNote Amount, user[{}], productId[{}], objectData[{}]",
                            user, entry.getKey(), objectData);
                }
            }
        });
    }

    /**
     * 创建一条入库单数据
     *
     * @param user 用户
     * @param goodsReceivedNoteVO 入库单vo
     * @param goodsReceivedNoteProductVOList 入库单产品列表
     * @param goodsReceivedNoteRecordType @see com.facishare.crm.stock.enums.GoodsReceivedNoteRecordTypeEnum.RequisitionIn.apiName
     */
    public IObjectData create(User user, GoodsReceivedNoteVO goodsReceivedNoteVO, List<GoodsReceivedNoteProductVO> goodsReceivedNoteProductVOList, String goodsReceivedNoteRecordType) {
        SaveMasterAndDetailData.Arg arg = buildGoodsReceivedNoteObjectData(user, goodsReceivedNoteVO, goodsReceivedNoteProductVOList, goodsReceivedNoteRecordType);
        return serviceFacade.saveMasterAndDetailData(user, arg).getMasterObjectData();
    }

    public SaveMasterAndDetailData.Arg buildGoodsReceivedNoteObjectData(User user, GoodsReceivedNoteVO goodsReceivedNoteVO, List<GoodsReceivedNoteProductVO> goodsReceivedNoteProductVOList, String goodsReceivedNoteRecordType) {
        IObjectDescribe noteDescribe = serviceFacade.findObject(user.getTenantId(), GoodsReceivedNoteConstants.API_NAME);
        IObjectDescribe noteProductDescribe = serviceFacade.findObject(user.getTenantId(), GoodsReceivedNoteProductConstants.API_NAME);

        if (noteDescribe == null || noteProductDescribe == null) {
            log.warn("buildGoodsReceivedNoteObjectData failed. describe is null. user[{}]", user);
            return null;
        }

        //创建主对象
        IObjectData masterObj = new ObjectData();
        masterObj.set(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.apiName, goodsReceivedNoteVO.getGoodsReceivedDate());
        masterObj.set(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName, goodsReceivedNoteVO.getRequisitionId());
        masterObj.set(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName, goodsReceivedNoteVO.getGoodsReceivedType());
        masterObj.set(GoodsReceivedNoteConstants.Field.Warehouse.apiName, goodsReceivedNoteVO.getWarehouseId());
        masterObj.set(GoodsReceivedNoteConstants.Field.Remark.apiName, goodsReceivedNoteVO.getRemark());


        masterObj.setTenantId(user.getTenantId());
        masterObj.setCreatedBy(user.getUserId());
        masterObj.setLastModifiedBy(user.getUserId());
        masterObj.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
        masterObj.setRecordType(goodsReceivedNoteRecordType);
        masterObj.set(IObjectData.DESCRIBE_ID, noteDescribe.getId());
        masterObj.set(IObjectData.DESCRIBE_API_NAME, GoodsReceivedNoteConstants.API_NAME);
        masterObj.set(IObjectData.PACKAGE, "CRM");
        masterObj.set(IObjectData.VERSION, noteDescribe.getVersion());

        //相关团队
        TeamMember teamMember = new TeamMember(user.getUserId(), TeamMember.Role.OWNER, TeamMember.Permission.READANDWRITE);

        ObjectDataExt objectDataExt = ObjectDataExt.of(masterObj);
        objectDataExt.setTeamMembers(Lists.newArrayList(teamMember));

        masterObj = objectDataExt.getObjectData();

        //创建从对象
        Map<String, List<IObjectData>> detailObjectMap = new HashMap<>();
        List<IObjectData> detailObjects = Lists.newArrayList();
        goodsReceivedNoteProductVOList.forEach(vo -> {
            IObjectData productObj = new ObjectData();
            productObj.set(GoodsReceivedNoteProductConstants.Field.Product.apiName, vo.getProductId());
            productObj.set(GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName, vo.getGoodsReceivedAmount());
            productObj.set(GoodsReceivedNoteProductConstants.Field.Remark.apiName, vo.getRemark());

            productObj.setTenantId(user.getTenantId());
            productObj.setCreatedBy(user.getUserId());
            productObj.setLastModifiedBy(user.getUserId());
            productObj.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
            productObj.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
            productObj.set(IObjectData.DESCRIBE_ID, noteProductDescribe.getId());
            productObj.set(IObjectData.DESCRIBE_API_NAME, GoodsReceivedNoteProductConstants.API_NAME);
            productObj.set(IObjectData.PACKAGE, "CRM");
            productObj.set(IObjectData.VERSION, noteProductDescribe.getVersion());


            //相关团队
            ObjectDataExt productDataExt = ObjectDataExt.of(productObj);
            productDataExt.setTeamMembers(Lists.newArrayList(teamMember));

            productObj = productDataExt.getObjectData();
            detailObjects.add(productObj);
        });

        detailObjectMap.put(GoodsReceivedNoteProductConstants.API_NAME, detailObjects);

        //创建映射Map
        Map<String, IObjectDescribe> objectDescribesMap = new HashMap<>();
        objectDescribesMap.put(GoodsReceivedNoteConstants.API_NAME, noteDescribe);
        objectDescribesMap.put(GoodsReceivedNoteProductConstants.API_NAME, noteProductDescribe);

        return SaveMasterAndDetailData.Arg.builder().masterObjectData(masterObj).detailObjectData(detailObjectMap).objectDescribes(objectDescribesMap).build();
    }


/*    public void updateGoodsReceivedNoteDescribeAndLayout(User user) {
        String apiName = GoodsReceivedNoteConstants.API_NAME;
        String goodsReceivedNoteProdutApiName = GoodsReceivedNoteProductConstants.API_NAME;

        initManager.updateObjectDescribe(user, apiName);
        initManager.updateObjectLayout(user, apiName, true, true);

        for(GoodsReceivedNoteRecordTypeEnum recordTypeEnum : GoodsReceivedNoteRecordTypeEnum.values()) {
            if (!Objects.equals(recordTypeEnum.apiName, GoodsReceivedNoteRecordTypeEnum.DefaultGoodsReceivedNote.apiName)) {
                initManager.initNoteRecordType(user, GoodsReceivedNoteConstants.API_NAME, recordTypeEnum.apiName, GoodsReceivedNoteConstants.DETAIL_LAYOUT_API_NAME, Lists.newArrayList());
            }
        }

        initManager.updateObjectDescribe(user, goodsReceivedNoteProdutApiName);

    }*/

    public void addFieldDescribeAndLayout(User user) {
        String apiName = GoodsReceivedNoteConstants.API_NAME;
        String tenantId = user.getTenantId();

        //添加入库单产品describe、layout
        List<String> fieldApiNames = new ArrayList<>();
        fieldApiNames.add(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName);

        //查询describe
        IObjectDescribe objectDescribe = initManager.findObjectDescribeByTenantIdAndDescribeApiName(tenantId, apiName);

        //添加字段（添加之前，判断是否已存在对应的字段）
        initManager.addFieldDescribes(objectDescribe, apiName, fieldApiNames);

        initManager.addFieldLayouts(tenantId, apiName, fieldApiNames, true, true);

        updateGoodsReceivedNoteDescribe(user);

        for(GoodsReceivedNoteRecordTypeEnum recordTypeEnum : GoodsReceivedNoteRecordTypeEnum.values()) {
            if (!Objects.equals(recordTypeEnum.apiName, GoodsReceivedNoteRecordTypeEnum.DefaultGoodsReceivedNote.apiName)) {
                if (!checkRecordType(user.getTenantId(), GoodsReceivedNoteConstants.API_NAME, recordTypeEnum.apiName)) {
                    initManager.initNoteRecordType(user, GoodsReceivedNoteConstants.API_NAME, recordTypeEnum.apiName, GoodsReceivedNoteConstants.DETAIL_LAYOUT_API_NAME, Lists.newArrayList());
                }
            }
        }

        updateGoodsReceivedNoteProductDescribe(user);
    }

    public Boolean checkRecordType(String tenantId, String apiName, String recordTypeId) {
        Map<String, List<IRecordTypeOption>> recordTypeMap = serviceFacade.findRecordTypes(tenantId, Lists.newArrayList(apiName));
        if (!CollectionUtils.isEmpty(recordTypeMap)) {
            if (recordTypeMap.containsKey(apiName)) {
                List<IRecordTypeOption> recordTypeOptions = recordTypeMap.get(apiName);
                return recordTypeOptions.stream().anyMatch(recordTypeOption -> Objects.equals(recordTypeOption.getApiName(), recordTypeId));
            }
        }

        return false;
    }

    /**
     * 获取FormFields
     */
    public List<IFormField> getFormFields() {
        //基本信息
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.Name.apiName).readOnly(false).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.apiName).readOnly(false).renderType(SystemConstants.RenderType.Date.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.Warehouse.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName).readOnly(false).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).renderType(SystemConstants.RenderType.Employee.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.LongText.renderType).required(false).build());

        return formFields;
    }

    /**
     * 获取TableColumns （只是用在listLayout）
     */
    public List<ITableColumn> getTableColumns() {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.Name.apiName).lableName(GoodsReceivedNoteConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName).lableName(GoodsReceivedNoteConstants.Field.GoodsReceivedType.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.Warehouse.apiName).lableName(GoodsReceivedNoteConstants.Field.Warehouse.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName).lableName(GoodsReceivedNoteConstants.Field.RequisitionNote.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.apiName).lableName(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.LifeStatus.apiName).lableName(SystemConstants.Field.LifeStatus.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        return tableColumns;
    }

    public void updateGoodsReceivedNoteDescribe(User user) {
        String apiName = GoodsReceivedNoteConstants.API_NAME;

        //更新入库单describe、layout
        List<String> fieldApiNames = new ArrayList<>();
        fieldApiNames.add(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName);
        fieldApiNames.add(SystemConstants.Field.RecordType.apiName);

        fieldApiNames.forEach(fieldApiName -> {
            initManager.updateGoodsReceivedNoteField(user, apiName, fieldApiName);
        });
    }

    public void updateGoodsReceivedNoteProductDescribe(User user) {
        String apiName = GoodsReceivedNoteProductConstants.API_NAME;
        //更新入库单产品describe
        initManager.updateDescribeObjectConfig(user, apiName);
    }

    public void modifyArg(String tenantId, BaseObjectSaveAction.Arg arg) {
        ObjectDataDocument objectData = arg.getObjectData();
        if (objectData == null) {
            throw new ValidateException("对象不能为空");
        }

        if (CollectionUtils.isEmpty(arg.getDetails())) {
            throw new ValidateException("从对象不能为空");
        }

        // 业务类型默认设置为预设业务类型，因为OpenAPI调用时RecordType会传空
        if (StringUtils.isEmpty(arg.getObjectData().toObjectData().getRecordType())) {
            arg.getObjectData().put(MultiRecordType.RECORD_TYPE, MultiRecordType.RECORD_TYPE_DEFAULT);
        }

        // OpenAPI接口调用时describeID为空，需要补充此字段
        String objectDescribeId = (String) arg.getObjectData().get(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID);
        if (StringUtils.isEmpty(objectDescribeId)) {
            IObjectDescribe describe = findDescribe(tenantId, GoodsReceivedNoteConstants.API_NAME);
            setDescribeField(arg.getObjectData(), describe);
        }

        // 补全从对象的describeId
        Map<String, List<ObjectDataDocument>> details = arg.getDetails();
        if (MapUtils.isNotEmpty(details)) {
            details.forEach((describeApiName, value) -> {
                IObjectDescribe detailDescribe = findDescribe(tenantId, describeApiName);
                value.forEach(objectDataDocument -> {
                    String detailObjectDescribeId = (String) objectDataDocument.get(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID);
                    if (StringUtils.isEmpty(detailObjectDescribeId)) {
                        setDescribeField(objectDataDocument, detailDescribe);
                    }
                });
            });
        }

        List<ObjectDataDocument> productObjectDocList = arg.getDetails().get(GoodsReceivedNoteProductConstants.API_NAME);
        productObjectDocList.forEach(product -> {
            // 业务类型默认设置为预设业务类型，因为OpenAPI调用时RecordType会传空
            if (StringUtils.isEmpty(product.toObjectData().getRecordType())) {
                product.put(MultiRecordType.RECORD_TYPE, MultiRecordType.RECORD_TYPE_DEFAULT);
            }
        });

        arg.setObjectData(objectData);
    }

    private void setDescribeField(ObjectDataDocument objectDataDocument, IObjectDescribe objectDescribe) {
        objectDataDocument.put(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID, objectDescribe.getId());
        objectDataDocument.put(ObjectFieldConstantsUtil.FIELD_DESCRIBE_API_NAME, objectDescribe.getApiName());
    }

    private IObjectDescribe findDescribe(String tenantId, String describeApiName) {
        IObjectDescribe describe = serviceFacade.findObject(tenantId, describeApiName);
        if (describe == null) {
            throw new ValidateException("查询不到对象[" + describeApiName + "]");
        }
        return describe;
    }

    public IObjectData getObjectDataById(User user, String id) {
        IObjectData iObjectData = this.serviceFacade.findObjectDataIncludeDeleted(user, id, GoodsReceivedNoteConstants.API_NAME);
        if (Objects.isNull(iObjectData)) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "入库单不存在");
        }
        return iObjectData;
    }
}
