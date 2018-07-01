package com.facishare.crm.sfainterceptor.predefine.service.impl;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.crm.erpstock.predefine.manager.ErpStockManager;
import com.facishare.crm.rest.dto.CheckBulkAddOrderModel;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.sfainterceptor.predefine.manager.SalesOrderInterceptorErpStockManager;
import com.facishare.crm.sfainterceptor.predefine.manager.SalesOrderInterceptorStockManager;
import com.facishare.crm.sfainterceptor.predefine.service.SalesOrderInterceptorService;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkInvalid.BulkInvalidAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkInvalid.BulkInvalidBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkRecover.BulkRecoverAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkRecover.BulkRecoverBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.common.SalesOrderProductVo;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd.SalesOrderBulkAddAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd.SalesOrderBulkAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd.SalesOrderBulkAddTransactionAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit.SalesOrderEditAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit.SalesOrderEditBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit.SalesOrderEditFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.invalid.SalesOrderInvalidAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.invalid.SalesOrderInvalidBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.invalid.SalesOrderInvalidFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.recover.SalesOrderRecoverAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.recover.SalesOrderRecoverBeforeModel;
import com.facishare.crm.sfainterceptor.utils.SfainterceptorUtils;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.ProductManager;
import com.facishare.crm.stock.predefine.manager.SaleOrderManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.crm.util.BeanUtils;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "sfainterceptorAccess")
public class SalesOrderInterceptorServiceImpl implements SalesOrderInterceptorService {
    @Resource
    private StockManager stockManager;

    @Resource
    private SalesOrderInterceptorStockManager salesOrderInterceptorStockManager;

    @Resource
    private SalesOrderInterceptorErpStockManager salesOrderInterceptorErpStockManager;

    @Resource
    private DeliveryNoteManager deliveryNoteManager;

    @Resource
    private WareHouseManager wareHouseManager;

    @Resource
    private ProductManager productManager;

    @Resource
    private SaleOrderManager saleOrderManager;

    @Resource
    private ErpStockManager erpStockManager;
    //注意，做下面操作之前先判断对象（库存？等）是否已经创建了，创建的话再做检验。
    //仓库不为空才做拦截

    @Override
    public SalesOrderAddBeforeModel.Result addBefore(ServiceContext context, SalesOrderAddBeforeModel.Arg arg) {
        //是否开启ERP库存
        if (erpStockManager.isErpStockEnable(context.getTenantId())) {
            log.info("ERP salesOrderAddBefore. context[{}], arg[{}]", context, arg);
            if (CollectionUtils.isEmpty(arg.getSalesOrderVo().getSalesOrderProductVos())) {
                return new SalesOrderAddBeforeModel.Result();
            }
            salesOrderInterceptorErpStockManager.checkOrderAndAvailableStock(context.getUser(), arg.getSalesOrderVo().getSalesOrderProductVos());
        } else {
            SalesOrderAddBeforeModel.Result result = new SalesOrderAddBeforeModel.Result();
            if (stockManager.isStockEnable(context.getTenantId())) {
                log.info("salesOrderAddBefore. context[{}], arg[{}]", context, arg);

                context = SfainterceptorUtils.outUser2Admin(context);
                //创建前校验订单
                if (StringUtils.isBlank(arg.getSalesOrderVo().getWarehouseId()) && !stockManager.isAllWarehouseOrder(context.getTenantId())) {
                    throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "指定仓库订货，仓库不能为空");
                }

                String warehouseId = salesOrderInterceptorStockManager.checkOrderAndAvailableStock(context.getUser(), arg.getSalesOrderVo().getSalesOrderProductVos(), arg.getSalesOrderVo().getWarehouseId(), arg.getSalesOrderVo().getCustomerId(), true);
                if (!Objects.equals(warehouseId, arg.getSalesOrderVo().getWarehouseId())) {
                    result.setWarehouseId(warehouseId);
                }
            } else if (deliveryNoteManager.isDeliveryNoteEnable(context.getTenantId())) {
                //临时关闭库存的企业 订单页面有可能存在订货仓库字段 （允许填写会造成发货单新建失败）
                if (StringUtils.isNotBlank(arg.getSalesOrderVo().getWarehouseId())) {
                    throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存未开启，不能填写订货仓库");
                }
            }
            return result;
        }
        return new SalesOrderAddBeforeModel.Result();
    }
    @Override
    public SalesOrderAddAfterModel.Result addAfter(ServiceContext context, SalesOrderAddAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderAddAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                    || Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)) {
                //校验订单增加冻结库存
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.ADD.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();
                if (Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)) {
                    info.setOperateResult(StockOperateResultEnum.IN_APPROVAL.value);
                }
                salesOrderInterceptorStockManager.checkOrderAndAddBlockedStock(context.getUser(), arg.getDataId(), info);
            }
        }
        return new SalesOrderAddAfterModel.Result();
    }

    @Override
    public SalesOrderAddFlowCompletedAfterModel.Result addFlowCompletedAfter(ServiceContext context, SalesOrderAddFlowCompletedAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderAddFlowCompletedAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Ineffective.value)) {
                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.ADD_FLOW_COMPLETE.value)
                        .beforeLifeStatus(SystemConstants.LifeStatus.UnderReview.value)
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.REJECT.value).build();

                salesOrderInterceptorStockManager.minusBlockedStock(context.getUser(), arg.getDataId(), stockOperateInfo);
            }
        }
        return new SalesOrderAddFlowCompletedAfterModel.Result();
    }

    @Override
    public SalesOrderEditBeforeModel.Result editBefore(ServiceContext context, SalesOrderEditBeforeModel.Arg arg) {
        //ERP 库存是否开启
        if (erpStockManager.isErpStockEnable(context.getTenantId())) {
            log.info("ERP salesOrderEditBefore. context[{}], arg[{}]", context, arg);
            if (CollectionUtils.isEmpty(arg.getSalesOrderVo().getSalesOrderProductVos())) {
                return new SalesOrderEditBeforeModel.Result();
            }
            if (Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.Ineffective.value) || Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)) {
                salesOrderInterceptorErpStockManager.checkOrderAndAvailableStock(context.getUser(), arg.getSalesOrderVo().getSalesOrderProductVos());
            }
        } else {
            SalesOrderEditBeforeModel.Result result = new SalesOrderEditBeforeModel.Result();
            //纷享库存是否开启
            if (stockManager.isStockEnable(context.getTenantId())) {
                context = SfainterceptorUtils.outUser2Admin(context);

                //未生效
                if (Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.Ineffective.value)) {
                    //编辑提交前校验订单
                    if (StringUtils.isBlank(arg.getSalesOrderVo().getWarehouseId()) && !stockManager.isAllWarehouseOrder(context.getTenantId())) {
                        throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "指定仓库订货，仓库不能为空");
                    }
                    String warehouseId = salesOrderInterceptorStockManager.checkOrderAndAvailableStock(context.getUser(), arg.getSalesOrderVo().getSalesOrderProductVos(), arg.getSalesOrderVo().getWarehouseId(), arg.getSalesOrderVo().getCustomerId(), true);
                    if (!Objects.equals(warehouseId, arg.getSalesOrderVo().getWarehouseId())) {
                        //回填仓库
                        result.setWarehouseId(warehouseId);
                    }
                } else {
                    //其他状态编辑
                    SalesOrderModel.SalesOrderVo salesOrderVo = saleOrderManager.getById(context.getUser(), arg.getSalesOrderVo().getTradeId());
                    //校验仓库是否修改
                    String warehouseId = salesOrderInterceptorStockManager.checkBeforeEdit(context.getUser(), arg, salesOrderVo);
                    result.setWarehouseId(warehouseId);

                    List<SalesOrderModel.SalesOrderProductVO> oldProductVOs = productManager.getProductsByOrderId(context.getUser(), arg.getSalesOrderVo().getTradeId(), false);

                    //订单产品数量、种类、销售单价是否修改
                    boolean isProductModified = salesOrderInterceptorStockManager.isProductsModified(arg.getSalesOrderVo().getSalesOrderProductVos(), oldProductVOs);
                    //产品有修改
                    if (isProductModified) {
                        //订单是否关联有效发货单
                        if (!salesOrderInterceptorStockManager.isAllDeliveryNoteInvalid(context.getUser(), arg.getSalesOrderVo().getTradeId())) {
                            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "订单已关联发货单，订单产品种类、数量、销售单价不可修改");
                        }
                    }
                    //没有订货仓库的不处理
                    if (warehouseId != null) {
                        if (Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                                || Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)
                                || Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.InChange.value)) {

                            //修改产品数量
                            if (isProductModified) {
                                List<String> allowSalesOrderIds = Arrays.asList(ConfigCenter.ALLOW_MODIFIED_SALES_ORDER_IDS.split(";"));

                                //由于订单编辑是通过库存操作记录表 计算已冻结库存数据 对于库存操作记录不完整的订单 不允许修改
                                //订单创建时间 大于 库存记录的最早时间 || 订单白名单
                                if (salesOrderVo.getCreateTime().compareTo(Long.parseLong(ConfigCenter.EARLIEST_STOCK_LOG_CREATE_TIME)) >= 0
                                        || allowSalesOrderIds.contains(arg.getSalesOrderVo().getTradeId())) {

                                    Map<String, List<SalesOrderProductVo>> modifiedProductsVosMap = salesOrderInterceptorStockManager.getModifiedProductsVos(context.getUser(), arg.getSalesOrderVo().getSalesOrderProductVos(), oldProductVOs, true);
                                    //新增的产品 增量
                                    List<SalesOrderProductVo> newAddProductsVos = modifiedProductsVosMap.get("addProducts");

                                    //有新增的产品 或者有新增数量 校验新增产品的可用库存
                                    if (!CollectionUtils.isEmpty(newAddProductsVos)) {
                                        warehouseId = salesOrderInterceptorStockManager.checkOrderAndAvailableStock(context.getUser(), newAddProductsVos, warehouseId, arg.getSalesOrderVo().getCustomerId(), false);
                                        //回填仓库
                                        result.setWarehouseId(warehouseId);
                                    }
                                } else {
                                    throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "该订单暂不支持手动编辑，如需编辑请联系纷享客服：400-1869-000");
                                }
                            }
                        }
                    }

                }
            } else if (deliveryNoteManager.isDeliveryNoteEnable(context.getTenantId())) {
                if (!Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.Ineffective.value)) {
                    List<SalesOrderModel.SalesOrderProductVO> oldProductVOs = productManager.getProductsByOrderId(context.getUser(), arg.getSalesOrderVo().getTradeId(), false);

                    //订单产品数量、种类、销售单价是否修改
                    if (salesOrderInterceptorStockManager.isProductsModified(arg.getSalesOrderVo().getSalesOrderProductVos(), oldProductVOs)) {
                        //订单是否关联有效发货单
                        if (!salesOrderInterceptorStockManager.isAllDeliveryNoteInvalid(context.getUser(), arg.getSalesOrderVo().getTradeId())) {
                            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "订单已关联发货单，订单产品种类、数量、销售单价不可修改");
                        }
                    }
                } else {
                    //临时关闭库存的企业 订单页面有可能存在订货仓库字段 （允许填写会造成发货单新建失败）
                    if (StringUtils.isNotBlank(arg.getSalesOrderVo().getWarehouseId())) {
                        throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存未开启，不能填写订货仓库");
                    }
                }
            }
            return result;
        }
        return new SalesOrderEditBeforeModel.Result();
    }

    @Override
    public SalesOrderEditAfterModel.Result editAfter(ServiceContext context, SalesOrderEditAfterModel.Arg arg) {

        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderEditAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);

            //对于新建类型
            //ineffective -> normal  || ineffective -> under_review
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Ineffective.value)
                    && (Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                    || Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.UnderReview.value))) {

                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.EDIT.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                if (Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)) {
                    info.setOperateResult(StockOperateResultEnum.IN_APPROVAL.value);
                }

                salesOrderInterceptorStockManager.checkOrderAndAddBlockedStock(context.getUser(), arg.getDataId(), info);
            } else {
                SalesOrderModel.SalesOrderVo salesOrderVo = saleOrderManager.getById(context.getUser(), arg.getDataId());

                //仓库不存在不处理
                if (StringUtils.isBlank(salesOrderVo.getWarehouseId())) {
                    return new SalesOrderEditAfterModel.Result();
                }

                //normal -> normal  || under_review -> under_review  || in_change -> in_change
                if ((Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Normal.value) && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value))
                        || (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.UnderReview.value) && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.UnderReview.value))
                        || (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.InChange.value) && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.InChange.value))) {

                    Map<String, List<SalesOrderProductVo>> modifiedProductsVosMap = salesOrderInterceptorStockManager.getModifiedProductsVosByBlockedStock(context.getUser(), arg.getDataId());
                    List<SalesOrderProductVo> addProductVos = modifiedProductsVosMap.get("addProducts");
                    List<SalesOrderProductVo> minusProductVos = modifiedProductsVosMap.get("minusProducts");

                    StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.EDIT.value)
                            .beforeLifeStatus(arg.getBeforeLifeStatus())
                            .afterLifeStatus(arg.getAfterLifeStatus())
                            .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                            .operateResult(StockOperateResultEnum.PASS.value).build();


                    //增量的产品 增加冻结库存
                    if (!CollectionUtils.isEmpty(addProductVos)) {
                        salesOrderInterceptorStockManager.checkOrderAndAddBlockedStockByAddProducts(context.getUser(), salesOrderVo, addProductVos, info);
                    }

                    //减少的产品 释放冻结库存
                    if (!CollectionUtils.isEmpty(minusProductVos)) {
                        salesOrderInterceptorStockManager.minusBlockedStockByMinusProducts(context.getUser(), salesOrderVo, minusProductVos, info);
                    }
                }

                //normal -> in_change
                if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                        && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.InChange.value)) {

                    List<SalesOrderModel.SalesOrderProductVO> oldProductVOs = productManager.getProductsByOrderId(context.getUser(), arg.getSalesOrderVo().getTradeId(), true);

                    Map<String, List<SalesOrderProductVo>> modifiedProductsVosMap = salesOrderInterceptorStockManager.getModifiedProductsVos(context.getUser(), arg.getSalesOrderVo().getSalesOrderProductVos(), oldProductVOs, false);
                    List<SalesOrderProductVo> addProductVos = modifiedProductsVosMap.get("addProducts");

                    StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.EDIT.value)
                            .beforeLifeStatus(arg.getBeforeLifeStatus())
                            .afterLifeStatus(arg.getAfterLifeStatus())
                            .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                            .operateResult(StockOperateResultEnum.IN_APPROVAL.value).build();

                    //增量的产品 增加冻结库存
                    if (!CollectionUtils.isEmpty(addProductVos)) {
                        salesOrderInterceptorStockManager.checkOrderAndAddBlockedStockByAddProducts(context.getUser(), salesOrderVo, addProductVos, info);
                    }
                }
            }
        }

        return new SalesOrderEditAfterModel.Result();
    }

    @Override
    public SalesOrderEditFlowCompletedAfterModel.Result editFlowCompletedAfter(ServiceContext context, SalesOrderEditFlowCompletedAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderEditFlowCompletedAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);

            //under_review -> ineffective
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Ineffective.value)) {

                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.EDIT_FLOW_COMPLETE.value)
                        .beforeLifeStatus(SystemConstants.LifeStatus.UnderReview.value)
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.REJECT.value).build();

                salesOrderInterceptorStockManager.minusBlockedStock(context.getUser(), arg.getDataId(), stockOperateInfo);
            }

            //in_change -> normal
            //由于订单获取不到编辑变更前的状态，编辑前的状态写死成under_review  所以这里为under_review
            //未生效的订单 编辑  under_review->normal走的是addFlowComplete
            //以下逻辑只释放多增加的冻结库存 无影响
            if ((Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)|| Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.InChange.value))
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                //仓库不存在不处理
                SalesOrderModel.SalesOrderVo salesOrderVo = saleOrderManager.getById(context.getUser(), arg.getDataId());
                if (StringUtils.isBlank(salesOrderVo.getWarehouseId())) {
                    return new SalesOrderEditAfterModel.Result();
                }

                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.EDIT_FLOW_COMPLETE.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                Map<String, List<SalesOrderProductVo>> modifyProductsMap = salesOrderInterceptorStockManager.getModifiedProductsVosByBlockedStock(context.getUser(), arg.getDataId());
                List<SalesOrderProductVo> minusProducts = modifyProductsMap.get("minusProducts");
                if (!CollectionUtils.isEmpty(minusProducts)) {
                    salesOrderInterceptorStockManager.minusBlockedStockByMinusProducts(context.getUser(), salesOrderVo, minusProducts, stockOperateInfo);
                }
            }
        }

        return new SalesOrderEditFlowCompletedAfterModel.Result();
    }

    @Override
    public SalesOrderInvalidBeforeModel.Result invalidBefore(ServiceContext context, SalesOrderInvalidBeforeModel.Arg arg) {
        //所有的发货单是否'已作废'
        context = SfainterceptorUtils.outUser2Admin(context);

        if (deliveryNoteManager.isDeliveryNoteEnable(context.getTenantId())) {
            List<String> salesOrderIds = Arrays.asList(arg.getDataId());
            deliveryNoteManager.checkAllDeliveryNoteIsInvalid(context.getUser(), salesOrderIds);
        }
        return new SalesOrderInvalidBeforeModel.Result();
    }

    @Override
    public SalesOrderInvalidAfterModel.Result invalidAfter(ServiceContext context, SalesOrderInvalidAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderInvalidAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Invalid.value)) {
                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.INVALID.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                salesOrderInterceptorStockManager.minusBlockedStock(context.getUser(), arg.getDataId(), stockOperateInfo);
            }
        }

        return new SalesOrderInvalidAfterModel.Result();
    }

    @Override
    public SalesOrderInvalidFlowCompletedAfterModel.Result invalidFlowCompletedAfter(ServiceContext context, SalesOrderInvalidFlowCompletedAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderInvalidFlowCompletedAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.InChange.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Invalid.value)) {
                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.INVALID_FLOW_COMPLETE.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                salesOrderInterceptorStockManager.minusBlockedStock(context.getUser(), arg.getDataId(), stockOperateInfo);
            }
        }
        return new SalesOrderInvalidFlowCompletedAfterModel.Result();
    }

    @Override
    public SalesOrderRecoverBeforeModel.Result recoverBefore(ServiceContext context, SalesOrderRecoverBeforeModel.Arg arg) {
        //是否开启ERP库存
        if (erpStockManager.isErpStockEnable(context.getTenantId())) {
            log.info("ERP salesOrderRecoverBefore. context[{}], arg[{}]", context, arg);
            if (Objects.equals(arg.getRecoverToStatus(), SystemConstants.LifeStatus.Normal.value)) {
                salesOrderInterceptorErpStockManager.checkOrderAndAvailableStockByDataId(context.getUser(), arg.getDataId());
            }
        } else {
            if (stockManager.isStockEnable(context.getTenantId())) {
                log.info("salesOrderRecoverBefore. context[{}], arg[{}]", context, arg);

                context = SfainterceptorUtils.outUser2Admin(context);
                //订单校验  作废前：正常
                if (Objects.equals(arg.getRecoverToStatus(), SystemConstants.LifeStatus.Normal.value)) {
                    salesOrderInterceptorStockManager.checkOrderAndAvailableStockByDataId(context.getUser(), arg.getDataId());
                }
            }
        }
        return new SalesOrderRecoverBeforeModel.Result();
    }

    @Override
    @Transactional
    public SalesOrderRecoverAfterModel.Result recoverAfter(ServiceContext context, SalesOrderRecoverAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderRecoverAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            //订单校验 锁定库存
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Invalid.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {

                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.RECOVER.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                salesOrderInterceptorStockManager.checkOrderAndAddBlockedStock(context.getUser(), arg.getDataId(), info);
            }
        }
        return new SalesOrderRecoverAfterModel.Result();
    }

    @Override
    @Transactional
    public BulkRecoverBeforeModel.Result bulkRecoverBefore(ServiceContext context, BulkRecoverBeforeModel.Arg arg) {
        if (erpStockManager.isErpStockEnable(context.getTenantId())) {
            log.info("ERP salesOrderBulkRecoverBefore. context[{}], arg[{}]", context, arg);
            context = SfainterceptorUtils.outUser2Admin(context);
            List<BulkRecoverBeforeModel.BulkObj> validBulkObjs = arg.getBulkObjs().stream().filter(bulkObj -> Objects.equals(bulkObj.getRecoverToStatus(), SystemConstants.LifeStatus.Normal.value)).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(validBulkObjs)) {
                List<String> validOrderIds = validBulkObjs.stream().map(BulkRecoverBeforeModel.BulkObj :: getDataId).collect(Collectors.toList());
                salesOrderInterceptorErpStockManager.checkOrderAvailableStockByDataIds(context.getUser(), validOrderIds);
            }
        } else {
            if (stockManager.isStockEnable(context.getTenantId())) {
                log.info("salesOrderBulkRecoverBefore. context[{}], arg[{}]", context, arg);

                context = SfainterceptorUtils.outUser2Admin(context);

                List<BulkRecoverBeforeModel.BulkObj> validBulkObjs = arg.getBulkObjs().stream().filter(bulkObj -> Objects.equals(bulkObj.getRecoverToStatus(), SystemConstants.LifeStatus.Normal.value)).collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(validBulkObjs)) {
                    List<String> validOrderIds = validBulkObjs.stream().map(BulkRecoverBeforeModel.BulkObj :: getDataId).collect(Collectors.toList());
                    salesOrderInterceptorStockManager.checkOrderAvailableStockByDataIds(context.getUser(), validOrderIds);
                }

            }
        }
        return new BulkRecoverBeforeModel.Result();
    }

    @Override
    public BulkRecoverAfterModel.Result bulkRecoverAfter(ServiceContext context, BulkRecoverAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderBulkRecoverAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);

            if (!CollectionUtils.isEmpty(arg.getBulkObjs())) {
                List<BulkRecoverAfterModel.BulkObj> bulkObjs = arg.getBulkObjs().stream()
                        .filter(bulkObj -> Objects.equals(bulkObj.getBeforeLifeStatus(), SystemConstants.LifeStatus.Invalid.value)
                                && Objects.equals(bulkObj.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value))
                        .collect(Collectors.toList());

                List<String> dataIds = bulkObjs.stream().map(BulkRecoverAfterModel.BulkObj::getDataId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(dataIds)) {
                    StockOperateInfo info = StockOperateInfo.builder().operateType(StockOperateTypeEnum.RECOVER.value)
                            .beforeLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                            .afterLifeStatus( SystemConstants.LifeStatus.Normal.value)
                            .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                            .operateResult(StockOperateResultEnum.PASS.value).build();

                    salesOrderInterceptorStockManager.batchCheckOrderAndAddBlockedStock(context.getUser(), dataIds, info);
                }

            }
        }
        return new BulkRecoverAfterModel.Result();
    }

    @Override
    public BulkInvalidBeforeModel.Result bulkInvalidBefore(ServiceContext context, BulkInvalidBeforeModel.Arg arg) {
        //所有的发货单是否'已作废'
        context = SfainterceptorUtils.outUser2Admin(context);

        if (deliveryNoteManager.isDeliveryNoteEnable(context.getTenantId())) {
            List<BulkInvalidBeforeModel.BulkObj> bulkObjs = arg.getBulkObjs();
            List<String> salesOrderIds = bulkObjs.stream().map(BulkInvalidBeforeModel.BulkObj::getDataId).collect(Collectors.toList());
            deliveryNoteManager.checkAllDeliveryNoteIsInvalid(context.getUser(), salesOrderIds);
        }
        return new BulkInvalidBeforeModel.Result();
    }

    @Override
    @Transactional
    public BulkInvalidAfterModel.Result bulkInvalidAfter(ServiceContext context, BulkInvalidAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("salesOrderBulkInvalidAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);

            if (!CollectionUtils.isEmpty(arg.getBulkObjs())) {
                List<String> dataIds = arg.getBulkObjs().stream()
                        .filter(bulkObj -> Objects.equals(bulkObj.getBeforeLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                                && Objects.equals(bulkObj.getAfterLifeStatus(), SystemConstants.LifeStatus.Invalid.value))
                        .map(BulkInvalidAfterModel.BulkObj :: getDataId).collect(Collectors.toList());

                StockOperateInfo info = StockOperateInfo.builder().operateType(StockOperateTypeEnum.INVALID.value)
                        .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                        .afterLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                        .operateObjectType(StockOperateObjectTypeEnum.SALES_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();
                //批量扣减冻结库存
                salesOrderInterceptorStockManager.batchMinusBlockedStock(context.getUser(), dataIds, info);

            }
        }
        return new BulkInvalidAfterModel.Result();
    }

    @Override
    public SalesOrderBulkAddBeforeModel.Result bulkAddBefore(ServiceContext context, SalesOrderBulkAddBeforeModel.Arg arg) {
        SalesOrderBulkAddBeforeModel.Result checkResult = new SalesOrderBulkAddBeforeModel.Result();
        if (!CollectionUtils.isEmpty(arg.getMixtureVos())) {
            List<SalesOrderBulkAddBeforeModel.MixtureVo> mixtureVos = arg.getMixtureVos();
            List<SalesOrderBulkAddBeforeModel.MixtureResult> success = mixtureVos.stream().map(mixtureVo -> {
                SalesOrderBulkAddBeforeModel.MixtureResult result = new SalesOrderBulkAddBeforeModel.MixtureResult();
                result.setId(mixtureVo.getId());
                result.setErrCode(StockErrorCode.OK.getStringCode());
                result.setIsSalesOrderFail(false);
                result.setTradeId(mixtureVo.getTradeId());
                result.setTradeProductId(mixtureVo.getTradeProductId());
                return result;
            }).collect(Collectors.toList());
            checkResult.setSuccessResults(success);

            if (stockManager.isStockEnable(context.getTenantId())) {
                log.info("salesOrderBulkAddBefore. context[{}], arg[{}]", context, arg);

                if (arg.getIsCheckSalesOrder()) {
                    //校验仓库是否存在   校验客户与仓库是否适用
                    CheckBulkAddOrderModel.Result result = checkWarehouseAndCustomerId(context.getUser(), mixtureVos);
                    log.info("checkWarehouseAndCustomerId success. result[{}]", result);
                    List<SalesOrderBulkAddBeforeModel.MixtureResult> successResult = result.getSuccessResult().stream().map(detailResult -> {
                        SalesOrderBulkAddBeforeModel.MixtureResult mixtureResult = new SalesOrderBulkAddBeforeModel.MixtureResult();
                        BeanUtils.copyProperties(mixtureResult, detailResult);
                        return mixtureResult;
                    }).collect(Collectors.toList());

                    List<SalesOrderBulkAddBeforeModel.MixtureResult> failedResult = result.getFailedResult().stream().map(detailResult -> {
                        SalesOrderBulkAddBeforeModel.MixtureResult mixtureResult = new SalesOrderBulkAddBeforeModel.MixtureResult();
                        BeanUtils.copyProperties(mixtureResult, detailResult);
                        return mixtureResult;
                    }).collect(Collectors.toList());

                    checkResult.setSuccessResults(successResult);
                    checkResult.setFailResults(failedResult);
                }
            }

        }
        return checkResult;
    }

//    @Override
//    public SalesOrderBulkAddDoActModel.Result BulkAddDoAct(ServiceContext context, SalesOrderBulkAddDoActModel.Arg arg) {
//        return null;
//    }

    @Override
    public SalesOrderBulkAddAfterModel.Result bulkAddAfter(ServiceContext context, SalesOrderBulkAddAfterModel.Arg arg) {
        return new SalesOrderBulkAddAfterModel.Result();
    }

    @Override
    public SalesOrderBulkAddTransactionAfterModel.Result bulkAddTransaction(ServiceContext context,
                                                                            SalesOrderBulkAddTransactionAfterModel.Arg arg) {
        return new SalesOrderBulkAddTransactionAfterModel.Result();
    }

    private CheckBulkAddOrderModel.Result checkWarehouseAndCustomerId(User user, List<SalesOrderBulkAddBeforeModel.MixtureVo> mixtureVos) {
        List<CheckBulkAddOrderModel.Arg> args = mixtureVos.stream().map(mixtureVo -> {
            CheckBulkAddOrderModel.Arg arg = new CheckBulkAddOrderModel.Arg();
            arg.setWarehouseName(mixtureVo.getWarehouseName());
            arg.setCustomerId(mixtureVo.getCustomerId());
            arg.setTradeId(mixtureVo.getTradeId());
            arg.setTradeProductId(mixtureVo.getTradeProductId());
            arg.setId(mixtureVo.getId());
            return arg;
        }).collect(Collectors.toList());

        return wareHouseManager.checkWarehouseAndCustomerId(user, args);
    }
}
