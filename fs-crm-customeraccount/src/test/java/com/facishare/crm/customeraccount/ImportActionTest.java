package com.facishare.crm.customeraccount;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseActionTest;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.BaseImportDataAction;
import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.BaseImportVerifyAction;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class ImportActionTest extends BaseActionTest {
    public ImportActionTest() {
        super(PrepayDetailConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void prepayDetaidlInsertImportActionTest() {
        apiName = PrepayDetailConstants.API_NAME;
        BaseImportTemplateAction.Arg arg = new BaseImportTemplateAction.Arg();
        arg.setDescribeApiName(PrepayDetailConstants.API_NAME);
        arg.setImportType(SystemConstants.ImportType.Insert.value);
        String path = (String) execute(StandardAction.InsertImportTemplate.name(), arg);
        log.info("prepayDetaidlInsertImportActionTest->path:{}", path);
    }

    @Test
    public void customerAccountUpdateImportActionTest() {
        apiName = CustomerAccountConstants.API_NAME;
        BaseImportTemplateAction.Arg arg = new BaseImportTemplateAction.Arg();
        arg.setDescribeApiName(CustomerAccountConstants.API_NAME);
        arg.setImportType(SystemConstants.ImportType.Edit.value);

        String path = (String) execute(StandardAction.UpdateImportTemplate.name(), arg);
        log.info("customerAccountUpdateImportActionTest->path:{}", path);
    }

    @Test
    public void customerAccountImportUpdateDataTest() {
        apiName = CustomerAccountConstants.API_NAME;
        BaseImportDataAction.Arg arg = new BaseImportDataAction.Arg();
        arg.setImportType(SystemConstants.ImportType.Edit.value);
        arg.setApiName(CustomerAccountConstants.API_NAME);
        arg.setIsEmptyValueToUpdate(false);
        arg.setTenantId(tenantId);
        arg.setUserId(fsUserId);

        IObjectData objectData2 = new ObjectData();

        objectData2.set("账户ID（必填）", "CA2018-01-18_0002");
        objectData2.set("结算方式（必填）", "预付|现付");
        objectData2.set("信用额度(元)", "273");
        objectData2.set("客户名称（必填）", "C1");

        arg.setRows(Arrays.asList(ObjectDataDocument.of(objectData2)));

        BaseImportVerifyAction.Result verifyResult = (BaseImportVerifyAction.Result) execute(StandardAction.UpdateImportVerify.name(), arg);
        log.info("BaseImportDataAction.verifyResult->reulst:{}", verifyResult);

        //设置数据列：arg.setRows();
        BaseImportDataAction.Result result = (BaseImportDataAction.Result) execute(StandardAction.UpdateImportData.name(), arg);
        log.info("BaseImportDataAction.Result->reulst:{}", result);
    }

    @Test
    public void customerAccountImportUpdateDataTest1() {
        apiName = CustomerAccountConstants.API_NAME;
        BaseImportDataAction.Arg arg = new BaseImportDataAction.Arg();
        arg.setImportType(SystemConstants.ImportType.Edit.value);
        arg.setApiName(CustomerAccountConstants.API_NAME);
        arg.setIsEmptyValueToUpdate(false);
        arg.setTenantId("69660");
        arg.setUserId(fsUserId);

        IObjectData objectData2 = new ObjectData();

        objectData2.set("账户ID（必填）", "CA2018-02-26_7");
        objectData2.set("结算方式（必填）", "预付|现付");
        objectData2.set("信用额度(元)", "183");
        objectData2.set("客户名称（必填）", "阿狸");

        arg.setRows(Arrays.asList(ObjectDataDocument.of(objectData2)));

        BaseImportVerifyAction.Result verifyResult = (BaseImportVerifyAction.Result) execute(StandardAction.UpdateImportVerify.name(), arg);
        log.info("BaseImportDataAction.verifyResult->reulst:{}", verifyResult);

        //设置数据列：arg.setRows();
        BaseImportDataAction.Result result = (BaseImportDataAction.Result) execute(StandardAction.UpdateImportData.name(), arg);
        log.info("BaseImportDataAction.Result->reulst:{}", result);
    }

    /**
     * 2018-2-26 测试OK<br>
     */
    @Test
    public void prepayDetailInsertImportDataTest() {
        apiName = PrepayDetailConstants.API_NAME;
        BaseImportDataAction.Arg arg = new BaseImportDataAction.Arg();
        arg.setImportType(SystemConstants.ImportType.Insert.value);
        arg.setApiName(PrepayDetailConstants.API_NAME);
        arg.setIsEmptyValueToUpdate(false);
        arg.setTenantId(tenantId);
        arg.setUserId(fsUserId);

        IObjectData objectData2 = new ObjectData();

        //账户ID（必填）
        //        objectData2.set(CustomerAccountConstants.Field.Name.apiName, "CA2018-01-18_0002");
        //        objectData2.set(CustomerAccountConstants.Field.SettleType.apiName, "预付|现付");
        //        objectData2.set(CustomerAccountConstants.Field.CreditQuota.apiName, "189");
        //        objectData2.set(CustomerAccountConstants.Field.Customer.apiName, "C1");

        objectData2.set("备注", "CA2018-01-18_0002");
        objectData2.set("收入类型", "现金"); //FIXME 这个应该是必填<br>
        objectData2.set("负责人（必填）", "DEV");
        objectData2.set("金额(元)（必填）", "78.6");
        objectData2.set("交易时间（必填）", "2018-1-16 10:21");
        objectData2.set("客户名称（必填）", "C1");
        objectData2.set("客户账户", "CA2018-01-18_0002"); //FIXME 这个应该是必填
        objectData2.set("业务类型", "支出");

        objectData2.set("rowNo", 1);

        //objectData2.set("客户名称（必填）", "C12");  BaseImportDataAction.Result->reulst:BaseImportAction.Result(success=true, message=null, errorCode=0, value=BaseImportAction.ImportResultValue(importSucceedCount=0, rowErrorList=[BaseImportAction.ImportError(rowNo=0, errorMessage=客户名称关联的对象数据不存在或没有权限！)]))

        //IObjectData objectData = serviceFacade.findObjectData(user, "5a6088d3bab09cd292fe1ce0", CustomerAccountConstants.API_NAME);// 9197164
        //        cleanObjectData(objectData);
        //
        //        List<String> exportHeaderFilter = Lists.newArrayList(CustomerAccountConstants.Field.Name.apiName, CustomerAccountConstants.Field.CreditQuota.apiName, CustomerAccountConstants.Field.SettleType.apiName, CustomerAccountConstants.Field.Customer.apiName);
        //
        //        objectData.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, 0);
        //        objectData.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, 89);

        arg.setRows(Arrays.asList(ObjectDataDocument.of(objectData2)));

        //BaseImportVerifyAction.Result verifyResult = (BaseImportVerifyAction.Result) execute(StandardAction.InsertImportVerify.name(), arg);
        //log.info("BaseImportDataAction.verifyResult->reulst:{}", verifyResult);

        //设置数据列：arg.setRows();
        BaseImportDataAction.Result result = (BaseImportDataAction.Result) execute(StandardAction.InsertImportData.name(), arg);
        log.info("BaseImportDataAction.Result->reulst:{}", result);
    }

    /**
     * 2018-2-26 测试OK<br>
     */
    @Test
    public void rebateIncomeInsertImportDataTest() {
        apiName = RebateIncomeDetailConstants.API_NAME;
        BaseImportDataAction.Arg arg = new BaseImportDataAction.Arg();
        arg.setImportType(SystemConstants.ImportType.Insert.value);
        arg.setApiName(RebateIncomeDetailConstants.API_NAME);
        arg.setIsEmptyValueToUpdate(false);
        arg.setTenantId(tenantId);
        arg.setUserId(fsUserId);

        IObjectData objectData2 = new ObjectData();

        //        账户ID（必填）
        //        objectData2.set(CustomerAccountConstants.Field.Name.apiName, "CA2018-01-18_0002");
        //        objectData2.set(CustomerAccountConstants.Field.SettleType.apiName, "预付|现付");
        //        objectData2.set(CustomerAccountConstants.Field.CreditQuota.apiName, "189");
        //        objectData2.set(CustomerAccountConstants.Field.Customer.apiName, "C1");

        objectData2.set("备注", "CA2018-01-18_0002");
        objectData2.set("收入类型（必填）", "订单退款"); //FIXME 这个应该是必填<br>
        objectData2.set("负责人（必填）", "DEV");
        objectData2.set("返利金额(元)（必填）", "73.8");
        objectData2.set("结束时间（必填）", "2018-10-26 10:21");
        //        objectData2.set("可用返利(元)", "66.6"); //FIXME 新建的时候这个应该不需要。
        //        objectData2.set("已用返利(元)", "22.2"); //FIXME 新建的时候这个应该是空的。
        objectData2.set("开始时间（必填）", "2018-1-26 10:21");
        objectData2.set("交易时间（必填）", "2018-2-26 10:21");
        objectData2.set("客户名称（必填）", "C1");
        objectData2.set("客户账户（必填）", "CA2018-01-18_0002");

        objectData2.set("业务类型", "默认业务类型");//如果要填的话，必须填 “默认业务类型”

        objectData2.set("rowNo", 1);

        //objectData2.set("客户名称（必填）", "C12");  BaseImportDataAction.Result->reulst:BaseImportAction.Result(success=true, message=null, errorCode=0, value=BaseImportAction.ImportResultValue(importSucceedCount=0, rowErrorList=[BaseImportAction.ImportError(rowNo=0, errorMessage=客户名称关联的对象数据不存在或没有权限！)]))

        //IObjectData objectData = serviceFacade.findObjectData(user, "5a6088d3bab09cd292fe1ce0", CustomerAccountConstants.API_NAME);// 9197164
        //        cleanObjectData(objectData);
        //
        //        List<String> exportHeaderFilter = Lists.newArrayList(CustomerAccountConstants.Field.Name.apiName, CustomerAccountConstants.Field.CreditQuota.apiName, CustomerAccountConstants.Field.SettleType.apiName, CustomerAccountConstants.Field.Customer.apiName);
        //
        //        objectData.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, 0);
        //        objectData.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, 89);

        arg.setRows(Arrays.asList(ObjectDataDocument.of(objectData2)));

        BaseImportVerifyAction.Result verifyResult = (BaseImportVerifyAction.Result) execute(StandardAction.InsertImportVerify.name(), arg);
        log.info("BaseImportDataAction.verifyResult->reulst:{}", verifyResult);

        //设置数据列：arg.setRows();
        BaseImportDataAction.Result result = (BaseImportDataAction.Result) execute(StandardAction.InsertImportData.name(), arg);
        log.info("BaseImportDataAction.Result->reulst:{}", result);
    }

    /**
     * 2018-2-26 测试OK<br>
     */
    @Test
    public void rebateIncomeInsertImportDataTest69660() {
        //69660
        apiName = RebateIncomeDetailConstants.API_NAME;
        BaseImportDataAction.Arg arg = new BaseImportDataAction.Arg();
        arg.setImportType(SystemConstants.ImportType.Insert.value);
        arg.setApiName(RebateIncomeDetailConstants.API_NAME);
        arg.setIsEmptyValueToUpdate(false);
        arg.setTenantId(tenantId);
        arg.setUserId(fsUserId);

        IObjectData objectData2 = new ObjectData();

        //        账户ID（必填）
        //        objectData2.set(CustomerAccountConstants.Field.Name.apiName, "CA2018-01-18_0002");
        //        objectData2.set(CustomerAccountConstants.Field.SettleType.apiName, "预付|现付");
        //        objectData2.set(CustomerAccountConstants.Field.CreditQuota.apiName, "189");
        //        objectData2.set(CustomerAccountConstants.Field.Customer.apiName, "C1");

        objectData2.set("备注", "CA2018-02-26_7");
        objectData2.set("收入类型（必填）", "订单退款"); //FIXME 这个应该是必填<br>
        objectData2.set("负责人（必填）", "DEV");
        objectData2.set("返利金额(元)（必填）", "73.8");
        objectData2.set("结束时间（必填）", "2018-7-8");
        //        objectData2.set("可用返利(元)", "66.6"); //FIXME 新建的时候这个应该不需要。
        //        objectData2.set("已用返利(元)", "22.2"); //FIXME 新建的时候这个应该是空的。
        objectData2.set("开始时间（必填）", "2018-3-2");
        objectData2.set("交易时间（必填）", "2018-3-8 10:21");
        objectData2.set("客户名称（必填）", "阿狸");
        objectData2.set("客户账户（必填）", "CA2018-02-26_7");

        objectData2.set("业务类型", "默认业务类型");//如果要填的话，必须填 “默认业务类型”

        objectData2.set("rowNo", 1);

        //objectData2.set("客户名称（必填）", "C12");  BaseImportDataAction.Result->reulst:BaseImportAction.Result(success=true, message=null, errorCode=0, value=BaseImportAction.ImportResultValue(importSucceedCount=0, rowErrorList=[BaseImportAction.ImportError(rowNo=0, errorMessage=客户名称关联的对象数据不存在或没有权限！)]))

        //IObjectData objectData = serviceFacade.findObjectData(user, "5a6088d3bab09cd292fe1ce0", CustomerAccountConstants.API_NAME);// 9197164
        //        cleanObjectData(objectData);
        //
        //        List<String> exportHeaderFilter = Lists.newArrayList(CustomerAccountConstants.Field.Name.apiName, CustomerAccountConstants.Field.CreditQuota.apiName, CustomerAccountConstants.Field.SettleType.apiName, CustomerAccountConstants.Field.Customer.apiName);
        //
        //        objectData.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, 0);
        //        objectData.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, 89);

        arg.setRows(Arrays.asList(ObjectDataDocument.of(objectData2)));

        BaseImportVerifyAction.Result verifyResult = (BaseImportVerifyAction.Result) execute(StandardAction.InsertImportVerify.name(), arg);
        log.info("BaseImportDataAction.verifyResult->reulst:{}", verifyResult);

        //设置数据列：arg.setRows();
        BaseImportDataAction.Result result = (BaseImportDataAction.Result) execute(StandardAction.InsertImportData.name(), arg);
        log.info("BaseImportDataAction.Result->reulst:{}", result);
    }

    private void cleanObjectData(IObjectData iObjectData) {
        List<String> exportHeaderFilter = Lists.newArrayList(SystemConstants.Field.TennantID.apiName, SystemConstants.Field.LockRule.apiName, CustomerAccountConstants.Field.PrepayBalance.apiName, SystemConstants.Field.LockUser.apiName, CustomerAccountConstants.Field.RebateAvailableBalance.apiName, SystemConstants.Field.ExtendObjDataId.apiName,
                //is_deleted...
                CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, SystemConstants.Field.LifeStatusBeforeInvalid.apiName,
                //total_num
                //object_describe_api_name
                SystemConstants.Field.OwnerDepartment.apiName, SystemConstants.Field.Owner.apiName, SystemConstants.Field.LockStatus.apiName, CustomerAccountConstants.Field.RebateLockedBalance.apiName,
                //package
                SystemConstants.Field.LastModifiedTime.apiName, SystemConstants.Field.CreateTime.apiName, SystemConstants.Field.LifeStatus.apiName, SystemConstants.Field.LastModifiedBy.apiName, SystemConstants.Field.CreateBy.apiName,
                //version
                SystemConstants.Field.RecordType.apiName, SystemConstants.Field.RelevantTeam.apiName,
                //object_describe_id
                // -- name
                CustomerAccountConstants.Field.PrepayLockedBalance.apiName,
                // -- id
                // -- customer_id
                CustomerAccountConstants.Field.RebateBalance.apiName);

        for (String fieldApiName : exportHeaderFilter) {
            iObjectData.set(fieldApiName, null);
        }

    }

}
