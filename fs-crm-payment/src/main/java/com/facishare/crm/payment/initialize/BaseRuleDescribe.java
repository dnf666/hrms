package com.facishare.crm.payment.initialize;

import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.impl.IRule;
import com.facishare.paas.metadata.impl.Rule;
import com.google.common.collect.Lists;
import java.io.Serializable;

public class BaseRuleDescribe implements Serializable {

  private static final long serialVersionUID = -4341143740485559677L;

  private String apiName;
  private String ruleName;
  private String condition;
  private String message;

  public BaseRuleDescribe(String apiName, String ruleName, String condition, String message) {
    this.apiName = apiName;
    this.ruleName = ruleName;
    this.condition = condition;
    this.message = message;
  }

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public String getRuleName() {
    return ruleName;
  }

  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public IRule toRule(String tenantId, String describeApiName) {
    IRule rule = new Rule();
    rule.setTenantId(tenantId);
    rule.setApiName(apiName);
    rule.setRuleName(ruleName);
    rule.setDescribeApiName(describeApiName);
    rule.setScene(Lists.newArrayList("create", "update"));
    rule.setIsActive(true);
    rule.setCondition(condition);
    rule.setMessage(message);
    rule.setDefaultToZero(true);
    rule.setIsSave(false);
    rule.setCreatedBy(User.SUPPER_ADMIN_USER_ID);
    rule.setLastModifiedBy(User.SUPPER_ADMIN_USER_ID);
    return rule;
  }
}
