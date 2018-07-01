package com.facishare.crm.sfa.predefine.action;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.exception.RecordTypeNotFound;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.BaseObjectApprovalAction;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.metadata.TeamMember;
import com.facishare.paas.appframework.metadata.dto.RuleResult;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.describe.IObjectReferenceField;
import com.facishare.paas.metadata.api.describe.Payment;
import com.facishare.paas.metadata.api.describe.SignIn;
import com.facishare.paas.metadata.impl.describe.PaymentFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SignInFieldDescribe;
import com.facishare.paas.metadata.util.SpringUtil;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA. User: quzhf Date: 2017/11/23 16:29 Description:
 */
@Slf4j
public abstract class BaseObjectBulkSaveAction extends BaseObjectApprovalAction<BaseObjectBulkSaveAction.Arg, BaseObjectBulkSaveAction.Result> {
    protected List<IObjectData> objectDataList;
    @Autowired
    protected ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) SpringUtil.getContext().getBean("taskExecutor");

    protected abstract String getIRule();

    protected abstract ObjectAction getObjectAction();

    @Override
    protected void before(BaseObjectBulkSaveAction.Arg arg) {
        super.before(arg);
        init();
        validate();
    }

    @Override
    protected void finallyDo() {
        stopWatch.logSlow(2000);
    }

    protected void init() {
        this.objectDataList = arg.getDataList().stream().map(k -> k.toObjectData()).collect(Collectors.toList());
        stopWatch.lap("init");
    }

    protected void validate() {
        CountDownLatch latch = new CountDownLatch(objectDataList.size());
        stopWatch.lap("validate");
        objectDataList.forEach(objectData -> {
            executor.execute(() -> {
                //校验arg参数的完整性和合法性
                ObjectDataExt.of(objectData).validate(objectDescribe);
                latch.countDown();
            });
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("validate,CountDownLatch error.", e);
        }
        validateLookupData(objectDataList, objectDescribe);
        //校验规则校验
        validateValidationRules(objectDataList, objectDescribe, getIRule());
        stopWatch.lap("validateRule");
    }

    protected void batchSetDefaultRecordType(List<IObjectData> objectDataList, ObjectDescribeExt describeExt) {
        objectDataList.forEach(objectData -> {
            objectData.setTenantId(describeExt.getTenantId());
            String recordType = objectData.getRecordType();
            if (Strings.isNullOrEmpty(recordType) || "default".equals(recordType) || "sail".equals(recordType)) {
                objectData.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
            } else {
                Optional<IRecordTypeOption> optional = describeExt.getRecordTypeOption(recordType);
                IRecordTypeOption option = optional.orElseThrow(() -> new RecordTypeNotFound("业务类型不存在"));
                if (!option.isActive()) {
                    throw new RecordTypeNotFound(String.format("业务类型[%s]已被禁用", option.getLabel()));
                }
            }
        });
    }

    protected void validateValidationRules(List objectDataList, IObjectDescribe objectDescribe, String ruleOperation) {
        Map<String, IObjectDescribe> describeMap = Maps.newHashMap();
        describeMap.put(objectDescribe.getApiName(), objectDescribe);
        HashMap<String, List<IObjectData>> dataMap = Maps.newHashMap();
        dataMap.put(objectDescribe.getApiName(), objectDataList);
        RuleResult ruleResult = serviceFacade.validateRule(actionContext.getUser(), ruleOperation, objectDescribe, objectDataList);

        if (ruleResult.isMatch()) {
            throw new ValidateException(ruleResult.getFailMessage());
        }
    }


    protected void batchModifyObjectDataBeforeCreate(List<IObjectData> objectDataList, IObjectDescribe describe) {
        ObjectDescribeExt objectDescribeExt = ObjectDescribeExt.of(objectDescribe);
        CountDownLatch latch = new CountDownLatch(objectDataList.size());
        objectDataList.stream().forEach(objectData -> {
            executor.execute(() -> {
                ObjectDataExt objectDataExt = ObjectDataExt.of(objectData);
                // 修改objectData,写入tenantId、创建人、最后修改人、最后修改时间等系统字段。
                setDefaultSystemInfo(objectData);
                //设置默认的相关团队成员的负责人
                setDefaultTeamMember(objectDataExt);
                //设置签到字段的相关默认值
                setDefaultForSignIn(objectData, objectDescribeExt);
                //设置支付字段的相关默认值
                setDefaultForPayment(objectData, objectDescribeExt);
                latch.countDown();
            });
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("batchModifyObjectDataBeforeCreate list,CountDownLatch error.", e);
        }
        //设置默认业务类型
        batchSetDefaultRecordType(objectDataList, objectDescribeExt);
        //设置负责人
        batchSynchronizeOwnerWithMasterObjectData(objectDataList, objectDescribeExt);
    }

    protected void setDefaultSystemInfo(IObjectData objectData) {
        objectData.setTenantId(actionContext.getTenantId());
        objectData.setCreatedBy(actionContext.getUser().getUserId());
        objectData.setLastModifiedBy(actionContext.getUser().getUserId());
        objectData.set(UdobjConstants.LIFE_STATUS_API_NAME, UdobjConstants.LIFE_STATUS_VALUE_NORMAL);
    }


    protected void setDefaultTeamMember(ObjectDataExt objectDataExt) {
        objectDataExt.getOwnerId().ifPresent(x -> {
            TeamMember teamMember = new TeamMember(x, TeamMember.Role.OWNER, TeamMember.Permission.READANDWRITE);
            objectDataExt.addTeamMembers(Lists.newArrayList(teamMember));
        });
    }

    protected void setDefaultForPayment(IObjectData objectData, ObjectDescribeExt objectDescribeExt) {
        Optional<PaymentFieldDescribe> signInFieldDescribe = objectDescribeExt.getPaymentFieldDescribe();
        signInFieldDescribe.ifPresent(x -> {
            objectData.set(x.getPayStatusFieldApiName(), Payment.PAY_STATUS_INCOMPLETE);
        });
    }

    protected void setDefaultForSignIn(IObjectData objectData, ObjectDescribeExt objectDescribeExt) {
        Optional<SignInFieldDescribe> signInFieldDescribe = objectDescribeExt.getSignInFieldDescribe();
        signInFieldDescribe.ifPresent(x -> {
            objectData.set(x.getSignInInfoListFieldApiName(), null);
            objectData.set(x.getIntervalFieldApiName(), null);
            objectData.set(x.getVisitStatusFieldApiName(), SignIn.SIGN_STATUS_INCOMPLETE);
            objectData.set(x.getSignInStatusFieldApiName(), SignIn.SIGN_STATUS_INCOMPLETE);
            objectData.set(x.getSignInTimeFieldApiName(), null);
            objectData.set(x.getSignOutStatusFieldApiName(), SignIn.SIGN_STATUS_INCOMPLETE);
            objectData.set(x.getSignOutTimeFieldApiName(), null);
            objectData.set(x.getSignInLocationFieldApiName(), null);
            objectData.set(x.getSignOutLocationFieldApiName(), null);
        });
    }

    protected void batchSynchronizeOwnerWithMasterObjectData(List<IObjectData> objectDataList, ObjectDescribeExt describeExt) {
        describeExt.getMasterDetailFieldDescribe().ifPresent(masterDetailFieldDescribe -> {
            String masterDescribeApiName = masterDetailFieldDescribe.getTargetApiName();
            //任取其中一条
            IObjectData objectData = objectDataList.get(0);
            String masterId = objectData.get(masterDetailFieldDescribe.getApiName(), String.class);
            //值得注意的是,如果masterId==null也是正常的情况,因为此函数的进入场景可能是主从一起新建时的从对象,这时候就是没有masterId的。
            if (masterId != null) {
                IObjectDescribe masterDescribe = serviceFacade.findObject(objectData.getTenantId(), masterDescribeApiName);
                IObjectData masterObjectData = serviceFacade.findObjectData(objectData.getTenantId(), masterId, masterDescribe);
                Optional<String> ownerId = ObjectDataExt.of(masterObjectData).getOwnerId();
                //所有数据都新增负责人字段
                ownerId.ifPresent(x -> {
                    Objects.requireNonNull(ownerId);
                    objectDataList.forEach(k -> {
                        k.set(ObjectDataExt.OWNER, Arrays.asList(x));
                    });
                });

            }
        });
    }

    protected void validateLookupData(List<IObjectData> objectDataList, IObjectDescribe describe) {
        ObjectDescribeExt describeExt = ObjectDescribeExt.of(describe);
        List<IFieldDescribe> fields = describeExt.filter(a -> Objects.equals(a.getType(), IFieldType.OBJECT_REFERENCE));
        if (CollectionUtils.empty(fields)) {
            return;
        }
        objectDataList.forEach(objectData -> {
            for (IFieldDescribe fieldDescribe : fields) {
                serviceFacade.validateLookupData(actionContext.getUser(), objectData, (IObjectReferenceField) fieldDescribe);
            }
        });
    }

    protected void logAsync(Map<String, IObjectDescribe> objectDescribes, List<IObjectData> allObjectData, EventType eventType, ActionType actionType) {
        Map<String, List<String>> idMap = Maps.newHashMap();
        for (IObjectData data : allObjectData) {
            if (idMap.containsKey(data.getDescribeApiName())) {
                idMap.get(data.getDescribeApiName()).add(data.getId());
            } else {
                idMap.put(data.getDescribeApiName(), Lists.newArrayList(data.getId()));
            }
        }

        ParallelUtils.ParallelTask parallelTask = ParallelUtils.createParallelTask();
        Set<Map.Entry<String, List<String>>> entries = idMap.entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            parallelTask.submit(() -> {
                List<IObjectData> dataList = serviceFacade.findObjectDataByIds(
                        actionContext.getTenantId(), entry.getValue(), entry.getKey());
                serviceFacade.log(actionContext.getUser(), eventType, actionType, objectDescribes, dataList);
            });
        }
        try {
            parallelTask.await(20, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("Time out for log in add", e);
        }
    }

    @Data
    public static class Arg {
        @JsonProperty("data_list")
        List<ObjectDataDocument> dataList;
    }

    @Data
    @Builder
    public static class Result {
        List<ObjectDataDocument> dataList;
    }
}