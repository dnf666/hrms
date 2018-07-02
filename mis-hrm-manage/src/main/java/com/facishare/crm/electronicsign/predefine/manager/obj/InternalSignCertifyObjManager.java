package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.CertifyRecordManager;
import com.facishare.crm.electronicsign.predefine.manager.CommonManager;
import com.facishare.crm.electronicsign.predefine.manager.InternalSignCertifyUseRangeManager;
import com.facishare.crm.electronicsign.predefine.manager.bestsign.BestSignRegManager;
import com.facishare.crm.electronicsign.predefine.manager.bestsign.BestSignSignatureImageManager;
import com.facishare.crm.electronicsign.predefine.service.dto.CertifyRecordType;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.metadata.MetaDataActionService;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.uc.api.util.MobileUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InternalSignCertifyObjManager extends CommonManager {
    private static final int MAX_LIMIT_FOR_QUERY_ALL = 1000000;

    @Autowired
    private MetaDataActionService metaDataActionService;
    @Resource
    private InternalSignCertifyUseRangeManager internalSignCertifyUseRangeManager;
    @Resource
    private BestSignSignatureImageManager bestSignSignatureImageManager;
    @Resource
    private BestSignRegManager bestSignRegManager;
    @Resource
    private CertifyRecordManager certifyRecordManager;

    /**
     * 上上签认证
     */
    public void reg(User user, ObjectDataDocument objectData) {
        //认证
        String taskId = null;
        try {
            taskId = bestSignRegManager.tenantReg(objectData);
            //更新为"认证中"
            IObjectData newObjectData = getObjectDataById(user, objectData.get(InternalSignCertifyObjConstants.Field.Id.getApiName()).toString());
            updateCertifyResult(user, newObjectData, CertifyStatusEnum.CERTIFYING.getStatus(), null, null, false);
        } catch (ElecSignBusinessException e) {
            log.warn("InternalSignCertifyObjManager.reg, failed, user[{}], objectData[{}]", user, objectData, e);
            //注册企业并申请证书失败, 13760479322tenant71595 legalPersonIdentity[430522198809013333] wrong

            IObjectData newObjectData = getObjectDataById(user, objectData.get(InternalSignCertifyObjConstants.Field.Id.getApiName()).toString());  //这里查一次，是因为从after过来的version=1，而去更新时，库里version已经=2
            log.info("newObjectData[{}], objectData[{}]", newObjectData, objectData);
            updateCertifyResult(user, newObjectData, CertifyStatusEnum.FAIL.getStatus(), e.getMessage(), null, false);
            //增加认证记录
            certifyRecordManager.addForTenantCertify(user, objectData, taskId);
            throw e;
        } catch (Exception e) {
            log.warn("InternalSignCertifyObjManager.reg, failed, user[{}], objectData[{}]", user, objectData, e);
            IObjectData newObjectData = getObjectDataById(user, objectData.get(InternalSignCertifyObjConstants.Field.Id.getApiName()).toString());
            log.info("newObjectData[{}], objectData[{}]", newObjectData, objectData);
            updateCertifyResult(user, newObjectData, CertifyStatusEnum.FAIL.getStatus(), e.toString(), null, false);
            certifyRecordManager.addForTenantCertify(user, objectData, taskId);
            throw e;
        }

        //增加认证记录
        certifyRecordManager.addForTenantCertify(user, objectData, taskId);
    }

    public IObjectData getObjectDataById(User user, String id) {
        return this.serviceFacade.findObjectDataIncludeDeleted(user, id, InternalSignCertifyObjConstants.API_NAME);
    }

    public String generateBestSignAccount(User user, String regMobile) {
        return regMobile + "tenant" + user.getTenantId();
    }

    /**
     * 启用停用
     */
    public void enableOrDisable(User user, String id, String newUseStatus) {
        IObjectData objectData = queryById(user, id);
        String oldUseStatus = (String) objectData.get(InternalSignCertifyObjConstants.Field.UseStatus.apiName);
        if (!Objects.equals(oldUseStatus, newUseStatus)) {
            updateUseStatus(user, objectData, newUseStatus, false);
        }
    }

    public void checkMobile(BaseObjectSaveAction.Arg arg) {
        String regMobile = (String) arg.getObjectData().get(InternalSignCertifyObjConstants.Field.RegMobile.apiName);
        String legalPersonMobile = (String) arg.getObjectData().get(InternalSignCertifyObjConstants.Field.LegalPersonMobile.apiName);
        if (!MobileUtil.isMobile(regMobile)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册手机号:请输入正确的手机号");
        }
        if (!MobileUtil.isMobile(legalPersonMobile)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "法人或经办人手机号:请输入正确的手机号");
        }
    }

    /**
     * regMobile在tenantId中是否已经使用（不包含已作废的数据）
     */
    public boolean hasRegMobileUsed(User user, String regMobile) {
        if (CollectionUtils.isEmpty(queryByRegMobile(user, regMobile))) {
            return false;
        }
        return true;
    }

    /**
     * enterpriseName在tenantId中是否已经使用（不包含已作废的数据）
     */
    public boolean hasEnterpriseNameUsed(User user, String enterpriseName) {
        if (CollectionUtils.isEmpty(queryByEnterpriseName(user, enterpriseName))) {
            return false;
        }
        return true;
    }

    /**
     * unifiedSocialCreditIdentifier在tenantId中是否已经使用（不包含已作废的数据）
     */
    public boolean hasUnifiedSocialCreditIdentifierUsed(User user, String unifiedSocialCreditIdentifier) {
        if (CollectionUtils.isEmpty(queryByUnifiedSocialCreditIdentifier(user, unifiedSocialCreditIdentifier))) {
            return false;
        }
        return true;
    }

    /**
     * 租户是否有'已认证' '已启用'的记录
     */
    public boolean isHasCertifiedAndEnableRecord(User user) {
        List<IObjectData> objectDatas = queryByTenantId(user);
        if (CollectionUtils.isEmpty(objectDatas)) {
            return false;
        }

        for (IObjectData objectData : objectDatas) {
            String certifyStatus = (String) objectData.get(InternalSignCertifyObjConstants.Field.CertifyStatus.apiName);
            String useStatus = (String) objectData.get(InternalSignCertifyObjConstants.Field.UseStatus.apiName);
            if (Objects.equals(certifyStatus, CertifyStatusEnum.CRTTIFIED.getStatus())
                    && Objects.equals(useStatus, UseStatusEnum.ON.getStatus())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否存在已经认证的数据
     */
    public boolean hasAnyCertifiedObjectData(String tenantId) {
        User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFiltersWithUser(user, filters);
        SearchUtil.fillFilterEq(filters, InternalSignCertifyObjConstants.Field.CertifyStatus.apiName, CertifyStatusEnum.CRTTIFIED.getStatus());
        return searchQuery(user, InternalSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, 1).getTotalNumber() > 0;
    }

    /**
     * 认证回调
     */
    public void certifyCallback(User user, String bestSignAccount, String certifyStatus, CertifyRecordType.CertCallBack.Arg arg) {
        IObjectData objectData = queryByBestSignAccount(user, bestSignAccount);
        //更新certifyStatus certifyErrMsg
        String useStatus = null;
        if (Objects.equals(CertifyStatusEnum.CRTTIFIED.getStatus(), certifyStatus)) {
            useStatus = UseStatusEnum.ON.getStatus();
        }
        updateCertifyResult(user, objectData, certifyStatus, arg.getMessage(), useStatus, false);

        //更新'内部签章认证'使用范围的状态
        internalSignCertifyUseRangeManager.updateStatus(bestSignAccount, certifyStatus, useStatus);

        //生成签章
        String enterpriseName = (String) objectData.get(InternalSignCertifyObjConstants.Field.EnterpriseName.apiName);
        bestSignSignatureImageManager.createAndUploadSignatureImage(user, bestSignAccount, enterpriseName);
    }

    public Map<String, String> queryIdsByBestSignAccounts(User user, List<String> bestSignAccounts) {
        if (CollectionUtils.isEmpty(bestSignAccounts)) {
            return new HashMap<>();
        }
        List<IObjectData> objectDatas = queryByBestSignAccounts(user, bestSignAccounts);
        if (CollectionUtils.isEmpty(objectDatas)) {
            return new HashMap<>();
        }

        return objectDatas.stream().collect(Collectors.toMap(
                p -> p.get(InternalSignCertifyObjConstants.Field.BestSignAccount.apiName, String.class),
                p -> p.get(InternalSignCertifyObjConstants.Field.Id.apiName, String.class)
        ));
    }

    public List<IObjectData> queryByRegMobile(User user, String regMobile) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, InternalSignCertifyObjConstants.Field.RegMobile.apiName, regMobile);
        return searchQuery(user, InternalSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public List<IObjectData> queryByEnterpriseName(User user, String enterpriseName) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, InternalSignCertifyObjConstants.Field.EnterpriseName.apiName, enterpriseName);
        return searchQuery(user, InternalSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public List<IObjectData> queryByUnifiedSocialCreditIdentifier(User user, String unifiedSocialCreditIdentifier) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, InternalSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName, unifiedSocialCreditIdentifier);
        return searchQuery(user, InternalSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public List<IObjectData> queryByTenantId(User user) {
        List<IFilter> filters = Lists.newArrayList();
        return searchQuery(user, InternalSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public IObjectData queryByBestSignAccount(User user, String bestSignAccount) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, InternalSignCertifyObjConstants.Field.BestSignAccount.apiName, bestSignAccount);
        List<IObjectData> objectDatas = searchQuery(user, InternalSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
        if (CollectionUtils.isEmpty(objectDatas)) {
            return null;
        }
        return objectDatas.get(0);
    }

    public List<IObjectData> queryByBestSignAccounts(User user, List<String> bestSignAccounts) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, InternalSignCertifyObjConstants.Field.BestSignAccount.apiName, bestSignAccounts);
        return searchQuery(user, InternalSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public IObjectData queryById(User user, String id) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, InternalSignCertifyObjConstants.Field.Id.apiName, id);
        List<IObjectData> objectDatas = searchQuery(user, InternalSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
        if (CollectionUtils.isEmpty(objectDatas)) {
            return null;
        }
        return objectDatas.get(0);
    }

    /**
     * 修改认证状态
     * @param allowUpdateInvalid 是否可以更新作废数据
     */
    public IObjectData updateCertifyResult(User user, IObjectData objectData, String certifyStatus, String certifyErrMsg, String useStatus, boolean allowUpdateInvalid) {
        if (certifyStatus != null) {
            objectData.set(InternalSignCertifyObjConstants.Field.CertifyStatus.apiName, certifyStatus);
        }
        objectData.set(InternalSignCertifyObjConstants.Field.CertifyErrMsg.apiName, certifyErrMsg);
        if (useStatus != null) {
            objectData.set(InternalSignCertifyObjConstants.Field.UseStatus.apiName, useStatus);
        }
        IObjectData newObjectData = metaDataActionService.updateObjectData(user, objectData, allowUpdateInvalid);
        log.info("metaDataActionService.updateObjectData, user:{}, data:{}, allowUpdateInvalid:{}, result:{}", user, objectData, allowUpdateInvalid, newObjectData);
        return newObjectData;
    }

    /**
     * 更新启用停用中状态
     * @param allowUpdateInvalid 是否可以更新作废数据
     */
    public IObjectData updateUseStatus(User user, IObjectData objectData, String useStatus, boolean allowUpdateInvalid) {
        if (useStatus != null) {
            objectData.set(InternalSignCertifyObjConstants.Field.UseStatus.apiName, useStatus);
        }
        IObjectData newObjectData = metaDataActionService.updateObjectData(user, objectData, allowUpdateInvalid);
        log.info("metaDataActionService.updateObjectData, user:{}, data:{}, allowUpdateInvalid:{}, result:{}", user, objectData, allowUpdateInvalid, newObjectData);
        return newObjectData;
    }
}
