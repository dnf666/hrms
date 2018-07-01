package com.facishare.crm.payment.service;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.paas.appframework.coordination.CrmService;
import com.facishare.paas.appframework.coordination.dto.GetTableConfigList;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldListConfig;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IFieldListConfigService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.FieldListConfig;
import com.google.common.collect.ImmutableMap;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ServiceModule("tableconfigtransfer")
public class TableConfigTransferService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TableConfigTransferService.class);

  private static final String MAPPING = "CustomerID:account_id,CustomerName:account_id__r,CustomerTradeID:order_id,TradePaymentCode:name,PaymentTime:payment_time,PaymentMoney:payment_amount,PaymentType:payment_term,Remark:remark,BelongerName:owner,RemindTime:notification_time,attach:attachment,CreatorID:created_by,CreateTime:create_time,UpdateTime:last_modified_time,UpdatorID:last_modified_by,Status:life_status,FinanceEmployeeID:finance_employee_id,FinanceConfirmTime:finance_confirm_time,IsDeleted:is_deleted,LockStatus:lock_status,Department:owner_department,SubmitTime:create_time";
  private static final Map<String, String> NAME_MAPPING;

  static {
    NAME_MAPPING = new HashMap<>();
    String[] mappingNames = MAPPING.split(",");
    for(String mappingName : mappingNames) {
      String[] pair = mappingName.split(":");
      NAME_MAPPING.put(pair[0], pair[1]);
    }
  }

  @Autowired
  private CrmService crmService;
  @Autowired
  private ServiceFacade serviceFacade;
  @Autowired
  private IFieldListConfigService listConfigService;

  @ServiceMethod("payment")
  public boolean transferPayment(ServiceContext context) {
    GetTableConfigList.Arg arg = new GetTableConfigList.Arg();
    arg.setTableName("tradepayment");
    GetTableConfigList.Result result = crmService.getTableConfigList(context.getTenantId(), arg);
    if ( null == result || null == result.getConfig() || CollectionUtils.isEmpty(result.getConfig().getDetails())) {
      return true;
    }
    IObjectDescribe describe = getCustomerPaymentDescribe(context.getTenantId());
    Map<String, Integer> fields = getFields(describe);
    Map<String, Map<String, Integer>> userConfigs = new HashMap<>();
    result.getConfig().getDetails().forEach(d -> {
      String userId = "" + d.getEmployeeId();
      Map<String, Integer> configs = userConfigs.getOrDefault(userId, new HashMap<>(fields));
      if (!d.getIsVisible()) {
        configs.put(getFieldName(d.getFieldName()), Integer.MAX_VALUE);
      }
      if (d.getOrder() != null) {
        configs.put(getFieldName(d.getFieldName()), d.getOrder());
      }
      userConfigs.put(userId, configs);
    });
    for(Map.Entry<String, Map<String, Integer>> entry : userConfigs.entrySet()) {
      IFieldListConfig config = new FieldListConfig();
      List<Map<String, Boolean>> fieldList = entry.getValue().entrySet().stream().sorted(
          Comparator.comparingInt(Entry::getValue)).map(e -> ImmutableMap.of(e.getKey(),
          e.getValue() != Integer.MAX_VALUE)).collect(
          Collectors.toList());
      config.setFieldList(fieldList);
      createOrUpdateConfig(context.getTenantId(), entry.getKey(), describe.getApiName(), config);
    }
    return true;
  }

  private IObjectDescribe getCustomerPaymentDescribe(String tenantId) {
    return serviceFacade.findObject(tenantId, PaymentObject.CUSTOMER_PAYMENT.getApiName());
  }

  private Map<String, Integer> getFields(IObjectDescribe describe) {
    Map<String, Integer> result = new HashMap<>();
    List<IFieldDescribe> fields = describe.getFieldDescribes();
    for (int i = 1; i <= fields.size(); ++i) {
      result.put(fields.get(i-1).getApiName(), i);
    }
    return result;
  }

  private String getFieldName(String name) {
    return NAME_MAPPING.getOrDefault(name, name);
  }

  private void createOrUpdateConfig(String tenantId, String userId, String describeApiName, IFieldListConfig config) {
    try {
      IFieldListConfig exists = listConfigService
          .findByDescribeApiName(tenantId, userId, describeApiName);
      if (null != exists) {
        exists.setFieldList(config.getFieldList());
        listConfigService.update(exists);
      } else {
        config.setTenantId(tenantId);
        config.setUserId(userId);
        config.setDescribeApiName(describeApiName);
        config.setCreateTime(System.currentTimeMillis());
        config.setLastModifiedTime(System.currentTimeMillis());
        config.setCreatedBy(userId);
        config.setVersion(1);
        listConfigService.create(config);
      }
    } catch (MetadataServiceException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }
}
