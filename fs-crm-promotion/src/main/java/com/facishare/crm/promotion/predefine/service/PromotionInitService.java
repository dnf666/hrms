package com.facishare.crm.promotion.predefine.service;

import java.util.List;

import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.appframework.metadata.dto.auth.RoleInfoPojo;

public interface PromotionInitService {
    boolean init(User user);

    void initProductRecordType(User user, String objectApiName, String recordTypeId, String viewId, List<RoleInfoPojo> roleInfoPojos);

    DescribeResult initAdvertisement(User user);

    void initAdvertisementLayoutRule(User user);
}
