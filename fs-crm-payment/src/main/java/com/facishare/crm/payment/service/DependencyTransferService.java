package com.facishare.crm.payment.service;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.paas.appframework.coordination.CrmService;
import com.facishare.paas.appframework.coordination.dto.GetFieldDependencyList;
import com.facishare.paas.appframework.coordination.dto.GetFieldDependencyList.FieldDependency;
import com.facishare.paas.appframework.coordination.dto.GetOptionDependencyList;
import com.facishare.paas.appframework.coordination.dto.GetOptionDependencyList.OptionDependency;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ServiceModule("dependencytransfer")
public class DependencyTransferService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DependencyTransferService.class);

  @Autowired
  private CrmService crmService;
  @Autowired
  private ServiceFacade serviceFacade;
  @Autowired
  private IObjectDescribeService describeService;

  @ServiceMethod("payment")
  public boolean transferPayment(ServiceContext context) {
    String tenantId = context.getTenantId();
    GetFieldDependencyList.Arg arg = new GetFieldDependencyList.Arg();
    arg.setSource(GetFieldDependencyList.SOURCE_PAYMENT);
    GetFieldDependencyList.Result result = crmService.getFieldDependencyList(tenantId, arg);
    if ( null == result || CollectionUtils.isEmpty(result.getDependencyList())) {
      return true;
    }
    IObjectDescribe describe = serviceFacade.findObject(tenantId, PaymentObject.CUSTOMER_PAYMENT.getApiName());
    List<IFieldDescribe> fieldDescribeList = new ArrayList<>();
    for(FieldDependency dependency : result.getDependencyList()) {
      GetOptionDependencyList.Arg optionArg = new GetOptionDependencyList.Arg();
      optionArg.setSource(GetFieldDependencyList.SOURCE_PAYMENT);
      optionArg.setControlFieldName(dependency.getControlFieldName());
      optionArg.setDependFieldName(dependency.getDependFieldName());
      GetOptionDependencyList.Result optionResult = crmService.getOptionDependencyList(tenantId, optionArg);
      SelectOneFieldDescribe fieldDescribe = findSelectOneFieldDescribe(describe, dependency.getControlFieldName());
      SelectOneFieldDescribe dependFieldDescribe = findSelectOneFieldDescribe(describe, dependency.getDependFieldName());
      if (null == fieldDescribe || null == dependFieldDescribe) {
        continue;
      }
      dependFieldDescribe.set("cascade_parent_api_name", fieldDescribe.getApiName());
      updateDependencyOptions(fieldDescribe, optionResult.getDependencyList());
      fieldDescribeList.add(fieldDescribe);
      fieldDescribeList.add(dependFieldDescribe);
    }
    if ( CollectionUtils.isEmpty(fieldDescribeList) ) {
      return true;
    }
    describe.setFieldDescribes(fieldDescribeList);
    try {
      describeService.updateFieldDescribe(describe, fieldDescribeList);
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return true;
  }

  private SelectOneFieldDescribe findSelectOneFieldDescribe(IObjectDescribe describe, String fieldApiName) {
    if ( null == describe ) {
      return null;
    }
    if( "PaymentType".equals(fieldApiName) ) {
      fieldApiName = "payment_term";
    }
    if ( fieldApiName.startsWith("UD") ) {
      fieldApiName = fieldApiName + "__c";
    }
      IFieldDescribe field = describe.getFieldDescribe(fieldApiName);
      if ( null != field && field instanceof SelectOneFieldDescribe ) {
        return (SelectOneFieldDescribe)field;
      }
      LOGGER.warn("SelectOne field describe {} not found.", fieldApiName);
      return null;
  }

  private String translateFieldApiName(String fieldName) {
    if ( fieldName.startsWith("UD") ) {
      return fieldName + "__c";
    }
    return fieldName;
  }

  @SuppressWarnings("unchecked")
  private void updateDependencyOptions(SelectOneFieldDescribe fieldDescribe, List<OptionDependency> optionDependencyList) {
    Map<String, Map<String, List<String>>> childOptionMap = new HashMap<>();
    for( OptionDependency optionDependency : optionDependencyList ) {
      Map<String, List<String>> childOption = childOptionMap.getOrDefault(optionDependency.getCode(), new HashMap<>());
      List<String> dependencyOptions = childOption.getOrDefault(translateFieldApiName(optionDependency.getFieldName()), new ArrayList<>());
      dependencyOptions.add(optionDependency.getItemCode());
      childOption.put(translateFieldApiName(optionDependency.getFieldName()), dependencyOptions);
      childOptionMap.put(optionDependency.getCode(), childOption);
    }
    for( ISelectOption option : fieldDescribe.getSelectOptions() ) {
      if ( childOptionMap.containsKey(option.getValue()) ) {
        option.setChildOptions(Lists.newArrayList(childOptionMap.get(option.getValue())));
      }
    }
  }
}
