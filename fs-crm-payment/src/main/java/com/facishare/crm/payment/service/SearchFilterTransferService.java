package com.facishare.crm.payment.service;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.paas.appframework.coordination.CrmService;
import com.facishare.paas.appframework.coordination.dto.GetSearchFilterList;
import com.facishare.paas.appframework.coordination.dto.GetSearchFilterList.SearchFilter;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.MetadataContextExt;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.search.ISearchTemplate;
import com.facishare.paas.metadata.api.service.ISearchTemplateService;
import com.facishare.paas.metadata.common.MetadataContext;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplate;
import com.facishare.paas.metadata.support.GDSHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ServiceModule("searchfiltertransfer")
public class SearchFilterTransferService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchFilterTransferService.class);
  private static final String MAPPING = "CustomerID:account_id,CustomerName:account_id.name,CustomerTradeID:order_id,TradePaymentCode:name,PaymentTime:payment_time,PaymentMoney:payment_amount,PaymentType:payment_term,Remark:remark,BelongerID:owner,RemindTime:notification_time,attach:attachment,CreatorID:created_by,CreateTime:create_time,UpdateTime:last_modified_time,UpdatorID:last_modified_by,Status:life_status,FinanceEmployeeID:finance_employee_id,FinanceConfirmTime:finance_confirm_time,IsDeleted:is_deleted,LockStatus:lock_status,Department:owner_department";
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
  private GDSHandler gdsService;
  @Autowired
  private ISearchTemplateService searchTemplateService;

  @ServiceMethod("payment")
  public boolean transferPayment(ServiceContext context) {
    GetSearchFilterList.Arg arg = new GetSearchFilterList.Arg();
    arg.setTableName("tradepayment");
    String ea = gdsService.getEAByEI(context.getTenantId());
    GetSearchFilterList.Result result = crmService.getSearchFilterList(ea, arg);
    if (null == result || CollectionUtils.isEmpty(result.getFilters())) {
      return true;
    }
    IObjectDescribe describe = getCustomerPaymentDescribe(context.getTenantId());
    for (SearchFilter filter : result.getFilters()) {
      ISearchTemplate template = new SearchTemplate();
      template.setTenantId(context.getTenantId());
      template.setObjectDescribeApiName(describe.getApiName());
      template.setObjectDescribeId(describe.getId());
      template.setCreatedBy(User.SUPPER_ADMIN_USER_ID);
      template.setCreateTime(new Date().getTime());
      template.setIsHidden(filter.getIsHidden());
      template.setLastModifiedTime(new Date().getTime());
      template.setPackage("CRM");
      template.setLabel(filter.getName());
      template.setIsDefault(filter.getIsDefault());
      String userId = "" + filter.getEmployeeId();
      template.setUserId(userId);
      template.setVersion(1);
      template.setOrder(getSearchFilterOrder(context.getTenantId(), userId));
      List<IFilter> filters = filter.getDetails().stream().map(d -> {
        Filter f = new Filter();
        f.setOperator(getOperator(d.getOperator()));
        f.setFieldName(parseFieldName(d.getName()));
        f.setFieldValues(Lists.newArrayList(d.getValue()));
        return f;
      }).collect(Collectors.toList());
      template.setFilters(filters);
      try {
        LOGGER.debug("{}", template);
        if ( !exist(context.getTenantId(), userId, filter.getName()) ) {
          searchTemplateService.create(template, MetadataContextExt.of(context.getUser()).getMetadataContext());
        }
      } catch (Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return false;
      }
    }
    return true;
  }

  private IObjectDescribe getCustomerPaymentDescribe(String tenantId) {
    return serviceFacade.findObject(tenantId, PaymentObject.CUSTOMER_PAYMENT.getApiName());
  }

  private boolean exist(String tenantId, String userId, String label) {
    try {
      MetadataContext context = new MetadataContext();
      context.setTenantId(tenantId);
      context.setUserId(userId);
      context.setAppId("CRM");

      List<ISearchTemplate> templates = searchTemplateService
              .findByTenantIdAndUserId(PaymentObject.CUSTOMER_PAYMENT.getApiName(), context);
      return templates.stream().anyMatch(t -> label.equals(t.getLabel()));
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      return false;
    }
  }

  private Integer getSearchFilterOrder(String tenantId, String userId) {
    try {
        MetadataContext context = new MetadataContext();
        context.setTenantId(tenantId);
        context.setUserId(userId);
        context.setAppId("CRM");

      List<ISearchTemplate> templates = searchTemplateService
          .findByTenantIdAndUserId(PaymentObject.CUSTOMER_PAYMENT.getApiName(), context);
      if (CollectionUtils.isEmpty(templates)) {
        return 1;
      }
      return templates.stream().map(t -> t.getOrder() == null ? 1 : t.getOrder() + 1)
          .max(Integer::compareTo).orElse(1);
    } catch (MetadataServiceException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return 1;
    }
  }

  private static Operator getOperator(Integer operator) {
    if (null == operator) {
      return null;
    }

    // 比较符 1、等于；2、不等于；3、大于；4、大于等于；5、小于；6、小于等于；7、包含；8、不包含;9、为空；10、不为空；
    switch (operator) {
      case 1: return Operator.EQ;
      case 2: return Operator.N;
      case 3: return Operator.GT;
      case 4: return Operator.GTE;
      case 5: return Operator.LT;
      case 6: return Operator.LTE;
      case 7: return Operator.IN;
//      case 8: return Operator.CONTAINS;
      case 9: return Operator.IS;
      case 10: return Operator.ISN;
      case 11: return Operator.STARTWITH;
      case 12: return Operator.ENDWITH;
      case 13: return Operator.IN;
      case 14: return Operator.NIN;
      case 15: return Operator.IS;
      case 16: return Operator.ISN;
//      case 17: return Operator
//      case 18: return Operator.
      case 19: return Operator.BETWEEN;
      default: return null;
    }
  }

  private String parseFieldName(String oldFieldName) {
    return NAME_MAPPING.getOrDefault(oldFieldName, oldFieldName);
  }
}
