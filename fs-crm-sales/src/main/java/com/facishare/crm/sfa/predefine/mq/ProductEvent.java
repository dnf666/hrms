package com.facishare.crm.sfa.predefine.mq;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Created by luxin on 2017/11/17.
 */
@Data
public class ProductEvent {

    public static final String ADD_ACTION = "Add";
    public static final String ABOLISH_ACTION = "Abolish";
    public static final String DELETE_ACTION = "Delete";
    public static final String RECOVER_ACTION = "Recover";
    public static final String SUPER_ADMIN_USER_ID = "-10000";

    @JSONField(name = "TenantID")
    Integer tenantId;

    @JSONField(name = "TenantAccount")
    String tenantAccount;

    @JSONField(name = "AppID")
    String appId;

    @JSONField(name = "Package")
    String packageName;


    @JSONField(name = "ObjectApiName")
    String apiName;

    @JSONField(name = "ObjectID")
    String objectIdsStr;

    @JSONField(name = "ActionCode")
    String actionCode;

    @JSONField(name = "ActionContent")
    Object actionContent;

    @JSONField(name = "OperatorID")
    Integer operatorId;

    @JSONField(name = "ActionTime")
    Long actionTime;

    @JSONField(name = "Source")
    String source;


    @Override
    public String toString() {
        return "{" +
                "tenantId=" + tenantId +
                ", tenantAccount='" + tenantAccount + '\'' +
                ", appId='" + appId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", objectIdsStr='" + objectIdsStr + '\'' +
                ", actionCode='" + actionCode + '\'' +
                ", actionContent=" + actionContent +
                ", operatorId=" + operatorId +
                ", actionTime=" + actionTime +
                ", source='" + source + '\'' +
                '}';
    }
}
