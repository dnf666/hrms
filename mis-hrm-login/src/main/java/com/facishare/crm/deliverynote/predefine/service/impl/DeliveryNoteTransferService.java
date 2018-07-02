package com.facishare.crm.deliverynote.predefine.service.impl;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.enums.DeliveryNoteSwitchEnum;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.crm.deliverynote.predefine.manager.*;
import com.facishare.crm.manager.DeliveryNoteLayoutManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.RequestData;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.utils.ConverterUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 6.3，发货单添加3个字段，做老数据的刷库
 *
 * Created by chenzs on 2018/3/15.
 */
@Service
@Slf4j
public class DeliveryNoteTransferService extends CommonTransferService {
    @Resource
    private ConfigManager configManager;
    @Resource
    private ObjectDescribeManager objectDescribeManager;
    @Autowired
    private StockManager stockManager;
    @Autowired
    private DeliveryNoteManager deliveryNoteManager;
    @Autowired
    private DeliveryNoteProductManager deliveryNoteProductManager;
    @Autowired
    private SalesOrderManager salesOrderManager;
    @Autowired
    private DeliveryNoteProductTransferService deliveryNoteProductTransferService;
    @Autowired
    private LayoutManager layoutManager;
    @Autowired
    private DeliveryNoteLayoutManager deliveryNoteLayoutManager;

    /**
     * 6.3，发货单   添加[发货总金额、收货日期、收货备注]             3个字段, 旧数据的处理（describe、layout、data）
     * 6.3，发货单产品添加[平均单价、本次发货金额、本次收货数、收货备注] 4个字段, 旧数据的处理（describe、layout、data）
     */
    public void addFieldDescribeAndData(ServiceContext serviceContext, String tenantId) {
        User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);

        //开启了发货单才需要处理
        DeliveryNoteSwitchEnum deliveryNoteSwitchStatus = configManager.getDeliveryNoteStatus(tenantId);
        if (!Objects.equals(deliveryNoteSwitchStatus.getStatus(), DeliveryNoteSwitchEnum.OPENED.getStatus())) {
            return;
        }

        //是否开启了库存
        boolean isStockEnable = stockManager.isStockEnable(tenantId);

        //添加发货单产品describe、layout
        deliveryNoteAddField(tenantId);

        //添加发货单产品describe、layout
        deliveryNoteProductAddField(tenantId, isStockEnable);

        //查询所有的订单id
        List<String> allHasDeliveryNoteSaleOrderIds = salesOrderManager.getAllHasDeliveryNoteSaleOrderIds(user);

        //添加数据
        addData(user, allHasDeliveryNoteSaleOrderIds);
    }

    /**
     * 发货单添加[发货总金额、收货日期、收货备注] 3个字段的定义和layout
     */
    private void deliveryNoteAddField(String tenantId) {
        String totalDeliveryMoney = DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName;
        String receiveDate = DeliveryNoteObjConstants.Field.ReceiveDate.apiName;
        String receiveRemark = DeliveryNoteObjConstants.Field.ReceiveRemark.apiName;

        List<String> fieldApiNames = new ArrayList<>();
        fieldApiNames.add(totalDeliveryMoney);
        fieldApiNames.add(receiveDate);
        fieldApiNames.add(receiveRemark);
        //1、describe加字段
        objectDescribeManager.addFieldDescribes(tenantId, DeliveryNoteObjConstants.API_NAME, fieldApiNames);

        //2、defaultLayout加字段
        //要加的layout信息
        Map<String, IFormField> addFieldApiName2FormFieldMap = new HashMap<>();
        addFieldApiName2FormFieldMap.put(totalDeliveryMoney, deliveryNoteLayoutManager.getDeliveryNoteFormField(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName));
        addFieldApiName2FormFieldMap.put(receiveDate, deliveryNoteLayoutManager.getDeliveryNoteFormField(DeliveryNoteObjConstants.Field.ReceiveDate.apiName));
        addFieldApiName2FormFieldMap.put(receiveRemark, deliveryNoteLayoutManager.getDeliveryNoteFormField(DeliveryNoteObjConstants.Field.ReceiveRemark.apiName));

        //放在哪个字段后面
        Map<String, String> addFieldApiName2afterFieldApiNameMap = new HashMap<>();
        addFieldApiName2afterFieldApiNameMap.put(totalDeliveryMoney, DeliveryNoteObjConstants.Field.ExpressOrderId.apiName);
        addFieldApiName2afterFieldApiNameMap.put(receiveDate, DeliveryNoteObjConstants.Field.Remark.apiName);
        addFieldApiName2afterFieldApiNameMap.put(receiveRemark, DeliveryNoteObjConstants.Field.ReceiveDate.apiName);

        ILayout defaultLayout = layoutManager.getLayout(tenantId, DeliveryNoteObjConstants.API_NAME, SystemConstants.LayoutType.Detail.layoutType);
        deliveryNoteLayoutManager.detailLayoutAddField(defaultLayout, fieldApiNames, addFieldApiName2FormFieldMap, addFieldApiName2afterFieldApiNameMap);

        //listLayout不用
    }

    /**
     * 发货单产品添加[平均单价、本次发货金额、本次收货数、收货备注] 4个字段的定义和layout
     */
    private void deliveryNoteProductAddField(String tenantId, boolean isStockEnable) {
        String avgPrice = DeliveryNoteProductObjConstants.Field.AvgPrice.apiName;
        String deliveryMoney = DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName;
        String realReceiveNum = DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName;
        String receiveRemark = DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName;

        List<String> fieldApiNames = new ArrayList<>();
        fieldApiNames.add(avgPrice);
        fieldApiNames.add(deliveryMoney);
        fieldApiNames.add(realReceiveNum);
        fieldApiNames.add(receiveRemark);
        //1、describe加字段
        objectDescribeManager.addFieldDescribes(tenantId, DeliveryNoteProductObjConstants.API_NAME, fieldApiNames);

        //2、layout加字段
        //放在哪个字段后面
        Map<String, String> addFieldApiName2afterFieldApiNameMap = new HashMap<>();
        addFieldApiName2afterFieldApiNameMap.put(avgPrice, DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName);
        addFieldApiName2afterFieldApiNameMap.put(deliveryMoney, DeliveryNoteProductObjConstants.Field.AvgPrice.apiName);
        if (isStockEnable) {
            addFieldApiName2afterFieldApiNameMap.put(realReceiveNum, DeliveryNoteProductObjConstants.Field.RealStock.apiName);
        } else {
            addFieldApiName2afterFieldApiNameMap.put(realReceiveNum, DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName);
        }
        addFieldApiName2afterFieldApiNameMap.put(receiveRemark, DeliveryNoteProductObjConstants.Field.Remark.apiName);

        //2.1、defaultLayout加字段
        //要加的layout信息
        Map<String, IFormField> addFieldApiName2FormFieldMap = new HashMap<>();
        addFieldApiName2FormFieldMap.put(avgPrice, deliveryNoteLayoutManager.getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName));
        addFieldApiName2FormFieldMap.put(deliveryMoney, deliveryNoteLayoutManager.getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName));
        addFieldApiName2FormFieldMap.put(realReceiveNum, deliveryNoteLayoutManager.getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName));
        addFieldApiName2FormFieldMap.put(receiveRemark, deliveryNoteLayoutManager.getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName));
        ILayout defaultLayout = layoutManager.getLayout(tenantId, DeliveryNoteProductObjConstants.API_NAME, SystemConstants.LayoutType.Detail.layoutType);
        deliveryNoteLayoutManager.detailLayoutAddField(defaultLayout, fieldApiNames, addFieldApiName2FormFieldMap, addFieldApiName2afterFieldApiNameMap);

        //2.2、listLayout加字段
        Map<String, ITableColumn> addFieldApiName2TableColumnMap = new HashMap<>();
        addFieldApiName2TableColumnMap.put(avgPrice, deliveryNoteLayoutManager.getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName));
        addFieldApiName2TableColumnMap.put(deliveryMoney, deliveryNoteLayoutManager.getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName));
        addFieldApiName2TableColumnMap.put(realReceiveNum, deliveryNoteLayoutManager.getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName));
        addFieldApiName2TableColumnMap.put(receiveRemark, deliveryNoteLayoutManager.getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName));
        ILayout listLayout = layoutManager.getLayout(tenantId, DeliveryNoteProductObjConstants.API_NAME, SystemConstants.LayoutType.List.layoutType);
        deliveryNoteLayoutManager.listLayoutAddField(listLayout, fieldApiNames, addFieldApiName2TableColumnMap, addFieldApiName2afterFieldApiNameMap);
    }

    /**
     * 发货单   添加[发货总金额、收货日期、收货备注]             3个字段的数据（'收货备注'之前是没有的，不用补数据）
     * 发货单产品添加[平均单价、本次发货金额、本次收货数、收货备注] 4个字段的数据（'收货备注'之前是没有的，不用补数据）
     *
     * 分页处理，每次处理一条发货单，然后处理该条发货单的所有发货单产品，再处理下一条发货单
     */
    private void addData(User user, List<String> allHasDeliveryNoteSaleOrderIds) {
        log.info("addData tenantId[{}]", user.getTenantId());
        if (CollectionUtils.isEmpty(allHasDeliveryNoteSaleOrderIds)) {
            return;
        }

        allHasDeliveryNoteSaleOrderIds.forEach(saleOrderId -> {
            try {
                //订单的所有发货单（包括已作废的）
                List<IObjectData> deliveryNotes = deliveryNoteManager.getAllObjectDatasBySaleOrderId(user, saleOrderId);
                if (CollectionUtils.isEmpty(deliveryNotes)) {
                    return;
                }

                //订单的所有发货单产品（包括已作废的）
                List<IObjectData> deliveryNoteProducts = deliveryNoteProductManager.queryAllBySaleOrderId(user, saleOrderId);

                tranferData(user, saleOrderId, deliveryNotes, deliveryNoteProducts);
                log.info("tranferData, user[{}], saleOrderId[{}], deliveryNotes[{}], deliveryNoteProducts[{}]", user, saleOrderId, deliveryNotes, deliveryNoteProducts);
            } catch (Exception e) {
                log.warn("addData error, user[{}], saleOrderId[{}], allHasDeliveryNoteSaleOrderIds:[{}]", user, saleOrderId, allHasDeliveryNoteSaleOrderIds, e);
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_TRANSFER_FAILED, "tenantId = "+ user.getTenantId() + DeliveryNoteErrorCode.DELIVERY_NOTE_TRANSFER_FAILED.getMessage() + e);
            }
        });
    }

    private void tranferData(User user, String saleOrderId, List<IObjectData> deliveryNotes, List<IObjectData> allDeliveryNoteProducts) {
        if (CollectionUtils.isEmpty(deliveryNotes)) {
            return;
        }

        //获取发货单及其发货单产品要补充的money信息
        setMoneyField(user, saleOrderId, deliveryNotes, allDeliveryNoteProducts);
        Map<String, List<IObjectData>> deliveryNoteId2DeliveryNoteProductsMap = getDeliveryNoteId2DeliveryNoteProductsMap(allDeliveryNoteProducts);

        //补充发货单的'收货日期'字段
        addReceiveData(deliveryNotes);

        //添加发货单的数据
        RequestData requestData = buildRequestData(user.getTenantId(), DeliveryNoteObjConstants.STORE_TABLE_NAME, Lists.newArrayList(deliveryNotes));
        log.info("requestData=====>{}", requestData);
        transfer(requestData);

        //每个发货单的发货单产品
        deliveryNotes.forEach(deliveryNote -> {
            String deliveryNoteId = deliveryNote.getId().toString();

            String deliveryNoteStatus = null;
            if (deliveryNote.get(DeliveryNoteObjConstants.Field.Status.apiName) != null) {  //脏数据不处理
                deliveryNoteStatus = deliveryNote.get(DeliveryNoteObjConstants.Field.Status.apiName).toString();
            }

            //查找发货单的所有发货单产品
            List<IObjectData> deliveryNoteProducts = deliveryNoteId2DeliveryNoteProductsMap.get(deliveryNoteId);

            //添加发货单对应的发货单产品数据
            deliveryNoteProductTransferService.addData(user, deliveryNoteProducts, deliveryNoteStatus);
        });
    }

    private Map<String, List<IObjectData>> getDeliveryNoteId2DeliveryNoteProductsMap(List<IObjectData> allDeliveryNoteProducts) {
        Map<String, List<IObjectData>> result = new HashMap<>();

        allDeliveryNoteProducts.forEach(product -> {
            String deliveryNoteId = (String) product.get(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName);
            if (!CollectionUtils.isEmpty(result.get(deliveryNoteId))) {
                List<IObjectData> deliveryNoteProducts = result.get(deliveryNoteId);
                deliveryNoteProducts.add(product);
                result.put(deliveryNoteId, deliveryNoteProducts);
            } else {
                result.put(deliveryNoteId, Lists.newArrayList(product));
            }
        });

        return result;
    }

    /**
     * 补充发货单的'收货日期'字段
     */
    private void addReceiveData(List<IObjectData> deliveryNotes) {
        if (CollectionUtils.isEmpty(deliveryNotes)) {
            return;
        }

        deliveryNotes.forEach(deliveryNote -> {
            if (deliveryNote.get(DeliveryNoteObjConstants.Field.Status.apiName) != null) {
                String deliveryNoteStatus = ConverterUtil.convert2String(deliveryNote.get(DeliveryNoteObjConstants.Field.Status.apiName).toString());

                //'已发货'：'收货日期' = LastModifiedTime
                if (Objects.equals(deliveryNoteStatus, DeliveryNoteObjStatusEnum.RECEIVED.getStatus())) {
                    String lastModifyTimeStr = ConverterUtil.convert2String(deliveryNote.get(SystemConstants.Field.LastModifiedTime.apiName).toString());
                    Long lastModifyTime = new Long(lastModifyTimeStr);
                    deliveryNote.set(DeliveryNoteObjConstants.Field.ReceiveDate.apiName, lastModifyTime);
                }
            }
        });
    }

    @Override
    protected List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
        String table = DeliveryNoteObjConstants.STORE_TABLE_NAME;
        List<Record> records = Lists.newArrayList();

        String id = ConverterUtil.convert2String(sourceData.getData().get("id").toString());
        String tenantId = sourceData.getTenantId();

        //TotalDeliveryMoney
        String totalDeliveryMoneyStr = ConverterUtil.convert2String(sourceData.getData().get(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName).toString());
        BigDecimal deliveryMoney = new BigDecimal(totalDeliveryMoneyStr);
        Record deliveryMoneyRecord = getRecord(tenantId, id, table, DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, deliveryMoney);
        records.add(deliveryMoneyRecord);

        //ReceiveDate：'已收货'才有
        Object receiveDateObj = sourceData.getData().get(DeliveryNoteObjConstants.Field.ReceiveDate.apiName);
        if (receiveDateObj != null && !Objects.equals(receiveDateObj.toString(), "0")) { //数据库里是null，查出来是0
            Long receiveDate = new Long(receiveDateObj.toString());
            Record receiveDateRecord = getRecord(tenantId, id, table, DeliveryNoteObjConstants.Field.ReceiveDate.apiName, receiveDate);
            records.add(receiveDateRecord);
        }

        //ReceiveRemark（没内容）
        return records;
    }


    /**
     * 设置一个订单的所有发货单及发货单产品的发货金额字段 （必须确保存入参deliveryNoteList及deliveryNoteProductList就某个订单的所有数据）
     */
    private void setMoneyField(User user, String salesOrderId, List<IObjectData> deliveryNoteList, List<IObjectData> deliveryNoteProductList) {
        Map<String, SalesOrderManager.OrderProduct> productId2OrderProduct = salesOrderManager.getOrderProduct(user, salesOrderId);
        setMoneyField(deliveryNoteList, deliveryNoteProductList, productId2OrderProduct);
    }

    private void setMoneyField(List<IObjectData> deliveryNoteList, List<IObjectData> deliveryNoteProductList, Map<String, SalesOrderManager.OrderProduct> productId2OrderProduct) {
        Map<String, BigDecimal> product2HasDeliveredNum = Maps.newHashMap();
        Map<String, BigDecimal> product2HasDeliveredMoney = Maps.newHashMap();

        deliveryNoteList = deliveryNoteList.stream().sorted(Comparator.comparing(note -> note.get("create_time", Long.class))).collect(Collectors.toList());
        deliveryNoteList.forEach(deliveryNote -> {
            String deliveryNoteId = deliveryNote.getId();
            List<IObjectData> deliveryNoteProducts = deliveryNoteProductList.stream()
                    .filter(product -> Objects.equals(deliveryNoteId, product.get(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, String.class)))
                    .collect(Collectors.toList());

            // 未发货状态
            String deliveryNoteLifeStatus = deliveryNote.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            boolean isUndeliveredStatus = Objects.equals(SystemConstants.LifeStatus.Invalid.value, deliveryNoteLifeStatus)
                    || Objects.equals(SystemConstants.LifeStatus.Ineffective.value, deliveryNoteLifeStatus);

            // 设置发货单产品本次发货金额及平均单价
            deliveryNoteProducts.forEach(productObjectData -> {
                String productId = productObjectData.get(DeliveryNoteProductObjConstants.Field.ProductId.apiName, String.class);
                BigDecimal deliveryNum = productObjectData.get(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, BigDecimal.class);
                BigDecimal avePrice = productId2OrderProduct.get(productId).avgPrice();
                BigDecimal orderAllAmount = productId2OrderProduct.get(productId).getAllAmount();
                BigDecimal orderAllSubTotal = productId2OrderProduct.get(productId).getAllSubTotal();
                BigDecimal deliveryMoney = avePrice.multiply(deliveryNum).setScale(2, BigDecimal.ROUND_HALF_UP);
                if (isUndeliveredStatus) {
                    productObjectData.set(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, deliveryMoney);
                } else {
                    BigDecimal hasDeliveredNum = product2HasDeliveredNum.getOrDefault(productId, BigDecimal.ZERO);
                    BigDecimal hasDeliveredMoney = product2HasDeliveredMoney.getOrDefault(productId, BigDecimal.ZERO);
                    boolean isLastDeliver = hasDeliveredNum.add(deliveryNum).compareTo(orderAllAmount) >= 0;
                    if (isLastDeliver) {
                        deliveryMoney = orderAllSubTotal.subtract(hasDeliveredMoney);
                    }
                    productObjectData.set(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, deliveryMoney);

                    product2HasDeliveredNum.putIfAbsent(productId, BigDecimal.ZERO);
                    product2HasDeliveredNum.put(productId, hasDeliveredNum.add(deliveryNum));
                    product2HasDeliveredMoney.putIfAbsent(productId, BigDecimal.ZERO);
                    product2HasDeliveredMoney.put(productId, hasDeliveredMoney.add(deliveryMoney));
                }

                productObjectData.set(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName, avePrice);
            });

            // 设置发货单发货总金额
            BigDecimal totalDeliveryMoney = BigDecimal.ZERO;
            for (IObjectData productObjectData : deliveryNoteProducts) {
                BigDecimal deliveryMoney = productObjectData.get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class);
                totalDeliveryMoney = totalDeliveryMoney.add(deliveryMoney);
            }
            deliveryNote.set(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, totalDeliveryMoney);
        });
    }
}