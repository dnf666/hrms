package com.facishare.crm.electronicsign.predefine.manager;

import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.electronicsign.enums.AppElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.TenantElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.status.SingleSignStatusEnum;
import com.facishare.crm.electronicsign.enums.status.TotalSignStatusEnum;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SignTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.dao.SignRequestDAO;
import com.facishare.crm.electronicsign.predefine.manager.bestsign.BestSignContractManager;
import com.facishare.crm.electronicsign.predefine.manager.bestsign.BestSignFileManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.AccountSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.SignRecordObjManager;
import com.facishare.crm.electronicsign.predefine.model.*;
import com.facishare.crm.electronicsign.predefine.model.vo.SignerHasSignStatusVO;
import com.facishare.crm.electronicsign.predefine.model.vo.SimpleSignerVO;
import com.facishare.crm.electronicsign.predefine.mq.ElecSignMQSender;
import com.facishare.crm.electronicsign.predefine.mq.SignCompleteMessageData;
import com.facishare.crm.electronicsign.predefine.service.dto.SignRequestType;
import com.facishare.crm.rest.dto.RenderPdfModel;
import com.facishare.fsi.proxy.model.warehouse.n.fileupload.NUploadFileDirect;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class SignRequestManager {
    @Resource
    private TemplatePdfManager templatePdfManager;
    @Resource
    private SignRequestDAO signRequestDAO;
    @Autowired
    private ElecSignFileManager elecSignFileManager;
    @Resource
    private BestSignContractManager bestSignContractManager;
    @Resource
    private BestSignFileManager bestSignFileManager;
    @Resource
    private SignSettingManager signSettingManager;
    @Resource
    private ElecSignConfigManager elecSignConfigManager;
    @Resource
    private TenantQuotaManager tenantQuotaManager;
    @Resource
    private ServiceFacade serviceFacade;
    @Resource
    private SignRecordObjManager signRecordObjManager;
    @Resource
    private SignerManager signerManager;
    @Resource
    private SignPositionManager signPositionManager;
    @Resource
    private ElecSignMQSender elecSignMQSender;
    @Resource
    private AccountSignCertifyObjManager accountSignCertifyObjManager;

    public SignRequestType.IsHasSignPermission.Result isHasSignPermission(User user, SignRequestType.IsHasSignPermission.Arg arg) {
        SignRequestType.IsHasSignPermission.Result result = new SignRequestType.IsHasSignPermission.Result();

        // 1.平台开关
        TenantElecSignSwitchEnum tenantElecSignSwitchEnum = elecSignConfigManager.getTenantElecSignStatus(user.getTenantId());
        if (Objects.equals(tenantElecSignSwitchEnum.getStatus(), TenantElecSignSwitchEnum.OFF.getStatus())) {
            result.setStatus(ElecSignErrorCode.TENANT_ELEC_SIGN_OFF.getCode());
            result.setMessage("企业电子签章开关未开启");
            return result;
        }

        // 2.应用级开关
        AppElecSignSwitchEnum appElecSignStatus = elecSignConfigManager.getAppElecSignStatus(user.getTenantId(), AppTypeEnum.get(arg.getAppType()).get());
        if (Objects.equals(appElecSignStatus.getStatus(), AppElecSignSwitchEnum.OFF.getStatus())) {
            result.setStatus(ElecSignErrorCode.APP_ELEC_SIGN_OFF.getCode());
            result.setMessage(ElecSignErrorCode.APP_ELEC_SIGN_OFF.getMessage());
        }

        // 3.这个客户是否有认证账号+启用
        try {
            accountSignCertifyObjManager.getBestSignAccountAndCheckStatus(user, arg.getAccountId());
            return result;
        } catch (ElecSignBusinessException e) {
            result.setStatus(e.getErrorCode());
            result.setMessage(e.getMessage());
            return result;
        }
    }

    /**
     * 获取签署URL
     */
    public SignRequestType.GetSignUrl.Result getSignUrlOrAutoSign(User user, SignRequestType.GetSignUrl.Arg arg) {
        SignRequestType.GetSignUrl.Result result = new SignRequestType.GetSignUrl.Result();

        SignRequestType.GetSignUrl.SignerArg signer = arg.getSigner();

        //1 查看签章请求信息
        SignRequestDO signRequestDO = this.getSignRequestDO(user.getTenantId(), arg.getAppType(), arg.getObjDataId());

        //2 参数检查
        if (signRequestDO == null) {
            checkArgForFirstTime(arg);
        }

        //3 是否有签署权限
        checkSignPermission(user, arg.getAppType(), arg.getObjApiName(), arg.getObjDataId());

        //4 第1次，上传打印模板，创建合同
        String newContractId = null;
        String validSignerBestSignAccount;
        SignerDO signerDO;
        boolean hasReCreateContract = false;
        List<SignRequestType.GetSignUrl.SignerArg> hasSignOldContractSigners = null;
        if (signRequestDO == null || signRequestDO.getContractId() == null) {
            //剩余配额是否足够
            tenantQuotaManager.checkQuota(user);

            //获取签署者账号
            validSignerBestSignAccount = signerManager.getBestSignAccountAndCheckStatus(user, signer);

            //上传打印模板，创建合同
            signRequestDO = uploadTemplateAndCreateContract(user, arg, validSignerBestSignAccount);
            newContractId = signRequestDO.getContractId();
            signerDO = signRequestDO.getSigners().get(signer.getOrderNum() - 1);
        }
        //5 非第1次
        else {
            //是否保持了签署账号
            String hasSaveBestSignAccount = getHasSaveBestSignAccount(signRequestDO.getSigners(), arg.getSigner());

            //是否已经签了
            bestSignContractManager.checkBestSignAccountHasSigned(user, signRequestDO.getContractId(), hasSaveBestSignAccount);

            //剩余配额是否足够
            tenantQuotaManager.checkQuota(user);

            // TODO: 2018/5/22 chenzs test
            //还没签署，判断hasSaveBestSignAccount是不是最新的，不是则更新
            validSignerBestSignAccount = signerManager.getBestSignAccountAndCheckStatus(user, signer);
            if (!Objects.equals(hasSaveBestSignAccount, validSignerBestSignAccount) && hasSaveBestSignAccount != null) {
                for (SignerDO tempSignerDO : signRequestDO.getSigners()) {
                    if (Objects.equals(tempSignerDO.getOrderNum(), signer.getOrderNum())) {
                        tempSignerDO.setBestSignAccount(validSignerBestSignAccount);
                        break;
                    }
                }
                signerManager.updateSigners(signRequestDO.getId(), signRequestDO.getSigners());
            }

            //是否过期
            newContractId = signRequestDO.getContractId();
            Long currentTimeSeconds = System.currentTimeMillis() / 1000;
            if (currentTimeSeconds > signRequestDO.getContactExpireTime()) {
                if (arg.getIsReCreateContractIfExpired()) {
                    String oldContractId = signRequestDO.getContractId();
                    newContractId = recreateContract(user, signRequestDO, arg);
                    hasReCreateContract = true;

                    //哪些已经签过老合同的
                    List<SignerDO> signerDOS = signRequestDO.getSigners();
                    hasSignOldContractSigners = signerManager.getHasSignOldContractSigners(user, oldContractId, signerDOS);
                } else {
                    throw new ElecSignBusinessException(ElecSignErrorCode.CONTRACT_HAS_EXPIRE);
                }
            }

            //保存签署者
            signerDO = signerManager.addNewSignerIfNotExist(user, signRequestDO.getId(), signRequestDO.getSigners(), arg.getSigner(), validSignerBestSignAccount);
        }

        //6 获取签署位置
        SignPositionDO signPositionDO = signPositionManager.getSignPositionDO(signRequestDO.getSigners(), signer);

        //7 获取签署URL or 自动签
        Integer signType = signerDO.getSignType();
        if (Objects.equals(signType, SignTypeEnum.By_Hand.getType())) {
            String signUrl = bestSignContractManager.sendContract(user, newContractId, validSignerBestSignAccount, signPositionDO, arg.getIsAllowChangeSignaturePosition(), arg.getUrlExpireTime(), arg.getSignedReturnUrl(), arg.getVcodeMobile());
            result.setSignUrl(signUrl);
        } else if (Objects.equals(signType, SignTypeEnum.Auto.getType())) {
            bestSignContractManager.autoSign(user, newContractId, signerDO);
            //自动签实时返回结果，没有异步通知的
            singleSignerHasSigned(newContractId, validSignerBestSignAccount, true);
        }
        
        result.setSignType(signType);
        result.setHasRecreateContract(hasReCreateContract);
        result.setHasSignOldContractSigners(hasSignOldContractSigners);
        return result;
    }

    /**
     * 有些数据第一次需要传
     */
    private void checkArgForFirstTime(SignRequestType.GetSignUrl.Arg arg) {
        if (arg.getUpCreateContractUserId() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， 第一次获取签字URL，upCreateContractUserId 不能为空");
        }
        if (arg.getUpUploadContractUserId() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， 第一次获取签字URL，upUploadContractUserId 不能为空");
        }
        if (arg.getContractFileAttachmentName() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， 第一次获取签字URL，contractFileAttachmentName不能为空");
        }
        if (!arg.getContractFileAttachmentName().endsWith(".pdf")) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， contractFileAttachmentName必须以.pdf结尾");
        }
        if (arg.getTitle() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， 第一次获取签字URL，title不能为空");
        }
        if (arg.getContractExpireTime() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， 第一次获取签字URL，contractExpireTime不能为空");
        }
    }

    /**
     * 是否能签署
     */
    public void checkSignPermission(User user, String appType, String objApiName, String objDataId) {
        //1 该条数据是否已签过
        signRecordObjManager.checkHasSigned(user, appType, objApiName, objDataId);

        //2 查看租户级开关
        TenantElecSignSwitchEnum tenantElecSignSwitchEnum = elecSignConfigManager.getTenantElecSignStatus(user.getTenantId());
        if (Objects.equals(tenantElecSignSwitchEnum.getStatus(), TenantElecSignSwitchEnum.OFF.getStatus())) {
            throw new ElecSignBusinessException(ElecSignErrorCode.TENANT_ELEC_SIGN_OFF, "企业电子签章开关未开启");
        }

        //3 查看应用级开关
        AppElecSignSwitchEnum appElecSignStatus = elecSignConfigManager.getAppElecSignStatus(user.getTenantId(), AppTypeEnum.get(appType).get());
        if (Objects.equals(appElecSignStatus.getStatus(), AppElecSignSwitchEnum.OFF.getStatus())) {
            throw new ElecSignBusinessException(ElecSignErrorCode.APP_ELEC_SIGN_OFF);
        }
    }

    /**
     * 上传打印模板PDF，创建合同并保存到本地
     */
    private SignRequestDO uploadTemplateAndCreateContract(User user, SignRequestType.GetSignUrl.Arg arg, String signerBestSignAccount) {
        //1 获取模板path
        RenderPdfModel.Result pdfResult = templatePdfManager.getTemplatePdfTnPath(user, arg.getObjApiName(), arg.getObjDataId(), "Portrait"); // Landscape:横向  Portrait：纵向
        if (pdfResult.getCode() != 0) {
            throw new ElecSignBusinessException(ElecSignErrorCode.GET_OBJ_TEMPLATE_PATH_FAILED, ElecSignErrorCode.GET_OBJ_TEMPLATE_PATH_FAILED.getMessage() + pdfResult.getMsg());
        }
        String pdfTnPath = pdfResult.getResult();

        //2 下载模板PDF
        String ea = serviceFacade.getEAByEI(user.getTenantId());
        byte[] pdf = elecSignFileManager.download(ea, user.getUserId(), pdfTnPath);

        //3 获取关键字
        SignSettingDO signSettingDO = signSettingManager.getSignSettingDO(user.getTenantId(), arg.getAppType(), arg.getObjApiName());

        //4 根据关键字获取签署位置
        List<SignerDO> signerDOS = signerManager.buildSigners(user, signerBestSignAccount, pdf, signSettingDO, arg.getSigner());

        //5 创建合同的bestSignAccount
        String contractCreatorBestSignAccount = signerManager.getContractCreatorBestSignAccount(user.getTenantId(), arg.getUpCreateContractUserId());

        //6 获取pdf文件的页数
        int fpage = elecSignFileManager.getTotalPageNum(user, pdf);

        //7 上传模板PDF到上上签
        String templateBestSignFileId = bestSignFileManager.eaUploadPdf(user, contractCreatorBestSignAccount, pdf, fpage);

        //8 创建合同
        String contractId = bestSignContractManager.createContract(user, contractCreatorBestSignAccount, templateBestSignFileId, arg.getContractExpireTime().toString(), arg.getTitle(), null);

        //9 保存
        SignRequestDO signRequestDO = getSignRequestDO(user.getTenantId(), arg, signerDOS, templateBestSignFileId, contractId);
        signRequestDAO.save(signRequestDO);

        return signRequestDO;
    }

    /**
     * 老的合同过期，重新生成新的合同
     */
    private String recreateContract(User user, SignRequestDO signRequestDO, SignRequestType.GetSignUrl.Arg arg) {
        // 创建合同的bestSignAccount
        String contractCreatorBestSignAccount = signerManager.getContractCreatorBestSignAccount(user.getTenantId(), signRequestDO.getUpCreateContractUserId());

        // 创建合同
        String contractId= bestSignContractManager.createContract(user, contractCreatorBestSignAccount, signRequestDO.getTemplateBestSignFileId(), arg.getContractExpireTime().toString(), arg.getTitle(), null);

        // 更新contractId  contractExpireTime
        SignRequestDO updateSignRequestArg = new SignRequestDO();
        updateSignRequestArg.setContractId(contractId);
        updateSignRequestArg.setContactExpireTime(arg.getContractExpireTime());
        signRequestDAO.updateById(signRequestDO.getId(), updateSignRequestArg);

        return contractId;
    }

    private SignRequestDO getSignRequestDO(String tenantId, SignRequestType.GetSignUrl.Arg arg, List<SignerDO> signerDOS, String templateBestSignFileId, String contractId) {
        SignRequestDO signRequestDO = new SignRequestDO();
        signRequestDO.setTenantId(tenantId);
        signRequestDO.setUpCreateContractUserId(arg.getUpCreateContractUserId());
        signRequestDO.setUpUploadContractUserId(arg.getUpUploadContractUserId());
        signRequestDO.setAppType(arg.getAppType());
        signRequestDO.setObjApiName(arg.getObjApiName());
        signRequestDO.setObjDataId(arg.getObjDataId());
        signRequestDO.setContractFileAttachmentName(arg.getContractFileAttachmentName());
        signRequestDO.setSigners(signerDOS);
        signRequestDO.setTemplateBestSignFileId(templateBestSignFileId);
        signRequestDO.setContractId(contractId);
        signRequestDO.setContactExpireTime(arg.getContractExpireTime());
        signRequestDO.setCreateTime(System.currentTimeMillis());
        signRequestDO.setUpdateTime(System.currentTimeMillis());
        return signRequestDO;
    }

    /**
     * 如果已经保存了bestSignAccount，则返回
     */
    private String getHasSaveBestSignAccount(List<SignerDO> signerDOs, SignRequestType.GetSignUrl.SignerArg signerArg) {
        if (CollectionUtils.isEmpty(signerDOs)) {
            return null;
        }

        for (SignerDO signerDO : signerDOs) {
            if (Objects.equals(signerDO.getOrderNum(), signerArg.getOrderNum())) {
                return signerDO.getBestSignAccount();
            }
        }
        return null;
    }
    
    /**
     * 签署结果处理
     */
    public void signResultCallBack(User proxyUser, SignRequestType.SignResultCallBack.Arg arg) {
        log.info("signResultCallBack, proxyUser[{}], arg[{}]", proxyUser, arg);
        //签署不成功
        if (!Objects.equals(arg.getSignerStatus(), "2")) {
            log.warn("signResultCallBack, proxyUser[{}], arg[{}]", proxyUser, arg);
        }

        singleSignerHasSigned(arg.getContractId(), arg.getAccount(), false);
    }

    /**
     * 单个签署者都签署完的操作
     */
    private void singleSignerHasSigned(String contractId, String signerBestSignAccount, boolean isAutoSign) {
        SignRequestDO signRequestDO = signRequestDAO.queryByContractId(contractId);
        User realTenantUser = new User(signRequestDO.getTenantId(), User.SUPPER_ADMIN_USER_ID);

        //查看签署者的状态
        JSONObject signerStatusJsonObject = bestSignContractManager.getSignerStatus(realTenantUser, contractId);

        //查询所有的签署者
        List<SignerDO> signerDOs = signRequestDO.getSigners();
        if (signerDOs == null) {
            log.warn("find no signers, contractId[{}]", contractId);
            return;
        }

        //判断所有签署者是否签署完
        boolean allSignerHasSigned = true;
        for (SignerDO signerDO : signerDOs) {
            if (!Objects.equals(signerStatusJsonObject.get(signerDO.getBestSignAccount()), SingleSignStatusEnum.SIGNED.getStatus())) {
                allSignerHasSigned = false;
                break;
            }
        }

        //都签完了
        if (allSignerHasSigned) {
            allSignerHasSigned(realTenantUser, contractId, signRequestDO);
        }

        //签完了，发MQ
        if (!isAutoSign) {
            SimpleSignerVO simpleSignerVO = getSimpleSignerVO(signRequestDO, signerBestSignAccount);
            sendSignCompleteMsg(realTenantUser, signRequestDO, allSignerHasSigned, simpleSignerVO);
            log.info("sendSignCompleteMsg, proxyUser[{}], signRequestDO[{}], allSignerHasSigned[{}], simpleSignerVO[{}]", realTenantUser, signRequestDO, allSignerHasSigned, simpleSignerVO);
        }
    }

    /**
     * 所有签署者都签署完的操作
     */
    private void allSignerHasSigned(User user, String contractId, SignRequestDO signRequestDO){
        //结束合同
        bestSignContractManager.lockAndFinishContract(user, contractId);

        //下载合同
        byte[] contract = null;
        try {
            contract = bestSignContractManager.downloadContract(user, contractId);
        } catch (Exception e) {
            log.error("bestSignContractManager.downloadContract failed, user[{}], contractId[{}]", user, contractId, e);
        }

        //上传合同到文件系统
        NUploadFileDirect.Result uploadResult = null;
        String ea = serviceFacade.getEAByEI(user.getTenantId());
        try {
            if (contract != null) {
                uploadResult = elecSignFileManager.nUploadFile(ea, signRequestDO.getUpUploadContractUserId(), contract, "pdf");
            }
        } catch (Exception e) {
            log.error("elecSignFileManager.nUploadFile failed, ea[{}], userId[{}], data[{}], fileExt[{}]", ea, signRequestDO.getUpUploadContractUserId(), contract, "pdf", e);
        }

        //都签完了，增加签署记录
        String quotaType = signRecordObjManager.getQuotaType(user, signRequestDO.getSigners());
        try {
            signRecordObjManager.saveSignRecord(user, signRequestDO, contractId, uploadResult, quotaType);
        } catch (Exception e) {
            log.error("signRecordObjManager.saveSignRecord, user[{}], signRequestDO[{}], contractId[{}], uploadResult[{}], quotaType[{}]", user, signRequestDO, contractId, uploadResult, quotaType, e);
        } finally {
            //增加使用配额
            tenantQuotaManager.addUsedQuota(user, quotaType);
        }
    }

    /**
     * 签署完，发MQ
     */
    private void sendSignCompleteMsg(User proxyUser, SignRequestDO signRequestDO, boolean allSignerHasSigned, SimpleSignerVO simpleSignerVO) {
        SignCompleteMessageData msg = new SignCompleteMessageData();
        msg.setTenantId(proxyUser.getTenantId());
        msg.setAppType(signRequestDO.getAppType());
        msg.setObjApiName(signRequestDO.getObjApiName());
        msg.setObjDataId(signRequestDO.getObjDataId());
        msg.setTotalSignStatus(allSignerHasSigned ? TotalSignStatusEnum.ALL_SIGNED.getStatus() : TotalSignStatusEnum.PART_SIGNED.getStatus());
        msg.setSignStatus(SingleSignStatusEnum.SIGNED.getStatus());
        msg.setSigner(simpleSignerVO);
        elecSignMQSender.sendSignCompleteMsg(msg);
    }

    /**
     * 获取签署者的SimpleSignerVO
     */
    private SimpleSignerVO getSimpleSignerVO(SignRequestDO signRequestDO, String signBestSignAccount) {
        SimpleSignerVO simpleSignerVO = new SimpleSignerVO();
        for (SignerDO signer : signRequestDO.getSigners()) {
            if (Objects.equals(signer.getBestSignAccount(), signBestSignAccount)) {
                simpleSignerVO.setSignerType(signer.getSignerType());
                simpleSignerVO.setAccountId(signer.getAccountId());
                simpleSignerVO.setUpDepartmentId(signer.getUpDepartmentId());
                return simpleSignerVO;
            }
        }
        log.warn("no signer found for signBestSignAccount[{}]", signBestSignAccount);
        throw new ElecSignBusinessException(ElecSignErrorCode.NO_SIGNER_FOUND_FOR_SIGNER_BEST_SIGN_ACCOUNT);
    }

    public SignRequestType.GetSignStatus.Result getSignStatus(User user, SignRequestType.GetSignStatus.Arg arg) {
        SignRequestType.GetSignStatus.Result result = new SignRequestType.GetSignStatus.Result();

        SignRequestDO signRequestDO = getSignRequestDO(user.getTenantId(), arg.getAppType(), arg.getObjDataId());
        //1 未创建合同
        if (signRequestDO == null) {
            result.setTotalSignStatus(TotalSignStatusEnum.NO_CREATE_CONTRACT.getStatus());
            return result;
        }

        //2 都已签署
        String origin = signRecordObjManager.getOrigin(user.getTenantId(), arg.getObjApiName());
        IObjectData objectData = signRecordObjManager.query(user, arg.getAppType(), origin, arg.getObjDataId());
        if (objectData != null) {
            result.setTotalSignStatus(TotalSignStatusEnum.ALL_SIGNED.getStatus());
            return result;
        }

        // 3 都未签署
        //查询签署者状态
        JSONObject signerStatusJsonObject = bestSignContractManager.getSignerStatus(user, signRequestDO.getContractId());
        log.info("getSignStatus, signerStatusJsonObject[{}]", signerStatusJsonObject);
        if (Objects.equals(signerStatusJsonObject.toJSONString(), "{}")) {
            result.setTotalSignStatus(TotalSignStatusEnum.UN_SIGN.getStatus());
            return result;
        }

        // 4 部分签署
        result.setTotalSignStatus(TotalSignStatusEnum.PART_SIGNED.getStatus());
        List<SignerHasSignStatusVO> signerHasSignStatusVOS = new ArrayList<>();
        for (SignerDO signerDO : signRequestDO.getSigners()) {
            SignerHasSignStatusVO signerHasSignStatusVO = new SignerHasSignStatusVO();
            signerHasSignStatusVO.setSignerType(signerDO.getSignerType());
            signerHasSignStatusVO.setAccountId(signerDO.getAccountId());
            signerHasSignStatusVO.setUpDepartmentId(signerDO.getUpDepartmentId());
            signerHasSignStatusVO.setOrderNum(signerDO.getOrderNum());

            if (!Objects.equals(signerStatusJsonObject.get(signerDO.getBestSignAccount()), SingleSignStatusEnum.SIGNED.getStatus())) {
                signerHasSignStatusVO.setSignStatus(SingleSignStatusEnum.UN_SIGN.getStatus());
            } else {
                signerHasSignStatusVO.setSignStatus(SingleSignStatusEnum.SIGNED.getStatus());
            }

            signerHasSignStatusVOS.add(signerHasSignStatusVO);
        }
        result.setSignerStatus(signerHasSignStatusVOS);

        return result;
    }

    /**
     * 获取 SignRequestDO
     */
    public SignRequestDO getSignRequestDO(String tenantId, String appType, String objDataId) {
        SignRequestDO signRequestDOArg = new SignRequestDO();
        signRequestDOArg.setTenantId(tenantId);
        signRequestDOArg.setAppType(appType);
        signRequestDOArg.setObjDataId(objDataId);
        return querySignRequestDO(signRequestDOArg);
    }

    private SignRequestDO querySignRequestDO(SignRequestDO signRequestDOArg) {
        List<SignRequestDO> signRequestDOS = signRequestDAO.queryList(signRequestDOArg);
        if (CollectionUtils.isEmpty(signRequestDOS)) {
             return null;
        }

        return signRequestDOS.get(0);
    }
}
