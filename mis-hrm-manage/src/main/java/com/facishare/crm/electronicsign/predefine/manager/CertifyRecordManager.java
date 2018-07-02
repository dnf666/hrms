package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.UserObjEnum;
import com.facishare.crm.electronicsign.enums.status.BestSignCertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.type.CertifyTypeEnum;
import com.facishare.crm.electronicsign.enums.type.LegalPersonIdentityTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.dao.CertifyRecordDAO;
import com.facishare.crm.electronicsign.predefine.manager.obj.InternalSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.AccountSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.model.CertifyRecordDO;
import com.facishare.crm.electronicsign.predefine.model.vo.CertifyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.service.dto.CertifyRecordType;
import com.facishare.crm.electronicsign.util.CopyUtil;
import com.facishare.crm.util.BeanUtils;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 认证记录
 */
@Slf4j
@Service
public class CertifyRecordManager {
    @Resource
    private CertifyRecordDAO certifyRecordDAO;
    @Resource
    private InternalSignCertifyObjManager internalSignCertifyObjManager;
    @Resource
    private AccountSignCertifyObjManager accountSignCertifyObjManager;

    /**
     * 那帮人认证，添加认证记录
     */
    public void addForTenantCertify(User user, ObjectDataDocument tenantCertifyObj, String taskId) {
        String regMobile = (String) tenantCertifyObj.get(InternalSignCertifyObjConstants.Field.RegMobile.apiName);

        String enterpriseName = (String) tenantCertifyObj.get(InternalSignCertifyObjConstants.Field.EnterpriseName.apiName);
        String unifiedSocialCreditIdentifier = (String) tenantCertifyObj.get(InternalSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName);
        String legalPersonName = (String) tenantCertifyObj.get(InternalSignCertifyObjConstants.Field.LegalPersonName.apiName);
        String legalPersonIdentity = (String) tenantCertifyObj.get(InternalSignCertifyObjConstants.Field.LegalPersonIdentity.apiName);
        String legalPersonMobile = (String) tenantCertifyObj.get(InternalSignCertifyObjConstants.Field.LegalPersonMobile.apiName);

        String bestSignAccount = internalSignCertifyObjManager.generateBestSignAccount(user, regMobile);

        CertifyRecordDO certifyRecordDO = new CertifyRecordDO();
        certifyRecordDO.setTenantId(user.getTenantId());
        certifyRecordDO.setUserObj(UserObjEnum.TENANT.getType());
        certifyRecordDO.setCertifyType(CertifyTypeEnum.ENTERPRISE.getType());
        certifyRecordDO.setBestSignAccount(bestSignAccount);
        certifyRecordDO.setTaskId(taskId);

        certifyRecordDO.setEnterpriseName(enterpriseName);
        certifyRecordDO.setUnifiedSocialCreditIdentifier(unifiedSocialCreditIdentifier);
        certifyRecordDO.setLegalPersonName(legalPersonName);
        certifyRecordDO.setLegalPersonIdentity(legalPersonIdentity);
        certifyRecordDO.setLegalPersonIdentityType(LegalPersonIdentityTypeEnum.Identification_card.getType());
        certifyRecordDO.setLegalPersonMobile(legalPersonMobile);

        certifyRecordDO.setCertifyStatus(CertifyStatusEnum.CERTIFYING.getStatus());
        certifyRecordDO.setCreateTime(System.currentTimeMillis());
        certifyRecordDO.setUpdateTime(System.currentTimeMillis());

        certifyRecordDAO.save(certifyRecordDO);
    }

    /**
     * 客户认证，添加认证记录
     */
    public void addForAccountCertify(User user, ObjectDataDocument useCertifyObj, int certifyType, String taskId) {
        String regMobile = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.RegMobile.apiName);
        String recordType = (String) useCertifyObj.get(SystemConstants.Field.RecordType.apiName);
        String accountId = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.AccountId.apiName);

        String enterpriseName = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.EnterpriseName.apiName);
        String unifiedSocialCreditIdentifier = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName);
        String legalPersonName = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.LegalPersonName.apiName);
        String legalPersonIdentity = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.LegalPersonIdentity.apiName);
        String legalPersonMobile = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.LegalPersonMobile.apiName);

        String userName = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.UserName.apiName);
        String userIdentity = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.UserIdentity.apiName);
        String userMobile = (String) useCertifyObj.get(AccountSignCertifyObjConstants.Field.UserMobile.apiName);

        String bestSignAccount = accountSignCertifyObjManager.generateBestSignAccount(user, regMobile, recordType);

        CertifyRecordDO certifyRecordDO = new CertifyRecordDO();
        certifyRecordDO.setTenantId(user.getTenantId());
        certifyRecordDO.setUserObj(UserObjEnum.CRM_ACCOUNT.getType());
        certifyRecordDO.setCertifyType(certifyType);
        certifyRecordDO.setAccountId(accountId);
        certifyRecordDO.setBestSignAccount(bestSignAccount);
        certifyRecordDO.setTaskId(taskId);

        if (Objects.equals(certifyType, CertifyTypeEnum.ENTERPRISE.getType())) {
            certifyRecordDO.setEnterpriseName(enterpriseName);
            certifyRecordDO.setUnifiedSocialCreditIdentifier(unifiedSocialCreditIdentifier);
            certifyRecordDO.setLegalPersonName(legalPersonName);
            certifyRecordDO.setLegalPersonIdentity(legalPersonIdentity);
            certifyRecordDO.setLegalPersonIdentityType(LegalPersonIdentityTypeEnum.Identification_card.getType());
            certifyRecordDO.setLegalPersonMobile(legalPersonMobile);
        } else if (Objects.equals(certifyType, CertifyTypeEnum.INDIVIDUAL.getType())) {
            certifyRecordDO.setUserName(userName);
            certifyRecordDO.setUserIdentity(userIdentity);
            certifyRecordDO.setUserIdentityType(LegalPersonIdentityTypeEnum.Identification_card.getType());
            certifyRecordDO.setUserMobile(userMobile);
        }

        certifyRecordDO.setCertifyStatus(CertifyStatusEnum.CERTIFYING.getStatus());
        certifyRecordDO.setCreateTime(System.currentTimeMillis());
        certifyRecordDO.setUpdateTime(System.currentTimeMillis());

        certifyRecordDAO.save(certifyRecordDO);
    }

    /**
     * 认证回调
     */
    public void certCallBack(CertifyRecordType.CertCallBack.Arg arg) {
        log.info("certCallBack, arg[{}]", arg);
        CertifyRecordDO certifyRecordDO = certifyRecordDAO.queryByTaskId(arg.getTaskId());
        String certifyStatus = getCertifyStatus(arg.getStatus());
        User realTenantUser = new User(certifyRecordDO.getTenantId(), User.SUPPER_ADMIN_USER_ID);

        //修改预设对象
        if (Objects.equals(certifyRecordDO.getUserObj(), UserObjEnum.CRM_ACCOUNT.getType())) {
            accountSignCertifyObjManager.certifyCallback(realTenantUser, certifyRecordDO.getBestSignAccount(), certifyStatus, arg);
        } else if (Objects.equals(certifyRecordDO.getUserObj(), UserObjEnum.TENANT.getType())) {
            internalSignCertifyObjManager.certifyCallback(realTenantUser, certifyRecordDO.getBestSignAccount(), certifyStatus, arg);
        }

        //更新认证记录
        CertifyRecordDO newCertifyRecordDO = new CertifyRecordDO();
        newCertifyRecordDO.setCertifyStatus(certifyStatus);
        newCertifyRecordDO.setCertifyErrMsg(arg.getMessage());
        certifyRecordDAO.updateById(certifyRecordDO.getId(), newCertifyRecordDO);
    }

    /**
     * 上上签的bestSignCertifyStatus =>(换成） certifyStatus
     */
    private String getCertifyStatus(String bestSignCertifyStatus) {
        if (Objects.equals(bestSignCertifyStatus, BestSignCertifyStatusEnum.Apply_fail.getStatus())) {
            return CertifyStatusEnum.FAIL.getStatus();
        } else if (Objects.equals(bestSignCertifyStatus, BestSignCertifyStatusEnum.Time_out.getStatus())) {
            return CertifyStatusEnum.TIME_OUT.getStatus();
        } else if (Objects.equals(bestSignCertifyStatus, BestSignCertifyStatusEnum.Success.getStatus())) {
            return CertifyStatusEnum.CRTTIFIED.getStatus();
        }
        return null;
    }

    public Pager<CertifyRecordVO> getCertifyRecordByPage(CertifyRecordType.GetCertifyRecordByPage.Arg arg) {
        Integer certifyType = arg.getCertifyType();
        String certifyStatus = arg.getCertifyStatus();
        if (certifyType != null) {
            if (!CertifyTypeEnum.get(certifyType).isPresent()) {
                log.warn("CertifyRecordManager getCertifyRecordByPage 不支持认证类型 certifyType:[{}]", certifyType);
                throw new ElecSignBusinessException(ElecSignErrorCode.NO_SUPPORT_CERTIFY_TYPE);
            }
        }
        if (!Strings.isNullOrEmpty(certifyStatus)) {
            if (!CertifyStatusEnum.get(certifyStatus).isPresent()) {
                log.warn("CertifyRecordManager getCertifyRecordByPage 不支持认证状态 certifyStatus:[{}]", certifyStatus);
                throw new ElecSignBusinessException(ElecSignErrorCode.NO_SUPPORT_CERTIFY_STATUS);
            }
        }
        Pager<CertifyRecordVO> pager = new Pager<>();
        CertifyRecordDO certifyRecordDO = new CertifyRecordDO();
        certifyRecordDO.setCertifyType(certifyType);
        certifyRecordDO.setUserObj(arg.getUserObj());
        certifyRecordDO.setCertifyStatus(certifyStatus);

        pager.setCurrentPage(arg.getCurrentPage());
        pager.setPageSize(arg.getPageSize());
        Long startTime = arg.getStartTime();
        Long endTime = arg.getEndTime();
        int count = certifyRecordDAO.getCertifyRecordByConditionCount(certifyRecordDO, startTime, endTime);
        pager.setRecordSize(count);
        List<CertifyRecordDO> list = certifyRecordDAO.getCertifyRecordByConditionAndPage(certifyRecordDO, startTime, endTime, pager);
        List<CertifyRecordVO> voList = new ArrayList<>();
        list.forEach(e -> {
            CertifyRecordVO certifyRecordVO = BeanUtils.copyProperties(CertifyRecordVO.class, e);
            if (!Strings.isNullOrEmpty(e.getEnterpriseName())) {
                certifyRecordVO.setName(e.getEnterpriseName());
            } else {
                certifyRecordVO.setName(e.getUserName());
            }
            voList.add(certifyRecordVO);
        });
        pager.setData(voList);
        return pager;
    }
}
