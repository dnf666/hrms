package com.facishare.crm.payment.transfer;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CrmPackageObjectConstants;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.fxiaoke.transfer.dto.OpType;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.dto.columns.StringColumn;
import com.fxiaoke.transfer.utils.ConverterUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PaymentTransformer extends Transformer {

  public PaymentTransformer(ServiceFacade serviceFacade) {
    super(serviceFacade);
  }

  @Override
  public List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
    List<Record> records = Lists.newArrayList();
    String ei = sourceData.getTenantId();
    Map<String, Object> data = sourceData.getData();
    Record cp = generateCustomerPaymentRecord(ei, data);
    Record op = generateOrderPaymentRecord(ei, data);
    records.add(cp);
    records.add(op);
    return records;
  }

  private Record generateCustomerPaymentRecord(String tenantId, Map<String, Object> data) {
    Record cp = new Record();
    cp.setOpType(OpType.UPSERT);
    cp.setTable("payment_customer");
    cp.addIdColumn(
        new StringColumn("id", ConverterUtil.convert2String(data.get("trade_payment_id"))));
    cp.addIdColumn(
        new StringColumn("tenant_id", tenantId)
    );
    String describeId = getDescribe(tenantId, PaymentObject.CUSTOMER_PAYMENT.getApiName()).getId();
    String lifeStatus = getLifeStatusValue(ConverterUtil.convert2Integer(data.get("status")));
    String type = ConverterUtil.convert2String(data.get("payment_type"));
    String actualType = "8".equals(type) ? "other" : type;
    String approveEmployeeId = ConverterUtil.convert2String(data.get("finance_employee_id"));
    String actualApproveEmployeeId =
        (StringUtils.isBlank(approveEmployeeId) || "0".equals(approveEmployeeId)) ? null : approveEmployeeId;
    String lockUser = ConverterUtil.convert2String(data.get("last_locked_by"));
    String money = ConverterUtil.convert2String(data.get("payment_money"));
    BigDecimal amount = new BigDecimal(money);
    String actualLockUser = "0".equals(lockUser) ? null : lockUser;
    cp.addStringColumn(CustomerPaymentObj.FIELD_NAME,
        ConverterUtil.convert2String(data.get("trade_payment_code")))
        .addLongColumn(CustomerPaymentObj.FIELD_PAYMENT_TIME,
            ConverterUtil.convert2Long(data.get("payment_time")))
        .addStringColumn(CustomerPaymentObj.FIELD_PAYMENT_METHOD, actualType)
        .addDecimalColumn(CustomerPaymentObj.FIELD_PAYMENT_AMOUNT, amount)
        .addStringColumn(CustomerPaymentObj.FIELD_ACCOUNT_ID,
            ConverterUtil.convert2String(data.get("customer_id")))
        .addStringColumn(CustomerPaymentObj.FIELD_ORDER_ID,
            ConverterUtil.convert2String(data.get("customer_trade_id")))
        .addLongColumn(CustomerPaymentObj.FIELD_REMIND_TIME,
            ConverterUtil.convert2Long(data.get("remind_time")))
        .addObjectColumn(CustomerPaymentObj.FIELD_APPROVE_EMPLOYEE_ID, Lists.newArrayList(actualApproveEmployeeId))
        .addLongColumn(CustomerPaymentObj.FIELD_APPROVE_TIME,
            ConverterUtil.convert2Long(data.get("finance_confirm_time")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_OWNER,
            ConverterUtil.convert2String(data.get("belonger_id")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_LOCK_STATUS,
            ConverterUtil.convert2String(data.get("lock_status")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_LOCK_USER, actualLockUser)
        .addStringColumn(CrmPackageObjectConstants.FIELD_RECORD_TYPE,
            CrmPackageObjectConstants.DEFAULT_RECORD_TYPE)
        .addStringColumn(CrmPackageObjectConstants.FIELD_EXTEND_DATA_ID,
            ConverterUtil.convert2String(data.get("extend_obj_data_id")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_PACKAGE,
            CrmPackageObjectConstants.DEFAULT_PACKAGE)
        .addLongColumn(CrmPackageObjectConstants.FIELD_VERSION,
            CrmPackageObjectConstants.DEFAULT_VERSION)
        .addLongColumn(CrmPackageObjectConstants.FIELD_IS_DELETED,
            ConverterUtil.convert2Long(data.get("is_deleted")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_CREATED_BY,
            ConverterUtil.convert2String(data.get("creator_id")))
        .addLongColumn(CrmPackageObjectConstants.FIELD_CREATE_TIME,
            ConverterUtil.convert2Long(data.get("create_time")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_LAST_MODIFIED_BY,
            ConverterUtil.convert2String(data.get("updator_id")))
        .addLongColumn(CrmPackageObjectConstants.FIELD_LAST_MODIFIED_TIME,
            ConverterUtil.convert2Long(data.get("update_time")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_REMARK,
            ConverterUtil.convert2String(data.get("remark")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_DESCRIBE_API_NAME,
            PaymentObject.CUSTOMER_PAYMENT.getApiName())
        .addStringColumn(CrmPackageObjectConstants.FIELD_LIFE_STATUS,
            lifeStatus)
        .addStringColumn(CrmPackageObjectConstants.FIELD_DESCRIBE_ID, describeId);
    return cp;
  }

  private Record generateOrderPaymentRecord(String tenantId, Map<String, Object> data) {
    Record cp = new Record();
    cp.setOpType(OpType.UPSERT);
    cp.setTable("payment_order");
    cp.addIdColumn(
        new StringColumn("id", ConverterUtil.convert2String(data.get("trade_payment_id"))));
    cp.addIdColumn(
        new StringColumn("tenant_id", tenantId)
    );
    String describeId = getDescribe(tenantId, PaymentObject.ORDER_PAYMENT.getApiName()).getId();
    String lifeStatus = getLifeStatusValue(ConverterUtil.convert2Integer(data.get("status")));
    String money = ConverterUtil.convert2String(data.get("payment_money"));
    BigDecimal amount = new BigDecimal(money);
    cp.addStringColumn(OrderPaymentObj.FIELD_NAME,
        ConverterUtil.convert2String(data.get("trade_payment_code")))
        .addStringColumn(OrderPaymentObj.FIELD_PAYMENT_ID,
            ConverterUtil.convert2String(data.get("trade_payment_id")))
        .addDecimalColumn(OrderPaymentObj.FIELD_PAYMENT_AMOUNT, amount)
        .addStringColumn(OrderPaymentObj.FIELD_ACCOUNT_ID,
            ConverterUtil.convert2String(data.get("customer_id")))
        .addStringColumn(OrderPaymentObj.FIELD_ORDER_ID,
            ConverterUtil.convert2String(data.get("customer_trade_id")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_LIFE_STATUS, ObjectLifeStatus.NORMAL.getCode())
        .addStringColumn(CrmPackageObjectConstants.FIELD_OWNER,
            ConverterUtil.convert2String(data.get("belonger_id")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_LOCK_STATUS,
            ConverterUtil.convert2String(data.get("lock_status")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_LOCK_USER,
            ConverterUtil.convert2String(data.get("last_locked_by")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_RECORD_TYPE,
            CrmPackageObjectConstants.DEFAULT_RECORD_TYPE)
        .addStringColumn(CrmPackageObjectConstants.FIELD_PACKAGE,
            CrmPackageObjectConstants.DEFAULT_PACKAGE)
        .addLongColumn(CrmPackageObjectConstants.FIELD_VERSION,
            CrmPackageObjectConstants.DEFAULT_VERSION)
        .addLongColumn(CrmPackageObjectConstants.FIELD_IS_DELETED,
            ConverterUtil.convert2Long(data.get("is_deleted")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_CREATED_BY,
            ConverterUtil.convert2String(data.get("creator_id")))
        .addLongColumn(CrmPackageObjectConstants.FIELD_CREATE_TIME,
            ConverterUtil.convert2Long(data.get("create_time")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_LAST_MODIFIED_BY,
            ConverterUtil.convert2String(data.get("updator_id")))
        .addLongColumn(CrmPackageObjectConstants.FIELD_LAST_MODIFIED_TIME,
            ConverterUtil.convert2Long(data.get("update_time")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_REMARK,
            ConverterUtil.convert2String(data.get("remark")))
        .addStringColumn(CrmPackageObjectConstants.FIELD_LIFE_STATUS,
            lifeStatus)
        .addStringColumn(CrmPackageObjectConstants.FIELD_DESCRIBE_API_NAME,
            PaymentObject.ORDER_PAYMENT.getApiName())
        .addStringColumn(CrmPackageObjectConstants.FIELD_DESCRIBE_ID, describeId);

    if(data.get("extend_obj_data_id")!=null) {
      cp.addStringColumn(CrmPackageObjectConstants.FIELD_EXTEND_DATA_ID,
              ConverterUtil.convert2String(data.get("extend_obj_data_id")).replace("t_pa", "pa"));
    }
    return cp;
  }

  private String getLifeStatusValue(int status) {
    switch (status) {
      case 1:
        return ObjectLifeStatus.UNDER_REVIEW.getCode();
      case 3:
        return ObjectLifeStatus.NORMAL.getCode();
      case 4:
        return ObjectLifeStatus.INEFFECTIVE.getCode();
      case 99:
        return ObjectLifeStatus.INVALID.getCode();
      default:
        return ObjectLifeStatus.INEFFECTIVE.getCode();
    }
  }
}
