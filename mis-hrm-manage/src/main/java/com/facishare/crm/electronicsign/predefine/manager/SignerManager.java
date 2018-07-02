package com.facishare.crm.electronicsign.predefine.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.electronicsign.enums.status.SingleSignStatusEnum;
import com.facishare.crm.electronicsign.enums.type.SignerTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.dao.SignRequestDAO;
import com.facishare.crm.electronicsign.predefine.manager.bestsign.BestSignContractManager;
import com.facishare.crm.electronicsign.predefine.manager.bestsign.BestSignFileManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.AccountSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.model.*;
import com.facishare.crm.electronicsign.predefine.service.dto.SignRequestType;
import com.facishare.crm.util.GsonUtil;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class SignerManager {
    @Resource
    private SignRequestDAO signRequestDAO;
    @Resource
    private AccountSignCertifyObjManager accountSignCertifyObjManager;
    @Resource
    private BestSignContractManager bestSignContractManager;
    @Resource
    private BestSignFileManager bestSignFileManager;
    @Resource
    private InternalSignCertifyUseRangeManager internalSignCertifyUseRangeManager;

    /**
     * 设置签署位置
     * 还没有传具体签署人的把签署位置保存下来，后面就不用再判断关键字的位置
     */
    public List<SignerDO> buildSigners(User user, String signerBestSignAccount, byte[] pdf, SignSettingDO signSettingDO, SignRequestType.GetSignUrl.SignerArg signer) {
        List<SignerDO> result = new ArrayList<>();

        String fdata = Base64.encodeBase64String(pdf);
        List<SignerSettingDO> signerSettingDOS = signSettingDO.getSignerSettings();

        for(int i=0; i<signerSettingDOS.size(); i++) {
            //1 获取关键字位置
            SignerSettingDO signerSettingDO = signerSettingDOS.get(i);
            String keyword = signerSettingDO.getKeyword();
            JSONArray positions = bestSignFileManager.findKeywordPositions(user, keyword, fdata);

            //2 添加一个SignerDO
            SignerDO signerDO = new SignerDO();
            if (Objects.equals(i, (signer.getOrderNum()-1))) {
                signerDO.setSignerType(signer.getSignerType());
                signerDO.setAccountId(signer.getAccountId());
                signerDO.setUpDepartmentId(signer.getUpDepartmentId());
                signerDO.setOrderNum(signer.getOrderNum());
                signerDO.setBestSignAccount(signerBestSignAccount);
            }
            signerDO.setSignType(signerSettingDO.getSignType());

            //关键字多处出现，取最后一个位置
            String lastPositionStr = positions.get(positions.size()-1).toString();
            SignPositionDO signPositionDO = GsonUtil.json2object(lastPositionStr, SignPositionDO.class);
            signerDO.setSignPosition(signPositionDO);

            result.add(signerDO);
        }

        return result;
    }

    /**
     * 老合同过期，获取已经签过老合同的人员信息
     */
    public List<SignRequestType.GetSignUrl.SignerArg> getHasSignOldContractSigners(User user, String oldContractId, List<SignerDO> signerDOS) {
        List<SignRequestType.GetSignUrl.SignerArg> hasSignOldContractSigners = new ArrayList<>();

        //签署状态
        JSONObject signerStatusJsonObjects = bestSignContractManager.getSignerStatus(user, oldContractId);
        for (SignerDO signerDO : signerDOS) {
            if (signerStatusJsonObjects.get(signerDO.getBestSignAccount()) != null) {
                String signStatus = (String) signerStatusJsonObjects.get(signerDO.getBestSignAccount());
                if (Objects.equals(signStatus, SingleSignStatusEnum.SIGNED.getStatus())) {
                    SignRequestType.GetSignUrl.SignerArg signerArg = new SignRequestType.GetSignUrl.SignerArg();
                    signerArg.setSignerType(signerDO.getSignerType());
                    signerArg.setAccountId(signerDO.getAccountId());
                    signerArg.setUpDepartmentId(signerDO.getUpDepartmentId());
                    hasSignOldContractSigners.add(signerArg);
                }
            }
        }

        return hasSignOldContractSigners;
    }

    /**
     * 添加新的签署者
     */
    public SignerDO addNewSignerIfNotExist(User user, String signRequestId, List<SignerDO> existSignerDOs, SignRequestType.GetSignUrl.SignerArg newSigner, String signerBestSignAccount) {
        //是否已保存过这个签署者的信息
        if (isHasNewSigner(existSignerDOs, newSigner)) {
            return existSignerDOs.get(newSigner.getOrderNum() - 1);
        }

        //没有则添加
        SignerDO newSignerDO = existSignerDOs.get(newSigner.getOrderNum() - 1);
        newSignerDO.setSignerType(newSigner.getSignerType());
        newSignerDO.setAccountId(newSigner.getAccountId());
        newSignerDO.setUpDepartmentId(newSigner.getUpDepartmentId());
        newSignerDO.setOrderNum(newSigner.getOrderNum());
        newSignerDO.setBestSignAccount(signerBestSignAccount);

        SignRequestDO newSignRequestDO = new SignRequestDO();
        newSignRequestDO.setSigners(existSignerDOs);
        signRequestDAO.updateById(signRequestId, newSignRequestDO);

        return newSignerDO;
    }

    public void updateSigners(String signRequestId, List<SignerDO> signerDOs) {
        SignRequestDO signRequestDO = new SignRequestDO();
        signRequestDO.setSigners(signerDOs);
        signRequestDAO.updateById(signRequestId, signRequestDO);
    }

    /**
     * signerDOs中是否有newSigner
     */
    private boolean isHasNewSigner(List<SignerDO> signerDOs, SignRequestType.GetSignUrl.SignerArg newSigner) {
        if (CollectionUtils.isEmpty(signerDOs)) {
            return false;
        }

        for (SignerDO signerDO : signerDOs) {
            if (equal(signerDO, newSigner)) {
                return true;
            }
        }
        return false;
    }

    /**
     * signerDO & newSigner 是否是同一个人
     */
    private boolean equal(SignerDO signerDO, SignRequestType.GetSignUrl.SignerArg newSigner) {
        if (Objects.equals(signerDO.getSignerType(), newSigner.getSignerType()) && Objects.equals(signerDO.getOrderNum(), newSigner.getOrderNum())) {
            if (Objects.equals(signerDO.getSignerType(), SignerTypeEnum.CRM_ACCOUNT.getType())) {
                if (Objects.equals(signerDO.getAccountId(), newSigner.getAccountId())) {
                    return true;
                }
            } else if (Objects.equals(signerDO.getSignerType(), SignerTypeEnum.TENANT.getType())) {
                if (Objects.equals(signerDO.getUpDepartmentId(), newSigner.getUpDepartmentId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取签署者的'已认证的'、'已启用'上上签账号
     */
    public String getBestSignAccountAndCheckStatus(User user, SignRequestType.GetSignUrl.SignerArg signerArg) {
        String signerBestSignAccount = null;
        if (Objects.equals(signerArg.getSignerType(), SignerTypeEnum.CRM_ACCOUNT.getType())) {
            signerBestSignAccount = accountSignCertifyObjManager.getBestSignAccountAndCheckStatus(user, signerArg.getAccountId());
        } else if (Objects.equals(signerArg.getSignerType(), SignerTypeEnum.TENANT.getType())) {
            Optional<String> bestSignAccountOpt = internalSignCertifyUseRangeManager.getBestSignAccountByDeptId(user.getTenantId(), signerArg.getUpDepartmentId());
            if (!bestSignAccountOpt.isPresent()) {
                throw new ElecSignBusinessException(ElecSignErrorCode.HAS_NO_CERTIFIED_AND_ENABLE_TENANT_CERTIFY_OBJ_RECORD);
            }
            signerBestSignAccount = bestSignAccountOpt.get();
        }
        return signerBestSignAccount;
    }

    /**
     * 创建合同的bestSignAccount
     */
    public String getContractCreatorBestSignAccount(String tenantId, Integer createContractUpTriggerUserId) {
        Optional<String> bestSignAccountOpt = internalSignCertifyUseRangeManager.getBestSignAccountByUserId(tenantId, String.valueOf(createContractUpTriggerUserId));
        if (!bestSignAccountOpt.isPresent()) {
            throw new ElecSignBusinessException(ElecSignErrorCode.HAS_NO_CERTIFIED_AND_ENABLE_TENANT_CERTIFY_OBJ_RECORD);
        }
        return bestSignAccountOpt.get();
    }
}
