package com.facishare.crm.sfainterceptor.predefine.service.impl;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.rest.dto.CheckBulkAddOrderModel;
import com.facishare.crm.rest.dto.ReturnOrderModel;
import com.facishare.crm.sfainterceptor.predefine.manager.ReturnedGoodsInvoiceInterceptorStockManager;
import com.facishare.crm.sfainterceptor.predefine.service.ReturnedGoodsInvoiceInterceptorService;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.add.ReturnedGoodsInvoiceAddAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.add.ReturnedGoodsInvoiceAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.add.ReturnedGoodsInvoiceAddFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.bulkAdd.ReturnedGoodsInvoiceBulkAddAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.bulkAdd.ReturnedGoodsInvoiceBulkAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.bulkAdd.ReturnedGoodsInvoiceBulkAddTransactionAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.edit.ReturnedGoodsInvoiceEditAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.edit.ReturnedGoodsInvoiceEditBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.edit.ReturnedGoodsInvoiceEditFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.invalid.ReturnedGoodsInvoiceInvalidAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.invalid.ReturnedGoodsInvoiceInvalidBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.invalid.ReturnedGoodsInvoiceInvalidFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.recover.ReturnedGoodsInvoiceRecoverAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.recover.ReturnedGoodsInvoiceRecoverBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkInvalid.BulkInvalidAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkInvalid.BulkInvalidBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkRecover.BulkRecoverAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkRecover.BulkRecoverBeforeModel;
import com.facishare.crm.sfainterceptor.utils.SfainterceptorUtils;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.ReturnOrderManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.crm.util.BeanUtils;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Service
@Slf4j(topic = "sfainterceptorAccess")
public class ReturnedGoodsInvoiceInterceptorServiceImpl implements ReturnedGoodsInvoiceInterceptorService {
    @Resource
    private StockManager stockManager;

    @Resource
    private ReturnOrderManager returnOrderManager;

    @Resource
    private WareHouseManager wareHouseManager;

    @Resource
    private ReturnedGoodsInvoiceInterceptorStockManager returnedGoodsInvoiceInterceptorStockManager;

    //注意，做下面操作之前先判断对象（库存？等）是否已经创建了，创建的话再做检验。
    @Override
    public ReturnedGoodsInvoiceAddBeforeModel.Result addBefore(ServiceContext context, ReturnedGoodsInvoiceAddBeforeModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceAddBefore. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (arg.getReturnedGoodsInvoiceVo() != null && StringUtils.isNotBlank(arg.getReturnedGoodsInvoiceVo().getWarehouseId())) {
                wareHouseManager.checkWarehouseEnable(context.getUser(), arg.getReturnedGoodsInvoiceVo().getWarehouseId());
//                returnedGoodsInvoiceInterceptorStockManager.checkReturnedProductsOutNumber(context.getUser(), arg.getReturnedGoodsInvoiceVo().getTradeId());
            }
        }
        return new ReturnedGoodsInvoiceAddBeforeModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceEditBeforeModel.Result editBefore(ServiceContext context, ReturnedGoodsInvoiceEditBeforeModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceEditBefore. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (!Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.Ineffective.value)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "开启库存后，退货单确认后不能编辑");
            }

            if (arg.getReturnedGoodsInvoiceVo() != null && StringUtils.isNotBlank(arg.getReturnedGoodsInvoiceVo().getWarehouseId())) {
                wareHouseManager.checkWarehouseEnable(context.getUser(), arg.getReturnedGoodsInvoiceVo().getWarehouseId());
//                returnedGoodsInvoiceInterceptorStockManager.checkReturnedProductsOutNumber(context.getUser(), arg.getReturnedGoodsInvoiceVo().getTradeId());
            }
        }
        return new ReturnedGoodsInvoiceEditBeforeModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceAddAfterModel.Result addAfter(ServiceContext context,
                                                             ReturnedGoodsInvoiceAddAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceAddAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            //增加实际库存
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Ineffective.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.ADD.value)
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value).operateResult(StockOperateResultEnum.PASS.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus()).build();

                returnedGoodsInvoiceInterceptorStockManager.addRealStock(context.getUser(), arg.getDataId(), info);
            }
        }

        return new ReturnedGoodsInvoiceAddAfterModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceAddFlowCompletedAfterModel.Result addFlowCompletedAfter(ServiceContext context,
                                                                                       ReturnedGoodsInvoiceAddFlowCompletedAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceAddFlowCompletedAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            //增加实际库存
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.ADD_FLOW_COMPLETE.value)
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value).operateResult(StockOperateResultEnum.PASS.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus()).build();

                returnedGoodsInvoiceInterceptorStockManager.addRealStock(context.getUser(), arg.getDataId(), info);
            }
        }

        return new ReturnedGoodsInvoiceAddFlowCompletedAfterModel.Result();
    }


    @Override
    public ReturnedGoodsInvoiceEditAfterModel.Result editAfter(ServiceContext context,
                                                               ReturnedGoodsInvoiceEditAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceEditAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            //增加实际库存
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Ineffective.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.EDIT.value)
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value).operateResult(StockOperateResultEnum.PASS.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus()).build();

                returnedGoodsInvoiceInterceptorStockManager.addRealStock(context.getUser(), arg.getDataId(), info);
            }
        }

        return new ReturnedGoodsInvoiceEditAfterModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceEditFlowCompletedAfterModel.Result editFlowCompletedAfter(ServiceContext context,
                                                                                         ReturnedGoodsInvoiceEditFlowCompletedAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceEditFlowCompletedAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            //增加实际库存
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.UnderReview.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.EDIT_FLOW_COMPLETE.value)
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value).operateResult(StockOperateResultEnum.PASS.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus()).build();

                returnedGoodsInvoiceInterceptorStockManager.addRealStock(context.getUser(), arg.getDataId(), info);
            }
        }

        return new ReturnedGoodsInvoiceEditFlowCompletedAfterModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceInvalidBeforeModel.Result invalidBefore(ServiceContext context,
                                                                       ReturnedGoodsInvoiceInvalidBeforeModel.Arg arg) {

        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceInvalidBefore. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (Objects.equals(arg.getNowLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                //校验可用库存是否满足
                returnedGoodsInvoiceInterceptorStockManager.checkAvailableStock(context.getUser(), arg.getDataId());
            }
        }

        return new ReturnedGoodsInvoiceInvalidBeforeModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceInvalidAfterModel.Result invalidAfter(ServiceContext context,
                                                                     ReturnedGoodsInvoiceInvalidAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceInvalidAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Invalid.value)) {
                //扣减实际库存
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.INVALID.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                returnedGoodsInvoiceInterceptorStockManager.minusStock(context.getUser(), arg.getDataId(), false, true, info);

            } else if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.InChange.value)) {
                //增加冻结库存
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.INVALID.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value)
                        .operateResult(StockOperateResultEnum.IN_APPROVAL.value).build();

                returnedGoodsInvoiceInterceptorStockManager.addBlockedStock(context.getUser(), arg.getDataId(), info);
            }
        }

        return new ReturnedGoodsInvoiceInvalidAfterModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceInvalidFlowCompletedAfterModel.Result invalidFlowCompletedAfter(ServiceContext context,
                                                                                               ReturnedGoodsInvoiceInvalidFlowCompletedAfterModel.Arg arg) {

        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceInvalidFlowCompletedAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);
            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.InChange.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Invalid.value)) {
                //扣减实际和冻结库存
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.INVALID_FLOW_COMPLETE.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                returnedGoodsInvoiceInterceptorStockManager.minusStock(context.getUser(), arg.getDataId(), true, true, info);
            } else if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.InChange.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                //扣减冻结库存
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.INVALID_FLOW_COMPLETE.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus())
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value)
                        .operateResult(StockOperateResultEnum.REJECT.value).build();

                returnedGoodsInvoiceInterceptorStockManager.minusStock(context.getUser(), arg.getDataId(), true, false, info);
            }
        }
        return new ReturnedGoodsInvoiceInvalidFlowCompletedAfterModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceRecoverBeforeModel.Result recoverBefore(ServiceContext context,
                                                                       ReturnedGoodsInvoiceRecoverBeforeModel.Arg arg) {
        return new ReturnedGoodsInvoiceRecoverBeforeModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceRecoverAfterModel.Result recoverAfter(ServiceContext context,
                                                                     ReturnedGoodsInvoiceRecoverAfterModel.Arg arg) {

        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceRecoverAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);

            if (Objects.equals(arg.getBeforeLifeStatus(), SystemConstants.LifeStatus.Invalid.value)
                    && Objects.equals(arg.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                StockOperateInfo info = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.RECOVER.value)
                        .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value).operateResult(StockOperateResultEnum.PASS.value)
                        .beforeLifeStatus(arg.getBeforeLifeStatus())
                        .afterLifeStatus(arg.getAfterLifeStatus()).build();

                returnedGoodsInvoiceInterceptorStockManager.addRealStock(context.getUser(), arg.getDataId(), info);
            }
        }

        return new ReturnedGoodsInvoiceRecoverAfterModel.Result();
    }

    @Override
    @Transactional
    public BulkInvalidBeforeModel.Result bulkInvalidBefore(ServiceContext context, BulkInvalidBeforeModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceBulkInvalidBefore. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);

            if (!CollectionUtils.isEmpty(arg.getBulkObjs())) {
                List<BulkInvalidBeforeModel.BulkObj> bulkObjs = arg.getBulkObjs().stream().filter(bulkObj -> Objects.equals(bulkObj.getNowLifeStatus(), SystemConstants.LifeStatus.Normal.value)).collect(Collectors.toList());
                List<String> returnOrderIds = bulkObjs.stream().map(BulkInvalidBeforeModel.BulkObj::getDataId).collect(Collectors.toList());

                //批量校验可用库存
                if (!CollectionUtils.isEmpty(returnOrderIds)) {
                    returnedGoodsInvoiceInterceptorStockManager.batchCheckAvailableStock(context.getUser(), returnOrderIds);
                }
            }
        }
        return new BulkInvalidBeforeModel.Result();
    }

    @Override
    @Transactional
    public BulkInvalidAfterModel.Result bulkInvalidAfter(ServiceContext context, BulkInvalidAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceBulkInvalidAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);

            if (!CollectionUtils.isEmpty(arg.getBulkObjs())) {
                List<String> returnOrderIds = arg.getBulkObjs().stream().map(BulkInvalidAfterModel.BulkObj::getDataId).collect(Collectors.toList());

                List<ReturnOrderModel.ReturnOrderVo> returnOrderVos = returnOrderManager.getByIds(context.getUser(), returnOrderIds);
                returnOrderVos = returnOrderVos.stream().filter(returnOrderVo -> StringUtils.isNotBlank(returnOrderVo.getWarehouseId())).collect(Collectors.toList());
                List<String> validReturnOrderIds = returnOrderVos.stream().map(ReturnOrderModel.ReturnOrderVo::getReturnOrderId).collect(Collectors.toList());

                //需要扣减实际库存
                List<BulkInvalidAfterModel.BulkObj> needMinusRealStock = arg.getBulkObjs().stream()
                        .filter(bulkObj -> validReturnOrderIds.contains(bulkObj.getDataId()))
                        .filter(bulkObj -> Objects.equals(bulkObj.getBeforeLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                                && Objects.equals(bulkObj.getAfterLifeStatus(), SystemConstants.LifeStatus.Invalid.value))
                        .collect(Collectors.toList());
                List<String> needMinusRealStockIds = needMinusRealStock.stream().map(BulkInvalidAfterModel.BulkObj::getDataId).collect(Collectors.toList());

                List<ReturnOrderModel.ReturnOrderVo> needMinusRealStockVO = returnOrderVos.stream().filter(returnOrderVo -> needMinusRealStockIds.contains(returnOrderVo.getReturnOrderId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(needMinusRealStockVO)) {
                    StockOperateInfo info = StockOperateInfo.builder().operateType(StockOperateTypeEnum.INVALID.value)
                            .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value).operateResult(StockOperateResultEnum.PASS.value)
                            .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                            .afterLifeStatus(SystemConstants.LifeStatus.Invalid.value).build();

                    returnedGoodsInvoiceInterceptorStockManager.batchMinusRealStock(context.getUser(), needMinusRealStockVO, info);
                }

                //需要增加冻结库存
                List<BulkInvalidAfterModel.BulkObj> needAddBlockedStock = arg.getBulkObjs().stream()
                        .filter(bulkObj -> validReturnOrderIds.contains(bulkObj.getDataId()))
                        .filter(bulkObj -> Objects.equals(bulkObj.getBeforeLifeStatus(), SystemConstants.LifeStatus.Normal.value)
                                && Objects.equals(bulkObj.getAfterLifeStatus(), SystemConstants.LifeStatus.InChange.value))
                        .collect(Collectors.toList());
                List<String> needAddBlockedStockIds = needAddBlockedStock.stream().map(BulkInvalidAfterModel.BulkObj::getDataId).collect(Collectors.toList());
                List<ReturnOrderModel.ReturnOrderVo> needAddBlockedStockVO = returnOrderVos.stream().filter(returnOrderVo -> needAddBlockedStockIds.contains(returnOrderVo.getReturnOrderId())).collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(needAddBlockedStockVO)) {
                    StockOperateInfo info = StockOperateInfo.builder().operateType(StockOperateTypeEnum.INVALID.value)
                            .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value).operateResult(StockOperateResultEnum.IN_APPROVAL.value)
                            .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                            .afterLifeStatus(SystemConstants.LifeStatus.InChange.value).build();

                    returnedGoodsInvoiceInterceptorStockManager.batchAddBlockedStock(context.getUser(), needAddBlockedStockVO, info);
                }
            }
        }

        return new BulkInvalidAfterModel.Result();
    }


    @Override
    public BulkRecoverBeforeModel.Result bulkRecoverBefore(ServiceContext context, BulkRecoverBeforeModel.Arg arg) {
        return new BulkRecoverBeforeModel.Result();
    }

    @Override
    @Transactional
    public BulkRecoverAfterModel.Result bulkRecoverAfter(ServiceContext context, BulkRecoverAfterModel.Arg arg) {
        if (stockManager.isStockEnable(context.getTenantId())) {
            log.info("returnGoodsInvoiceBulkRecoverAfter. context[{}], arg[{}]", context, arg);

            context = SfainterceptorUtils.outUser2Admin(context);

            //暂时没有批量恢复的操作
            for (BulkRecoverAfterModel.BulkObj bulkObj : arg.getBulkObjs()) {
                if (Objects.equals(bulkObj.getBeforeLifeStatus(), SystemConstants.LifeStatus.Invalid.value)
                        && Objects.equals(bulkObj.getAfterLifeStatus(), SystemConstants.LifeStatus.Normal.value)) {
                    StockOperateInfo info = StockOperateInfo.builder().operateObjectId(bulkObj.getDataId()).operateType(StockOperateTypeEnum.RECOVER.value)
                            .operateObjectType(StockOperateObjectTypeEnum.RETURN_ORDER.value).operateResult(StockOperateResultEnum.PASS.value)
                            .beforeLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                            .afterLifeStatus(SystemConstants.LifeStatus.Normal.value).build();

                    returnedGoodsInvoiceInterceptorStockManager.addRealStock(context.getUser(), bulkObj.getDataId(), info);
                }
            }
        }
        return new BulkRecoverAfterModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceBulkAddBeforeModel.Result bulkAddBefore(ServiceContext context,
                                                                       ReturnedGoodsInvoiceBulkAddBeforeModel.Arg arg) {
        ReturnedGoodsInvoiceBulkAddBeforeModel.Result checkResult = new ReturnedGoodsInvoiceBulkAddBeforeModel.Result();
        if (!CollectionUtils.isEmpty(arg.getMixtureVos())) {
            List<ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureVo> mixtureVos = arg.getMixtureVos();
            List<ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult> success = mixtureVos.stream().map(mixtureVo -> {
                ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult result = new ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult();
                result.setId(mixtureVo.getId());
                result.setErrCode(StockErrorCode.OK.getStringCode());
                result.setIsReturnedGoodsInvoiceFail(false);
                result.setDataId(mixtureVo.getDataId());
                result.setProductId(mixtureVo.getProductId());
                return result;
            }).collect(Collectors.toList());
            checkResult.setSuccessResults(success);

            if (stockManager.isStockEnable(context.getTenantId())) {
                log.info("returnedGoodsInvoiceOrderBulkAddBefore. context[{}], arg[{}]", context, arg);
                if (arg.getIsCheckReturnedGoodsInvoice()) {
                    //校验仓库是否存在
                    CheckBulkAddOrderModel.Result result = checkWarehouse(context.getUser(), mixtureVos);
                    log.info("checkWarehouse success. result[{}]", result);
                    List<ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult> successResult = result.getSuccessResult().stream().map(detailResult -> {
                        ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult mixtureResult = new ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult();
                        BeanUtils.copyProperties(mixtureResult, detailResult);
                        return mixtureResult;
                    }).collect(Collectors.toList());

                    List<ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult> failedResult = result.getFailedResult().stream().map(detailResult -> {
                        ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult mixtureResult = new ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureResult();
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

    @Override
    public ReturnedGoodsInvoiceBulkAddAfterModel.Result bulkAddAfter(ServiceContext context,
                                                                     ReturnedGoodsInvoiceBulkAddAfterModel.Arg arg) {
        return new ReturnedGoodsInvoiceBulkAddAfterModel.Result();
    }

    @Override
    public ReturnedGoodsInvoiceBulkAddTransactionAfterModel.Result bulkAddTransaction(ServiceContext context,
                                                                                      ReturnedGoodsInvoiceBulkAddTransactionAfterModel.Arg arg) {
        return new ReturnedGoodsInvoiceBulkAddTransactionAfterModel.Result();
    }

    private CheckBulkAddOrderModel.Result checkWarehouse(User user, List<ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureVo> mixtureVos) {
        List<CheckBulkAddOrderModel.Arg> args = mixtureVos.stream().map(mixtureVo -> {
            CheckBulkAddOrderModel.Arg arg = new CheckBulkAddOrderModel.Arg();
            arg.setWarehouseName(mixtureVo.getWarehouseName());
            arg.setCustomerId(mixtureVo.getCustomerId());
            arg.setTradeId(mixtureVo.getTradeId());
            arg.setId(mixtureVo.getId());
            arg.setProductId(mixtureVo.getProductName());
            return arg;
        }).collect(Collectors.toList());

        return wareHouseManager.checkWarehouse(user, args);
    }
}
