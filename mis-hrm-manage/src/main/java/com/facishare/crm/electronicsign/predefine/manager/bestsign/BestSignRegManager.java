package com.facishare.crm.electronicsign.predefine.manager.bestsign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.type.UserTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.crm.electronicsign.util.BestSignHttpUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 上上签
 */
@Service
@Slf4j
public class BestSignRegManager {
    /**
     * 注册企业用户并申请证书（租户）
     */
    public String tenantReg(ObjectDataDocument arg) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/user/reg/";

        JSONObject credential= new JSONObject();
        credential.put("regCode", arg.get(InternalSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName));
        credential.put("orgCode", arg.get(InternalSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName));
        credential.put("taxCode", arg.get(InternalSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName));
        credential.put("legalPerson", arg.get(InternalSignCertifyObjConstants.Field.LegalPersonName.apiName));
        credential.put("legalPersonIdentity", arg.get(InternalSignCertifyObjConstants.Field.LegalPersonIdentity.apiName));
        credential.put("legalPersonIdentityType", "0");
        credential.put("legalPersonMobile", arg.get(InternalSignCertifyObjConstants.Field.LegalPersonMobile.apiName));
        credential.put("contactMobile", arg.get(InternalSignCertifyObjConstants.Field.LegalPersonMobile.apiName));

        JSONObject requestBody= new JSONObject();
        requestBody.put("account", arg.get(InternalSignCertifyObjConstants.Field.BestSignAccount.apiName));
        requestBody.put("name", arg.get(InternalSignCertifyObjConstants.Field.EnterpriseName.apiName));
        requestBody.put("userType", UserTypeEnum.ENTERPRISE.getType());
        requestBody.put("credential", credential);
        requestBody.put("applyCert", "1");

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("tenantReg failed arg[{}], requestBody[{}], sendResult[{}]", arg, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册企业并申请证书失败, " + sendResult.get("errmsg"));
                }
                log.info("tenantReg success arg[{}], requestBody[{}], sendResult[{}]", arg, requestBody, sendResult);
                JSONObject data = JSON.parseObject(sendResult.get("data").toString());
                return (String) data.get("taskId");
            } else {
                log.warn("tenantReg failed, result is null, arg[{}], requestBody[{}]", arg, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册企业并申请证书失败");
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("tenantReg failed arg[{}], requestBody[{}]", arg, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册企业并申请证书失败, " + e);
        }
    }

    /**
     * 注册企业用户并申请证书（客户）
     * 如果用同一个账号注册了个人账号，在注册企业账号，会报错：{"errno":240009,"cost":40,"errmsg":"13277977535account55988 not a enterprise user"}
     */
    public String eaAccountReg(ObjectDataDocument arg) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/user/reg/";

        JSONObject credential= new JSONObject();
        credential.put("regCode", arg.get(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName));
        credential.put("orgCode", arg.get(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName));
        credential.put("taxCode", arg.get(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName));
        credential.put("legalPerson", arg.get(AccountSignCertifyObjConstants.Field.LegalPersonName.apiName));
        credential.put("legalPersonIdentity", arg.get(AccountSignCertifyObjConstants.Field.LegalPersonIdentity.apiName));
        credential.put("legalPersonIdentityType", "0");
        credential.put("legalPersonMobile", arg.get(AccountSignCertifyObjConstants.Field.LegalPersonMobile.apiName));
        credential.put("contactMobile", arg.get(AccountSignCertifyObjConstants.Field.LegalPersonMobile.apiName));

        JSONObject requestBody= new JSONObject();
        requestBody.put("account", arg.get(AccountSignCertifyObjConstants.Field.BestSignAccount.apiName));
        requestBody.put("name", arg.get(AccountSignCertifyObjConstants.Field.EnterpriseName.apiName));
        requestBody.put("userType", UserTypeEnum.ENTERPRISE.getType());
        requestBody.put("credential", credential);
        requestBody.put("applyCert", "1");

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("eaAccountReg failed arg[{}], requestBody[{}], sendResult[{}]", arg, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册企业客户并申请证书失败, " + sendResult.get("errmsg"));
                }
                log.info("eaAccountReg success arg[{}], requestBody[{}], sendResult[{}]", arg, requestBody, sendResult);
                JSONObject data = JSON.parseObject(sendResult.get("data").toString());
                return (String) data.get("taskId");
            } else {
                log.warn("eaAccountReg failed, result is null, arg[{}], requestBody[{}]", arg, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册企业客户并申请证书失败");
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("eaAccountReg failed arg[{}], requestBody[{}]", arg, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册企业客户并申请证书失败, " + e);
        }
    }

    /**
     * 注册个人用户并申请证书（客户）
     */
    public String individualAccountReg(ObjectDataDocument arg) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/user/reg/";

        JSONObject credential= new JSONObject();
        credential.put("identity", arg.get(AccountSignCertifyObjConstants.Field.UserIdentity.apiName));

        JSONObject requestBody= new JSONObject();
        requestBody.put("account", arg.get(AccountSignCertifyObjConstants.Field.BestSignAccount.apiName));
        requestBody.put("name", arg.get(AccountSignCertifyObjConstants.Field.UserName.apiName));
        requestBody.put("userType", UserTypeEnum.INDIVIDUAL.getType());
        requestBody.put("mobile", arg.get(AccountSignCertifyObjConstants.Field.UserMobile.apiName));
        requestBody.put("credential", credential);
        requestBody.put("applyCert", "1");

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("individualAccountReg failed arg[{}], requestBody[{}], sendResult[{}]", arg, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册个人客户并申请证书失败, " + sendResult.get("errmsg"));
                }
                log.info("individualAccountReg success arg[{}], requestBody[{}], sendResult[{}]", arg, requestBody, sendResult);
                JSONObject data = JSON.parseObject(sendResult.get("data").toString());
                return (String) data.get("taskId");
            } else {
                log.warn("individualAccountReg failed, result is null, arg[{}], requestBody[{}]", arg, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册个人客户并申请证书失败");
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("individualAccountReg failed arg[{}], requestBody[{}]", arg, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册个人客户并申请证书失败, " + e);
        }
    }
}
