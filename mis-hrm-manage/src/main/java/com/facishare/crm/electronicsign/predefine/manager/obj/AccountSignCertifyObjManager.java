package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import com.facishare.crm.electronicsign.enums.type.CertifyTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.CertifyRecordManager;
import com.facishare.crm.electronicsign.predefine.manager.CommonManager;
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

/**
 * 客户签章认证
 */
@Service
@Slf4j
public class AccountSignCertifyObjManager extends CommonManager {
    private static final int MAX_LIMIT_FOR_QUERY_ALL = 1000000;

    @Autowired
    private MetaDataActionService metaDataActionService;
    @Resource
    private BestSignSignatureImageManager bestSignSignatureImageManager;
    @Resource
    private BestSignRegManager bestSignRegManager;
    @Resource
    private CertifyRecordManager certifyRecordManager;

    public List<IObjectData> findObjectDataByIdsIncludeDeleted(User user, List<String> ids) {
        return serviceFacade.findObjectDataByIdsIncludeDeleted(user, ids, AccountSignCertifyObjConstants.API_NAME);
    }

    /**
     * 上上签认证
     */
    public void reg(User user, ObjectDataDocument objectDataDocument) {
        //认证
        String recordType = (String) objectDataDocument.get(SystemConstants.Field.RecordType.apiName);
        String taskId = null;
        Integer certifyType = null;
        try {
            if (Objects.equals(recordType, AccountSignCertifyObjConstants.RecordType.EnterpriseRecordType.apiName)) {
                certifyType = CertifyTypeEnum.ENTERPRISE.getType();
                taskId = bestSignRegManager.eaAccountReg(objectDataDocument);

            } else {
                certifyType = CertifyTypeEnum.INDIVIDUAL.getType();
                taskId = bestSignRegManager.individualAccountReg(objectDataDocument);
            }
            //更新为"认证中"
            IObjectData newObjectData = getObjectDataById(user, objectDataDocument.toObjectData().get(AccountSignCertifyObjConstants.Field.Id.getApiName()).toString());
            updateCertifyResult(user, newObjectData, CertifyStatusEnum.CERTIFYING.getStatus(), null, null, false);
        } catch (ElecSignBusinessException e) {
            //注册企业并申请证书失败, 13760479322eaAccount71595 legalPersonIdentity[430522198809013333] wrong
            IObjectData newObjectData = getObjectDataById(user, objectDataDocument.toObjectData().get(AccountSignCertifyObjConstants.Field.Id.getApiName()).toString());  //这里查一次，是因为从after过来的version=1，而去更新时，库里version已经=2
            updateCertifyResult(user, newObjectData, CertifyStatusEnum.FAIL.getStatus(), e.getMessage(), null, false);
            //增加认证记录
            certifyRecordManager.addForAccountCertify(user, objectDataDocument, certifyType, taskId);
            throw e;
        } catch (Exception e) {
            IObjectData newObjectData = getObjectDataById(user, objectDataDocument.toObjectData().get(AccountSignCertifyObjConstants.Field.Id.getApiName()).toString());
            updateCertifyResult(user, newObjectData, CertifyStatusEnum.FAIL.getStatus(), e.toString(), null, false);
            certifyRecordManager.addForAccountCertify(user, objectDataDocument, certifyType, taskId);
            throw e;
        }

        //增加认证记录
        certifyRecordManager.addForAccountCertify(user, objectDataDocument, certifyType, taskId);
    }

    /**
     * 如果用同一个账号注册了个人账号，在注册企业账号，会报错：{"errno":240009,"cost":40,"errmsg":"13277977535account55988 not a enterprise user"}
     * 所以这里区分开
     */
    public String generateBestSignAccount(User user, String regMobile, String recordType) {
        if (Objects.equals(recordType, AccountSignCertifyObjConstants.RecordType.EnterpriseRecordType.apiName)) {
            return regMobile + "eaAccount" + user.getTenantId();
        } else if (Objects.equals(recordType, AccountSignCertifyObjConstants.RecordType.IndividualRecordType.apiName)) {
            return regMobile + "individualAccount" + user.getTenantId();
        } else {
            throw new ElecSignBusinessException(ElecSignErrorCode.GENERATE_BEST_SIGN_ACCOUNT);
        }
    }

    /**
     * 启用停用
     */
    public void enableOrDisable(User user, String id, String newUseStatus) {
        IObjectData objectData = queryById(user, id);
        String oldUseStatus = (String) objectData.get(AccountSignCertifyObjConstants.Field.UseStatus.apiName);
        if (!Objects.equals(oldUseStatus, newUseStatus)) {
            updateUseStatus(user, objectData, newUseStatus, false);
        }
    }

    public void checkMobile(BaseObjectSaveAction.Arg arg) {
        String regMobile = (String) arg.getObjectData().get(AccountSignCertifyObjConstants.Field.RegMobile.apiName);
        String legalPersonMobile = (String) arg.getObjectData().get(AccountSignCertifyObjConstants.Field.LegalPersonMobile.apiName);
        String userMobile = (String) arg.getObjectData().get(AccountSignCertifyObjConstants.Field.UserMobile.apiName);
        if (!MobileUtil.isMobile(regMobile)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册手机号:请输入正确的手机号");
        }
        if (legalPersonMobile !=null && !MobileUtil.isMobile(legalPersonMobile)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "法人或经办人手机号:请输入正确的手机号");
        }
        if (userMobile !=null && !MobileUtil.isMobile(userMobile)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "手机号:请输入正确的手机号");
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
     * regMobile在tenantId中是否已经使用（不包含已作废的数据）
     */
    public boolean hasEnterpriseNameUsed(User user, String enterpriseName) {
        if (CollectionUtils.isEmpty(queryByEnterpriseName(user, enterpriseName))) {
            return false;
        }
        return true;
    }

    /**
     * regMobile在tenantId中是否已经使用（不包含已作废的数据）
     */
    public boolean hasUnifiedSocialCreditIdentifierUsed(User user, String unifiedSocialCreditIdentifier) {
        if (CollectionUtils.isEmpty(queryByUnifiedSocialCreditIdentifier(user, unifiedSocialCreditIdentifier))) {
            return false;
        }
        return true;
    }

    /**
     * 客户accountId是否有记录（不包含已作废的数据）
     */
    public boolean isAccountHasRecord(User user, String accountId) {
        if (queryByAccountId(user, accountId) == null) {
            return false;
        }
        return true;
    }

    /**
     * 客户accountId是否'已认证'
     */
    public boolean isAccountHasCertified(IObjectData accountSignCertifyObj) {
        String certifyStatus = (String) accountSignCertifyObj.get(AccountSignCertifyObjConstants.Field.CertifyStatus.apiName);
        if (Objects.equals(certifyStatus, CertifyStatusEnum.CRTTIFIED.getStatus())) {
            return true;
        }

        return false;
    }

    /**
     * 客户accountId是否'已启用'
     */
    public boolean isAccountHasEnable(IObjectData accountSignCertifyObj) {
        String certifyStatus = (String) accountSignCertifyObj.get(AccountSignCertifyObjConstants.Field.CertifyStatus.apiName);
        if (Objects.equals(certifyStatus, CertifyStatusEnum.CRTTIFIED.getStatus())) {
            return true;
        }

        return false;
    }

    /**
     *  获取bestSignAccount, 并检查是否'已认证' '已启用'
     */
    public String getBestSignAccountAndCheckStatus(User user, String accountId) {
        IObjectData accountSignCertifyObj = queryByAccountId(user, accountId);
        if (accountSignCertifyObj == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.NO_ACCOUNT_SIGN_CERTIFY_OBJ_DATA);
        }
        if (!isAccountHasCertified(accountSignCertifyObj)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.ACCOUNT_NO_CERTIFIED);
        }
        if (!isAccountHasEnable(accountSignCertifyObj)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.ACCOUNT_IS_NOT_ENABLE);
        }
        return (String) accountSignCertifyObj.get(AccountSignCertifyObjConstants.Field.BestSignAccount.apiName);
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

        //生成签章
        String recordType = (String) objectData.get(SystemConstants.Field.RecordType.apiName);
        if (Objects.equals(recordType, AccountSignCertifyObjConstants.RecordType.EnterpriseRecordType.apiName)) {
            String enterpriseName = (String) objectData.get(AccountSignCertifyObjConstants.Field.EnterpriseName.apiName);
            bestSignSignatureImageManager.createAndUploadSignatureImage(user, bestSignAccount, enterpriseName);
        } else {
            bestSignSignatureImageManager.create(user, bestSignAccount);
        }
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
                p -> p.get(AccountSignCertifyObjConstants.Field.BestSignAccount.apiName, String.class),
                p -> p.get(AccountSignCertifyObjConstants.Field.Id.apiName, String.class)
        ));
    }

    public List<IObjectData> queryByRegMobile(User user, String regMobile) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, AccountSignCertifyObjConstants.Field.RegMobile.apiName, regMobile);
        return searchQuery(user, AccountSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public List<IObjectData> queryByEnterpriseName(User user, String enterpriseName) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, AccountSignCertifyObjConstants.Field.EnterpriseName.apiName, enterpriseName);
        return searchQuery(user, AccountSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public List<IObjectData> queryByUnifiedSocialCreditIdentifier(User user, String unifiedSocialCreditIdentifier) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName, unifiedSocialCreditIdentifier);
        return searchQuery(user, AccountSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public IObjectData queryByAccountId(User user, String accountId) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, AccountSignCertifyObjConstants.Field.AccountId.apiName, accountId);
        List<IObjectData> objectDatas = searchQuery(user, AccountSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
        if (CollectionUtils.isEmpty(objectDatas)) {
            return null;
        }
        return objectDatas.get(0);
    }

    public List<IObjectData> queryByBestSignAccounts(User user, List<String> bestSignAccounts) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, AccountSignCertifyObjConstants.Field.BestSignAccount.apiName, bestSignAccounts);
        return searchQuery(user, AccountSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    public IObjectData queryById(User user, String id) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, AccountSignCertifyObjConstants.Field.Id.apiName, id);
        List<IObjectData> objectDatas = searchQuery(user, AccountSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
        if (CollectionUtils.isEmpty(objectDatas)) {
            return null;
        }
        return objectDatas.get(0);
    }

    public IObjectData queryByBestSignAccount(User user, String bestSignAccount) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, AccountSignCertifyObjConstants.Field.BestSignAccount.apiName, bestSignAccount);
        List<IObjectData> objectDatas = searchQuery(user, AccountSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
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
            objectData.set(AccountSignCertifyObjConstants.Field.CertifyStatus.apiName, certifyStatus);
        }
        objectData.set(AccountSignCertifyObjConstants.Field.CertifyErrMsg.apiName, certifyErrMsg);
        if (useStatus != null) {
            objectData.set(AccountSignCertifyObjConstants.Field.UseStatus.apiName, useStatus);
        }
        IObjectData newObjectData = metaDataActionService.updateObjectData(user, objectData, allowUpdateInvalid);
        log.debug("metaDataActionService.updateObjectData, user:{}, data:{}, allowUpdateInvalid:{}, result:{}", user, objectData, allowUpdateInvalid, newObjectData);
        return newObjectData;
    }


    /**
     * 更新启用停用中状态
     * @param allowUpdateInvalid 是否可以更新作废数据
     */
    public IObjectData updateUseStatus(User user, IObjectData objectData, String useStatus, boolean allowUpdateInvalid) {
        if (useStatus != null) {
            objectData.set(AccountSignCertifyObjConstants.Field.UseStatus.apiName, useStatus);
        }
        IObjectData newObjectData = metaDataActionService.updateObjectData(user, objectData, allowUpdateInvalid);
        log.debug("metaDataActionService.updateObjectData, user:{}, data:{}, allowUpdateInvalid:{}, result:{}", user, objectData, allowUpdateInvalid, newObjectData);
        return newObjectData;
    }

    public IObjectData getObjectDataById(User user, String id) {
        return this.serviceFacade.findObjectDataIncludeDeleted(user, id, AccountSignCertifyObjConstants.API_NAME);
    }
}