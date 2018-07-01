package com.facishare.crm.requisitionnote.predefine.action;


import com.facishare.crm.requisitionnote.exception.RequisitionNoteBusinessException;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteErrorCode;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import lombok.extern.slf4j.Slf4j;


/**
 * @author liangk
 * @date 18/01/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteBulkRecoverAction extends StandardBulkRecoverAction{
    @Override
    protected void before(Arg arg) {
        //调拨单不允许恢复
        throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "调拨单不允许恢复");
    }
}
