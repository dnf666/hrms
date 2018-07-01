package com.facishare.crm.sfainterceptor.predefine.service;



import com.facishare.crm.sfainterceptor.predefine.service.model.bulkInvalid.BulkInvalidAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkInvalid.BulkInvalidBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkRecover.BulkRecoverAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.bulkRecover.BulkRecoverBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd.SalesOrderBulkAddTransactionAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.invalid.SalesOrderInvalidAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.invalid.SalesOrderInvalidBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.invalid.SalesOrderInvalidFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd.SalesOrderBulkAddAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd.SalesOrderBulkAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit.SalesOrderEditAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit.SalesOrderEditBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit.SalesOrderEditFlowCompletedAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.recover.SalesOrderRecoverAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.recover.SalesOrderRecoverBeforeModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;



/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@ServiceModule("SalesOrderInterceptor")
public interface SalesOrderInterceptorService {

    @ServiceMethod("AddBefore")
    public SalesOrderAddBeforeModel.Result addBefore(ServiceContext context, SalesOrderAddBeforeModel.Arg arg);

    @ServiceMethod("AddAfter")
    public SalesOrderAddAfterModel.Result addAfter(ServiceContext context, SalesOrderAddAfterModel.Arg arg);

    @ServiceMethod("AddFlowCompletedAfter")
    public SalesOrderAddFlowCompletedAfterModel.Result addFlowCompletedAfter(ServiceContext context, SalesOrderAddFlowCompletedAfterModel.Arg arg);

    @ServiceMethod("EditBefore")
    public SalesOrderEditBeforeModel.Result editBefore(ServiceContext context, SalesOrderEditBeforeModel.Arg arg);

    @ServiceMethod("EditAfter")
    public SalesOrderEditAfterModel.Result editAfter(ServiceContext context, SalesOrderEditAfterModel.Arg arg);

    @ServiceMethod("EditFlowCompletedAfter")
    public SalesOrderEditFlowCompletedAfterModel.Result editFlowCompletedAfter(ServiceContext context, SalesOrderEditFlowCompletedAfterModel.Arg arg);

    @ServiceMethod("InvalidBefore")
    public SalesOrderInvalidBeforeModel.Result invalidBefore(ServiceContext context, SalesOrderInvalidBeforeModel.Arg arg);

    @ServiceMethod("InvalidAfter")
    public SalesOrderInvalidAfterModel.Result invalidAfter(ServiceContext context, SalesOrderInvalidAfterModel.Arg arg);

    @ServiceMethod("InvalidFlowCompletedAfter")
    public SalesOrderInvalidFlowCompletedAfterModel.Result invalidFlowCompletedAfter(ServiceContext context, SalesOrderInvalidFlowCompletedAfterModel.Arg arg);

    @ServiceMethod("RecoverBefore")
    public SalesOrderRecoverBeforeModel.Result recoverBefore(ServiceContext context, SalesOrderRecoverBeforeModel.Arg arg);

    @ServiceMethod("RecoverAfter")
    public SalesOrderRecoverAfterModel.Result recoverAfter(ServiceContext context, SalesOrderRecoverAfterModel.Arg arg);

//    @ServiceMethod("RecoverFlowCompletedAfter")
//    public SalesOrderRecoverFlowCompletedAfterModule.Result recoverFlowCompletedAfter(ServiceContext context, SalesOrderRecoverFlowCompletedAfterModule.Arg arg);

    @ServiceMethod("BulkInvalidBefore")
    public BulkInvalidBeforeModel.Result bulkInvalidBefore(ServiceContext context, BulkInvalidBeforeModel.Arg arg);

    @ServiceMethod("BulkInvalidAfter")
    public BulkInvalidAfterModel.Result bulkInvalidAfter(ServiceContext context, BulkInvalidAfterModel.Arg arg);

    @ServiceMethod("BulkRecoverBefore")
    public BulkRecoverBeforeModel.Result bulkRecoverBefore(ServiceContext context, BulkRecoverBeforeModel.Arg arg);

    @ServiceMethod("BulkRecoverAfter")
    public BulkRecoverAfterModel.Result bulkRecoverAfter(ServiceContext context, BulkRecoverAfterModel.Arg arg);

    @ServiceMethod("BulkAddBefore")
    public SalesOrderBulkAddBeforeModel.Result bulkAddBefore(ServiceContext context, SalesOrderBulkAddBeforeModel.Arg arg);

    @ServiceMethod("BulkAddAfter")
    public SalesOrderBulkAddAfterModel.Result bulkAddAfter(ServiceContext context, SalesOrderBulkAddAfterModel.Arg arg);

    @ServiceMethod("BulkAddTransaction")
    public SalesOrderBulkAddTransactionAfterModel.Result bulkAddTransaction(ServiceContext context, SalesOrderBulkAddTransactionAfterModel.Arg arg);

}
