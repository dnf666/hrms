package com.facishare.crm.electronicsign.predefine.manager.bestsign;

import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.crm.electronicsign.util.BestSignHttpUtil;
import com.facishare.crm.electronicsign.util.SealUtils;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 签名/印章图片
 */
@Slf4j
@Service
public class BestSignSignatureImageManager {
    /**
     * 生成用户签名/印章图片
     */
    public void create(User user, String bestSignAccount) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/signatureImage/user/create/";

        JSONObject requestBody= new JSONObject();
        requestBody.put("account", bestSignAccount);
        //text
        //fontName
        //fontSize
        //fontColor

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("BestSignSignatureImageManager.create failed user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.CREATE_SIGNATURE_IMAGE_BY_BEST_SIGN_FAIL, ElecSignErrorCode.CREATE_SIGNATURE_IMAGE_BY_BEST_SIGN_FAIL.getMessage() + sendResult.get("errmsg"));
                }
                log.info("BestSignSignatureImageManager.create success user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
            } else {
                log.warn("BestSignSignatureImageManager.create failed, result is null, user[{}], requestBody[{}]", user, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.CREATE_SIGNATURE_IMAGE_BY_BEST_SIGN_FAIL);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("BestSignSignatureImageManager.create failed user[{}], requestBody[{}]", user, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.CREATE_SIGNATURE_IMAGE_BY_BEST_SIGN_FAIL, ElecSignErrorCode.CREATE_SIGNATURE_IMAGE_BY_BEST_SIGN_FAIL.getMessage() + e);
        }
    }

    /**
     * 生成签章
     */
    public void createAndUploadSignatureImage(User user, String bestSignAccount, String enterpriseName) {
        //生成签章
        byte[] image = null;
        try {
            image = SealUtils.genSealData(enterpriseName);
        } catch (IOException e) {
            log.warn("SealUtils.genSealData generateSignatureImage failed, user[{}], enterpriseName[{}]", user, enterpriseName);
            throw new ElecSignBusinessException(ElecSignErrorCode.GENERATE_SIGNATURE_IMAGE_FAIL);
        }

        //上传签章
        upload(user, bestSignAccount, image, null);
    }

    /**
     * 上传用户签名/印章图片
     *
     * @param user            必填
     * @param bestSignAccount 必填
     * @param image           必填
     * @param imageName       选填
     */
    public void upload(User user, String bestSignAccount, byte[] image, String imageName) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/signatureImage/user/upload/";

        String imageData = Base64.encodeBase64String(image);

        JSONObject requestBody= new JSONObject();
        requestBody.put("account", bestSignAccount);
        requestBody.put("imageData", imageData);
        requestBody.put("imageName", imageName);  //传空或default表示更新默认的签名/印章图片

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("upload failed user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.UPLOAD_SIGNATURE_IMAGE_TO_BEST_SIGN_FAIL, ElecSignErrorCode.UPLOAD_SIGNATURE_IMAGE_TO_BEST_SIGN_FAIL.getMessage() + sendResult.get("errmsg"));
                }
                log.info("upload success user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
            } else {
                log.warn("upload failed, result is null, user[{}], requestBody[{}]", user, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.UPLOAD_SIGNATURE_IMAGE_TO_BEST_SIGN_FAIL);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("upload failed user[{}], requestBody[{}]", user, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.UPLOAD_SIGNATURE_IMAGE_TO_BEST_SIGN_FAIL, ElecSignErrorCode.UPLOAD_SIGNATURE_IMAGE_TO_BEST_SIGN_FAIL.getMessage() + e);
        }
    }
}
