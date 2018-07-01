package com.facishare.crm.sfainterceptor.predefine.service;

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
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@ServiceModule("ReturnedGoodsInvoiceInterceptor")
public interface ReturnedGoodsInvoiceInterceptorService {

    //add 退货单before检测暂时用不上。
    @ServiceMethod("AddBefore")
    public ReturnedGoodsInvoiceAddBeforeModel.Result addBefore(ServiceContext context, ReturnedGoodsInvoiceAddBeforeModel.Arg arg);

    //before=未生效[after=正常（增加实际库存） after=审核中]
    @ServiceMethod("AddAfter")
    public ReturnedGoodsInvoiceAddAfterModel.Result addAfter(ServiceContext context, ReturnedGoodsInvoiceAddAfterModel.Arg arg);


    //before=审核中[after=未生效   after=正常（增加实际库存）]
    @ServiceMethod("AddFlowCompletedAfter")
    public ReturnedGoodsInvoiceAddFlowCompletedAfterModel.Result addFlowCompletedAfter(ServiceContext context, ReturnedGoodsInvoiceAddFlowCompletedAfterModel.Arg arg);


//    //edit
//    //不操作
    @ServiceMethod("EditBefore")
    public ReturnedGoodsInvoiceEditBeforeModel.Result editBefore(ServiceContext context, ReturnedGoodsInvoiceEditBeforeModel.Arg arg);

    //before=未生效[after=正常（增加实际库存） after=审核中]
    @ServiceMethod("EditAfter")
    public ReturnedGoodsInvoiceEditAfterModel.Result editAfter(ServiceContext context, ReturnedGoodsInvoiceEditAfterModel.Arg arg);

    //before=审核中[after=未生效   after=正常（增加实际库存）]
    @ServiceMethod("EditFlowCompletedAfter")
    public ReturnedGoodsInvoiceEditFlowCompletedAfterModel.Result editFlowCompletedAfter(ServiceContext context, ReturnedGoodsInvoiceEditFlowCompletedAfterModel.Arg arg);


    //Invalid
    //beforeInvalid=正常(校验可用库存 >= 入库数  )   beforeInvalid=未生效(不操作)
    @ServiceMethod("InvalidBefore")
    public ReturnedGoodsInvoiceInvalidBeforeModel.Result invalidBefore(ServiceContext context, ReturnedGoodsInvoiceInvalidBeforeModel.Arg arg);


    //before=正常[after=审核中(增加冻结库存)  after=已作废(扣减实际库存)]
    //before=未生效[after=未生效  after=审核中  after=已作废]
    @ServiceMethod("InvalidAfter")
    public ReturnedGoodsInvoiceInvalidAfterModel.Result invalidAfter(ServiceContext context, ReturnedGoodsInvoiceInvalidAfterModel.Arg arg);


    //before=审核中[after=正常（扣减冻结库存）  after=未生效  after=已作废{beforeInvalid=正常（扣减实际库存  扣减冻结库存）}]
    @ServiceMethod("InvalidFlowCompletedAfter")
    public ReturnedGoodsInvoiceInvalidFlowCompletedAfterModel.Result invalidFlowCompletedAfter(ServiceContext context, ReturnedGoodsInvoiceInvalidFlowCompletedAfterModel.Arg arg);

    //recover
    //不操作
    @ServiceMethod("RecoverBefore")
    public ReturnedGoodsInvoiceRecoverBeforeModel.Result recoverBefore(ServiceContext context, ReturnedGoodsInvoiceRecoverBeforeModel.Arg arg);


    //before=已作废[after=正常（增加实际库存）  after=未生效（不操作）]
    @ServiceMethod("RecoverAfter")
    public ReturnedGoodsInvoiceRecoverAfterModel.Result recoverAfter(ServiceContext context, ReturnedGoodsInvoiceRecoverAfterModel.Arg arg);

//    @ServiceMethod("RecoverFlowCompletedAfter")
//    public ReturnedGoodsInvoiceRecoverFlowCompletedAfterModule.Result recoverFlowCompletedAfter(ServiceContext context, ReturnedGoodsInvoiceRecoverFlowCompletedAfterModule.Arg arg);

    //批量作废
    @ServiceMethod("BulkInvalidBefore")
    public BulkInvalidBeforeModel.Result bulkInvalidBefore(ServiceContext context, BulkInvalidBeforeModel.Arg arg);

    @ServiceMethod("BulkInvalidAfter")
    public BulkInvalidAfterModel.Result bulkInvalidAfter(ServiceContext context, BulkInvalidAfterModel.Arg arg);


    //批量恢复
    @ServiceMethod("BulkRecoverBefore")
    public BulkRecoverBeforeModel.Result bulkRecoverBefore(ServiceContext context, BulkRecoverBeforeModel.Arg arg);

    @ServiceMethod("BulkRecoverAfter")
    public BulkRecoverAfterModel.Result bulkRecoverAfter(ServiceContext context, BulkRecoverAfterModel.Arg arg);

    //批量导入
    @ServiceMethod("BulkAddBefore")
    public ReturnedGoodsInvoiceBulkAddBeforeModel.Result bulkAddBefore(ServiceContext context, ReturnedGoodsInvoiceBulkAddBeforeModel.Arg arg);

    @ServiceMethod("BulkAddAfter")
    public ReturnedGoodsInvoiceBulkAddAfterModel.Result bulkAddAfter(ServiceContext context, ReturnedGoodsInvoiceBulkAddAfterModel.Arg arg);

    @ServiceMethod("BulkAddTransaction")
    public ReturnedGoodsInvoiceBulkAddTransactionAfterModel.Result bulkAddTransaction(ServiceContext context, ReturnedGoodsInvoiceBulkAddTransactionAfterModel.Arg arg);


}
