package com.facishare.crm.electronicsign.predefine.manager.bestsign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.crm.electronicsign.util.BestSignHttpUtil;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * 上上签文件相关的
 */
@Service
@Slf4j
public class BestSignFileManager {

    /**
     * 上传PDF到上上签
     *
     * @throws IOException
     * @param user
     * @param contractCreatorBestSignAccount
     * @param pdf
     * @param fpages  最大长度3位
     * @return        fid不会过期
     * @throws IOException
     */
    public String eaUploadPdf(User user, String contractCreatorBestSignAccount, byte[] pdf, int fpages) {
        if (fpages > 1000) {
            throw new ElecSignBusinessException(ElecSignErrorCode.UPLOAD_PDF_TO_BEST_SIGN_FAIL, ElecSignErrorCode.UPLOAD_PDF_TO_BEST_SIGN_FAIL.getMessage() + ", 打印模板长度超过1000");
        }

        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/storage/upload/";

        String fmd5 = DigestUtils.md5Hex(pdf);
        String fdata = Base64.encodeBase64String(pdf);

        JSONObject requestBody= new JSONObject();
        requestBody.put("account", contractCreatorBestSignAccount);
        requestBody.put("fmd5", fmd5);
        requestBody.put("ftype", "PDF");
        requestBody.put("fname", "fxiaoke" + System.currentTimeMillis() + ".pdf");   // 上上签：可以随便给名，但是不要有特殊字符（重复也可以）
        requestBody.put("fpages", String.valueOf(fpages));
        requestBody.put("fdata", fdata);

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("eaUploadPdf failed user[{}], pdf[{}], requestBody[{}], sendResult[{}]", user, pdf, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.UPLOAD_PDF_TO_BEST_SIGN_FAIL, ElecSignErrorCode.UPLOAD_PDF_TO_BEST_SIGN_FAIL.getMessage() + sendResult.get("errmsg"));
                }
                log.debug("eaUploadPdf success user[{}], pdf[{}], requestBody[{}], sendResult[{}]", user, pdf, requestBody, sendResult);
                log.info("eaUploadPdf success user[{}], contractCreatorBestSignAccount[{}], fpages[{}], sendResult[{}]", user, contractCreatorBestSignAccount, fpages, sendResult);

                JSONObject dataJson = JSON.parseObject(sendResult.get("data").toString());
                return dataJson.get("fid").toString();
            } else {
                log.warn("eaRegeaUploadPdf failed, result is null, user[{}], pdf[{}], requestBody[{}]", user, pdf, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.UPLOAD_PDF_TO_BEST_SIGN_FAIL);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("eaUploadPdf failed user[{}], contractCreatorBestSignAccount[{}], pdf[{}], fpages[{}], requestBody[{}]", user, contractCreatorBestSignAccount, pdf, fpages, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.UPLOAD_PDF_TO_BEST_SIGN_FAIL, ElecSignErrorCode.UPLOAD_PDF_TO_BEST_SIGN_FAIL.getMessage() + e);
        }
    }

    /**
     * 查询关键字位置
     *
     {
     "errno":0,
     "cost":10,
     "data":{
     "positions":[{
     "y1":"0.6919",
     "x1":"0.1867",
     "y2":"0.7021",
     "x2":"0.222",
     "pageNum":"1"
     }]
     },
     "errmsg":""
     }
     找不到的情况：{"errno":0,"cost":10,"data":{"positions":[]},"errmsg":""}
     */
    public JSONArray findKeywordPositions(User user, String keyword, String pdfData) {
        String host = ConfigCenter.BEST_SIGN_URL;
        String developerId = ConfigCenter.BEST_SIGN_DEVELOPER_ID;
        String method = "/pdf/findKeywordPositions/";

        JSONObject requestBody= new JSONObject();
        requestBody.put("keyword", keyword);
        requestBody.put("pdfData", pdfData);

        try {
            JSONObject sendResult = BestSignHttpUtil.sendPost(developerId, host, method, requestBody.toJSONString());
            if (sendResult != null) {
                if (sendResult.getIntValue("errno") != 0) {
                    log.warn("findKeywordPositions failed user[{}], requestBody[{}], sendResult[{}]", user, requestBody, sendResult);
                    throw new ElecSignBusinessException(ElecSignErrorCode.GET_KEYWORD_POSITION_FAIL, ElecSignErrorCode.GET_KEYWORD_POSITION_FAIL.getMessage() + sendResult.get("errmsg"));
                }
                log.info("findKeywordPositions success user[{}], keyword[{}], sendResult[{}]", user, keyword, sendResult);
                log.debug("findKeywordPositions success user[{}], keyword[{}], pdfData[{}], sendResult[{}]", user, keyword, pdfData, sendResult);

                if (sendResult.get("data") == null) {
                    throw new ElecSignBusinessException(ElecSignErrorCode.NO_FIND_KEYWORD, ElecSignErrorCode.NO_FIND_KEYWORD.getMessage() + "'" + keyword + "'");
                }
                JSONObject dataJsonObject = JSON.parseObject(sendResult.get("data").toString());
                String positionsStr = dataJsonObject.get("positions").toString();
                if (Objects.equals(positionsStr, "[]")) {
                    throw new ElecSignBusinessException(ElecSignErrorCode.NO_FIND_KEYWORD, ElecSignErrorCode.NO_FIND_KEYWORD.getMessage() + "'" + keyword + "'");
                }
                return JSON.parseArray(positionsStr);
            } else {
                log.warn("findKeywordPositions failed, result is null, user[{}], requestBody[{}]", user, requestBody);
                throw new ElecSignBusinessException(ElecSignErrorCode.GET_KEYWORD_POSITION_FAIL);
            }
        } catch (ElecSignBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("findKeywordPositions failed user[{}], requestBody[{}]", user, requestBody, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.GET_KEYWORD_POSITION_FAIL, ElecSignErrorCode.GET_KEYWORD_POSITION_FAIL.getMessage() + e);
        }
    }
}