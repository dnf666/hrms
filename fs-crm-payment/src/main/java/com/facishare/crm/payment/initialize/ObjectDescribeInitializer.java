package com.facishare.crm.payment.initialize;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.dto.RuleResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import java.util.Map;

import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.IRule;
import com.facishare.paas.metadata.impl.Rule;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectDescribeInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ObjectDescribeInitializer.class);
  private ServiceFacade serviceFacade;
  private IObjectDescribeService describeService;

  public ObjectDescribeInitializer(ServiceFacade serviceFacade,
      IObjectDescribeService describeService) {
    this.serviceFacade = serviceFacade;
    this.describeService = describeService;
  }

  public boolean transfer(String tenantId, BaseObjectDescribe definition, String detailApiName,
      Map<String, String> functionsMapping) {
    try {
      serviceFacade.updateSfaDescribe(generateSuperAdmin(tenantId), definition.getDataJson(),
          definition.getDetailLayoutJson(), definition.getListLayoutJson(), detailApiName,
          functionsMapping);
      serviceFacade.initializeWorkFlow(PaymentObject.CUSTOMER_PAYMENT.getApiName(),
          generateSuperAdmin(tenantId));
    } catch (Exception ex) {
      LOGGER.warn(ex.getMessage(), ex);
    }
    return true;
  }

  public boolean transferFunctions(String tenantId) {
    try {
      serviceFacade.updateSfaFunctionPrivilege(generateSuperAdmin(tenantId),
          PaymentObject.CUSTOMER_PAYMENT.getApiName(), PaymentObject.ORDER_PAYMENT.getApiName(),
          CustomerPaymentObjectDescribe.ACTION_MAPPING);
    } catch (Exception ex) {
      LOGGER.warn(ex.getMessage(), ex);
    }
    return true;
  }

  public boolean initialize(String tenantId, BaseObjectDescribe definition) {
//    IObjectDescribe describe;
//    try {
//      describe = serviceFacade
//          .initializeDescribe(generateSuperAdmin(tenantId), definition.getDataJson(),
//              definition.getDetailLayoutJson(), definition.getListLayoutJson());
//    } catch (Exception ex) {
//      LOGGER.warn(ex.getMessage(), ex);
//      return false;
//    }
//    return null != describe &&
            return createOrUpdateRules(tenantId, definition);
  }

  private boolean createOrUpdateRules(String tenantId, BaseObjectDescribe definition) {
    if (CollectionUtils.isEmpty(definition.getRules())) {
      return true;
    }
    boolean result = true;
    for (BaseRuleDescribe ruleDescribe : definition.getRules()) {
      try {
        RuleResult ruleResult = serviceFacade
            .findRuleInfo(definition.getApiName(), tenantId, ruleDescribe.getApiName());
        if (ruleResult.getRule() == null) {
          serviceFacade.create(ruleDescribe.toRule(tenantId, definition.getApiName()));
        } else {
          IRule updatingRule = new Rule(ruleResult.getRule());
          updatingRule.setRuleName(ruleDescribe.getRuleName());
          updatingRule.setCondition(ruleDescribe.getCondition());
          updatingRule.setMessage(ruleDescribe.getMessage());
          serviceFacade.update(updatingRule);
        }
      } catch (Exception ex) {
        LOGGER.info(ex.getMessage(), ex);
        result = false;
        break;
      }
    }
    return result;
  }

  private User generateSuperAdmin(String tenantId) {
    return new User(tenantId, User.SUPPER_ADMIN_USER_ID);
  }
}
