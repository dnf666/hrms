package com.facishare.crm.customeraccount;

import com.facishare.crm.common.BaseServiceTest;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.predefine.service.SfaOrderPaymentService;
import com.facishare.crm.customeraccount.predefine.service.SfaPaymentService;
import com.facishare.crm.customeraccount.predefine.service.SfaRefundService;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaOrderPaymentModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class SfaOrderPaymentServiceTest extends BaseServiceTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    SfaPaymentService sfaPaymentService;
    @Autowired
    SfaRefundService sfaRefundService;

    @Autowired
    SfaOrderPaymentService sfaOrderPaymentService;

    @Autowired
    PrepayDetailManager prepayDetailManager;

    @Autowired
    RebateOutcomeDetailManager rebateOutcomeDetailManager;

    public SfaOrderPaymentServiceTest() {
        super(RebateOutcomeDetailConstants.API_NAME);
    }

    @Test
    public void create() {
        String customerId = "acb31cb4fc4e48e49412670f6a9ab3fb";
        // 1
        ObjectDataDocument prepayData = new ObjectDataDocument();
        prepayData.put(PrepayDetailConstants.Field.Customer.apiName, customerId);
        prepayData.put(PrepayDetailConstants.Field.OutcomeType.apiName, "1");
        prepayData.put(SystemConstants.Field.RecordType.apiName, PrepayDetailConstants.RecordType.OutcomeRecordType.apiName);
        prepayData.put(PrepayDetailConstants.Field.Amount.apiName, 33);
        prepayData.put(PrepayDetailConstants.Field.TransactionTime.apiName, 1517328000000L);
        prepayData.put(PrepayDetailConstants.Field.Payment.apiName, "5a65b5e096a74c190d8f1c2a");
        prepayData.put("life_status", "normal");
        prepayData.put(PrepayDetailConstants.Field.OrderPayment.apiName, "5a65b5e296a74c190d8f1c2e");

        ObjectDataDocument rebateData = new ObjectDataDocument();
        rebateData.put(PrepayDetailConstants.Field.Customer.apiName, customerId);
        rebateData.put(RebateOutcomeDetailConstants.Field.Amount.apiName, 22);
        rebateData.put(RebateOutcomeDetailConstants.Field.TransactionTime.apiName, 1517328000000L);
        rebateData.put(RebateOutcomeDetailConstants.Field.Payment.apiName, "5a65b5e096a74c190d8f1c2a");
        rebateData.put("life_status", "normal");
        rebateData.put(RebateOutcomeDetailConstants.Field.OrderPayment.apiName, "5a65b5e296a74c190d8f1c2e");

        SfaOrderPaymentModel.CreateArgDetail argDetail = new SfaOrderPaymentModel.CreateArgDetail();
        argDetail.setPrepayDetailData(prepayData);
        argDetail.setRebateOutcomeDetailData(rebateData);

        // 2
        ObjectDataDocument prepayData1 = new ObjectDataDocument();
        prepayData1.put(PrepayDetailConstants.Field.Customer.apiName, "acb31cb4fc4e48e49412670f6a9ab3fb");
        prepayData1.put(PrepayDetailConstants.Field.OutcomeType.apiName, "1");
        prepayData1.put(PrepayDetailConstants.Field.Amount.apiName, 66);
        prepayData1.put(PrepayDetailConstants.Field.TransactionTime.apiName, 1517328000000L);
        prepayData1.put(PrepayDetailConstants.Field.Payment.apiName, "5a65b5e096a74c190d8f1c2a");
        prepayData1.put("life_status", "normal");
        prepayData1.put(PrepayDetailConstants.Field.OrderPayment.apiName, "5a65b5e296a74c190d8f1c2f");

        ObjectDataDocument rebateData1 = new ObjectDataDocument();
        rebateData1.put(PrepayDetailConstants.Field.Customer.apiName, customerId);
        rebateData1.put(RebateOutcomeDetailConstants.Field.Amount.apiName, 55);
        rebateData1.put(RebateOutcomeDetailConstants.Field.TransactionTime.apiName, 1517328000000L);
        rebateData1.put(RebateOutcomeDetailConstants.Field.Payment.apiName, "5a65b5e096a74c190d8f1c2a");
        rebateData1.put("life_status", "normal");
        rebateData1.put(RebateOutcomeDetailConstants.Field.OrderPayment.apiName, "5a65b5e296a74c190d8f1c2f");

        SfaOrderPaymentModel.CreateArgDetail argDetail1 = new SfaOrderPaymentModel.CreateArgDetail();
        argDetail1.setPrepayDetailData(prepayData1);
        argDetail1.setRebateOutcomeDetailData(rebateData1);

        Map<String, SfaOrderPaymentModel.CreateArgDetail> createMap = Maps.newHashMap();
        createMap.put("5a65b5e296a74c190d8f1c2e", argDetail);
        createMap.put("5a65b5e296a74c190d8f1c2f", argDetail1);

        SfaOrderPaymentModel.CreateArg createArg = new SfaOrderPaymentModel.CreateArg();
        createArg.setOrderPaymentMap(createMap);
        createArg.setPaymentId("5a65b5e096a74c190d8f1c2a");
        SfaOrderPaymentModel.CreateResult result = sfaOrderPaymentService.create(createArg, newServiceContext());
        System.out.print("result===>" + result);
        Assert.assertNotNull(result);
    }

    @Test
    public void queryByRelativeNames() {
        SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdArg arg = new SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdArg();
        arg.setOrderPaymentId("5a6ed737830bdb197cd5feb2");
        SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdResult result = sfaOrderPaymentService.getRelativeNamesByOrderPaymentId(arg, newServiceContext());
        log.debug("queryByRelativeNames->result:{}", result);
    }

    /**
     * 可以把invalid,normal所有状态的对象都查出来。
     * @return
     */
    @Test
    public void testListInvalidOrderPayments() {
        List<String> orderPaymentIds = new ArrayList<>();
        orderPaymentIds.add("5a6ed737830bdb197cd5feb2");
        orderPaymentIds.add("5a6edb56830bdb21440ade76");
        List<IObjectData> rebatesDatas = rebateOutcomeDetailManager.listInvalidDataByOrderPaymentIds(newServiceContext().getUser(), orderPaymentIds);
        log.debug("rebatesDatas:{}", rebatesDatas);

    }

    @Test
    public void getOrderPaymentCost() {
        List<String> orderPaymentList = new ArrayList<>();

        orderPaymentList.add("5a6ec2a0830bdbfe8f55f050");
        orderPaymentList.add("5a950045830bdbcecb548c92");
        orderPaymentList.add("5a69965d9831862505143b62");

        for (int i = 0; i < 3; i++) {
            log.info("begin prepayObjectData,for orderPaymentid:{}", orderPaymentList.get(i));
            IObjectData prepayObjectData = prepayDetailManager.getByOrderPaymentId(newServiceContext().getUser(), orderPaymentList.get(i)); // 5a6ecb08830bdb120c0d0f0e
            log.debug("getOrderPaymentCost()-->prepayObjectData:{}", prepayObjectData);
        }
    }

}
