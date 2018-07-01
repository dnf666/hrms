package com.facishare.crm.customeraccount.predefine.service;

import java.util.Map;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.rest.dto.ApprovalInitModel;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.appframework.metadata.dto.auth.AddRoleRecordTypeModel;
import com.facishare.paas.appframework.metadata.dto.auth.AddRoleViewModel;
import com.facishare.paas.appframework.metadata.dto.auth.RoleInfoModel;
import com.facishare.paas.metadata.exception.MetadataServiceException;

public interface InitService {
    void initPrepayLayoutRecordType(ServiceContext serviceContext);

    RoleInfoModel.Result roleInfo(RoleInfoModel.Arg arg);

    AddRoleViewModel.Result saveLayoutAssign(AddRoleViewModel.Arg arg);

    AddRoleRecordTypeModel.Result addRoleRecordType(AddRoleRecordTypeModel.Arg arg);

    Boolean init(ServiceContext serviceContext);

    boolean initStartAndEndTimeRule(User user, String objectApiName, String ruleApiName, String ruleDisplayName, String startTimeFieldApiName, String endTimeFieldApiName, String ruleDescription) throws CrmCheckedException, MetadataServiceException;

    ApprovalInitModel.Result initApproval(String objectApiName, Map<String, String> headers);

    DescribeResult initRebateUseRule(User user);
}
