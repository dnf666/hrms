package com.facishare.crm;

import com.facishare.crm.checkins.CheckinsDefaultObject;
import com.facishare.crm.customeraccount.predefine.CustomerAccountPredefineObject;
import com.facishare.crm.customeraccount.task.TaskFactory;
import com.facishare.crm.deliverynote.predefine.DeliveryNotePredefineObject;
import com.facishare.crm.electronicsign.predefine.ElecSignPredefineObject;
import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.outbounddeliverynote.predefine.OutboundDeliveryNotePredefineObject;
import com.facishare.crm.erpstock.predefine.ErpStockPredefineObject;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.promotion.predefine.PromotionPredefineObject;
import com.facishare.crm.requisitionnote.predefine.RequisitionNotePredefineObject;
import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.crm.stock.predefine.StockPredefineObject;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 初始化服务
 * <p>
 * Created by liyiguang on 2017/7/12.
 */
@Service
public class CRMInitService extends ApplicationObjectSupport {

    @Autowired
    private ServiceFacade serviceFacade;

    @PostConstruct
    public void init() {
        SFAPreDefineObject.init();
        CustomerAccountPredefineObject.init();
        PaymentObject.init();
        GoalObject.init();
        //高级外勤
        CheckinsDefaultObject.init();
        //发货单
        DeliveryNotePredefineObject.init();
        StockPredefineObject.init();
        TaskFactory.init();
        PromotionPredefineObject.init();
        OutboundDeliveryNotePredefineObject.init();
        RequisitionNotePredefineObject.init();
        ElecSignPredefineObject.init();

        //ERP库存
        ErpStockPredefineObject.init();
    }
}