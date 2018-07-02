package com.facishare.crm.electronicsign.predefine.manager.bestsign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.electronicsign.enums.status.SingleSignStatusEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.model.SignPositionDO;
import com.facishare.crm.electronicsign.predefine.model.SignerDO;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.crm.electronicsign.util.BestSignHttpUtil;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * 上上签合同相关的
 */
@Service
@Slf4j
public class BestSignContractManager {

    /**
     * 创建合同
     *
     * @param user                          非必填
     * @param contractCreatorBestSignAccount  必填
     * @param fileId                          必填
     * @param expireTime                      必填
     * @param title                           必填
     * @param description                   非必填
     * @return
     * @throws IOException
     */
    public String createContract(User user, String contractCreatorBestSignAccount, String fileId, String expireTime, String title, String description) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/contract/create/";

        JSONObject requestBody = new JSONObject();
        requestBody.put("account", contractCreatorBestSignAccount);
        requestBody.put("fid", fileId);
        requestBody.put("expireTime", expireTime);
        requestBody.put("title", title);
        requestBody.put("description", description);

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("createContract failed user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.CREATE_CONTRACT_FAIL, ElecSignErrorCode.CREATE_CONTRACT_FAIL.getMessage() + sendResult.get("errmsg"));
                }
                log.info("createContract success user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);

                JSONObject dataJson = JSON.parseObject(sendResult.get("data").toString());
                return dataJson.get("contractId").toString();
            } else {
                log.warn("createContract failed, result is null, user[{}], requestBody[{}]", user, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.CREATE_CONTRACT_FAIL);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("createContract failed user[{}], requestBody[{}]", user, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.CREATE_CONTRACT_FAIL, ElecSignErrorCode.CREATE_CONTRACT_FAIL.getMessage() + e);
        }
    }

    /**
     * 发送合同，即获取合同签署的URL
     */
    public String sendContract(User user, String contractId, String signerBestSignAccount, SignPositionDO signPositionDO, String isAllowChangeSignaturePosition, String expireTime, String returnUrl, String vcodeMobile) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/contract/send/";

        JSONObject signaturePosition = new JSONObject();
        signaturePosition.put("x", signPositionDO.getX2());
        signaturePosition.put("y", String.valueOf((Float.valueOf(signPositionDO.getY1()) + Float.valueOf(signPositionDO.getY2())) / 2));  //最大长度10位，从上上签拿到的地址是小数点后4位，比如0.5651, 计算结果可能是小数点后5位
        signaturePosition.put("pageNum", signPositionDO.getPageNum());

        JSONObject requestBody = new JSONObject();
        requestBody.put("contractId", contractId);
        requestBody.put("signer", signerBestSignAccount);
        //dpi
        requestBody.put("signaturePositions", Lists.newArrayList(signaturePosition));
        requestBody.put("isAllowChangeSignaturePosition", isAllowChangeSignaturePosition);
        requestBody.put("expireTime", expireTime);
        requestBody.put("returnUrl", returnUrl);
        requestBody.put("vcodeMobile", vcodeMobile);
        requestBody.put("isDrawSignatureImage", "2"); //2强制必须手绘签名（只能手写不允许使用默认签名） 有其他选择 TODO: 2018/4/24 chenzs
        //sid
        //pushUrl
        //version
        requestBody.put("version", "3");
        //readAll

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("sendContract failed user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.SEND_CONTRACT_FAIL, ElecSignErrorCode.SEND_CONTRACT_FAIL.getMessage() + sendResult.get("errmsg"));
                }
                log.info("sendContract success user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);

                JSONObject dataJson = JSON.parseObject(sendResult.get("data").toString());
                return dataJson.get("url").toString();
            } else {
                log.warn("sendContract failed, result is null, user[{}], requestBody[{}]", user, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.SEND_CONTRACT_FAIL);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("sendContract failed user[{}], requestBody[{}]", user, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.SEND_CONTRACT_FAIL, ElecSignErrorCode.SEND_CONTRACT_FAIL.getMessage() + e);
        }
    }

    /**
     * 自动签
     */
    public void autoSign(User user, String contractId, SignerDO signerDO) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/storage/contract/sign/cert/";

        SignPositionDO signPositionDO = signerDO.getSignPosition();

        //和上上签确认过，其他的可以不传
        JSONObject signaturePosition = new JSONObject();
        signaturePosition.put("x", signPositionDO.getX2());
        signaturePosition.put("y", String.valueOf((Float.valueOf(signPositionDO.getY1()) + Float.valueOf(signPositionDO.getY2())) / 2));  //最大长度10位，从上上签拿到的地址是小数点后4位，比如0.5651
        signaturePosition.put("pageNum", signPositionDO.getPageNum());
        JSONArray signaturePositions = new JSONArray();
        signaturePositions.add(signaturePosition);

        JSONObject requestBody = new JSONObject();
        requestBody.put("contractId", contractId);
        requestBody.put("signer", signerDO.getBestSignAccount());
        requestBody.put("signaturePositions", signaturePositions);

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("autoSign failed user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.AUTO_SIGN_FAILED, ElecSignErrorCode.AUTO_SIGN_FAILED.getMessage() + sendResult.get("errmsg"));
                }
                log.info("autoSign success user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
            } else {
                log.warn("autoSign failed, result is null, user[{}], requestBody[{}]", user, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.AUTO_SIGN_FAILED);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("autoSign failed user[{}], requestBody[{}]", user, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.AUTO_SIGN_FAILED, ElecSignErrorCode.AUTO_SIGN_FAILED.getMessage() + e);
        }
    }

    /**
     * 查询合同签署者状态
     */
    public JSONObject getSignerStatus(User user, String contractId) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/contract/getSignerStatus/";

        JSONObject requestBody = new JSONObject();
        requestBody.put("contractId", contractId);

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("getSignerStatus failed user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.QUERY_SIGNER_STATUS_FAILED, ElecSignErrorCode.QUERY_SIGNER_STATUS_FAILED.getMessage() + sendResult.get("errmsg"));
                }
                log.info("getSignerStatus success user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);

                return JSON.parseObject(sendResult.get("data").toString());
            } else {
                log.warn("getSignerStatus failed, result is null, user[{}], requestBody[{}]", user, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.QUERY_SIGNER_STATUS_FAILED);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("getSignerStatus failed user[{}], requestBody[{}]", user, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.QUERY_SIGNER_STATUS_FAILED, ElecSignErrorCode.SEND_CONTRACT_FAIL.getMessage() + e);
        }
    }

    /**
     * bestSignAccount是否签了合同
     */
    public void checkBestSignAccountHasSigned(User user, String contractId, String bestSignAccount) {
        JSONObject signerStatus = getSignerStatus(user, contractId);
        if (Objects.equals(signerStatus.get(bestSignAccount), SingleSignStatusEnum.UN_SIGN.getStatus())) {
            throw new ElecSignBusinessException(ElecSignErrorCode.SIGNER_HAS_SIGNED);
        }
    }

    /**
     * 锁定并结束合同
     */
    public void lockAndFinishContract(User user, String contractId) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/storage/contract/lock/";

        JSONObject requestBody = new JSONObject();
        requestBody.put("contractId", contractId);

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                //{"errno":241423,"cost":21,"errmsg":"contract has been finished can not operated"}
                if (sendResult.getIntValue("errno") != 0 && sendResult.getIntValue("errno") != 241423) {
                    log.warn("lockAndFinishContract failed user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.LOCK_AND_FINISH_CONTRACT_FAILED, ElecSignErrorCode.LOCK_AND_FINISH_CONTRACT_FAILED.getMessage() + sendResult.get("errmsg"));
                }
                log.info("lockAndFinishContract success user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
            } else {
                log.warn("lockAndFinishContract failed, result is null, user[{}], requestBody[{}]", user, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.LOCK_AND_FINISH_CONTRACT_FAILED);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("lockAndFinishContract failed user[{}], requestBody[{}]", user, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.LOCK_AND_FINISH_CONTRACT_FAILED, ElecSignErrorCode.LOCK_AND_FINISH_CONTRACT_FAILED.getMessage() + e);
        }
    }

    /**
     * 下载合同PDF文件
     */
    public byte[] downloadContract(User user, String contractId)  {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/storage/contract/download/";

        // 组装url参数
        String urlParams = "contractId=" + contractId;

        try {
            byte[] contract = BestSignHttpUtil.sendHttpGet(developerId, host, method, urlParams);
            log.debug("downloadContract success user[{}], contractId[{}], result[{}]", user, contractId, contract);
            log.info("downloadContract success user[{}], contractId[{}]", user, contractId);
            return contract;
        } catch (Exception e) {
            log.warn("downloadContract failed user[{}], contractId[{}]", user, contractId, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.DOWN_CONTRACT_FROM_BEST_SIGN_FAILED, ElecSignErrorCode.DOWN_CONTRACT_FROM_BEST_SIGN_FAILED.getMessage() + e);
        }
    }
}