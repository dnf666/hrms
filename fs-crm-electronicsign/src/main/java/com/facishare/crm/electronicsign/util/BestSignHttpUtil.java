package com.facishare.crm.electronicsign.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class BestSignHttpUtil {
    //开发者私钥
    // TODO: 2018/4/23 chenzs 线上是否一样
    private static String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC0JrpuJJgLOKJVtjQkhrNDjAo0s3LnLDLuktfMXXJ/3pWim3isgTlsUvkYo+9VKv9zKPD7rYT2BWQmCSSlwKbokd0wDrugwopYTIx/WoI+KRmXEmtVkMpcT/k+i6Ih34vDCsLpk4mapf7Xj+lrVMRXZy9v2eRMa3wnfhOwdJQfUEho3e2KRIUF5E5/fgl3im54R0MMneWPC2KDDZGXmE/Lg2jwQPpozNOjBv35UZTJvR821BD75wzw9klMEHmt22eqC/W+Trf/AmKOx8aXl60A+TaKPgTnVHswLowBAAt7vL01miJ7qlTAN+8A+6Jr2jS2gO8LQ6fxT6uzlRY14Z/jAgMBAAECggEAc+7EZOM50WbV2TDTJ3pj3KE/ZCDjXe9sq2lmZIbyi2VziFxi8SiMCrDuyrOc7oMoNzTuuBg3i5d2lp+lrOFoyBwuaqHgjxkCrMY+WCqnzFbot/bLihoOkA+LR3vWj9Prfk3rlyMyF4qhkJl1TnQTkme9+E4RhDhbgpK5GwI63FbaMLbL+Cw9vP1H5jTfqneHI/fjN/RFFjrm1VD4eCAaKNbhyYE8+sR3x0Kbw8SVLoih3RJBeCVnc5+/0yy4UHz9V++8fEZzqVDKNQRu5hPPneoWvRVKDTdBnhQqZxKI/StJiy5swEIEYq1a8hMdD93kuvIITz2pXw9wHGkY7ezzUQKBgQDh4SJXOL/wP3xtHiVu/r2BBzUaSzMV6iw70KpC+byhPRcFE8ogx815P8Zt6jDyzrtC0ZA2sCsTQq3/rOLG100GspaxVtYiAFLFC4VPRUOahvDj22O+Rekwrq4nPg2lZP0Wt5hskRCPve9DphkwswxJfQt7pbPRhrYdWq5VLIKlOQKBgQDMLJF86neV/+/GaJU2bMlyHAfjQW99pF+/qH0wznmVbuVWBoK4USaS4eGORz+Cx79pKl4dNV+UvlSA8AuLzQ+YyUuvfXgKPehnDFuNvzgMkPRUp5reUMDzfbvBN62rHK1jO/8HMeZQUnafAx/5dUnjR6VMcbhbWAbmJmRgJ2+p+wKBgAL6VNmRhfZE3/8QRq4P7a+lyK1wEFxZmfuv5I69fB8kDwmiGSgVej/+9z67t6l70DwxRxVxfR/j4SddwB+e9wT/lb0AyBHqryyp2jgRUbLX1JUsb0Qy58AcjW83AjcL/cou4XOM9grvFhhuOCbMNX1CiMQ4iwZYIE6Cw4mb7J0BAoGAQ6tt2OH5Gp0GAlN9SNmLdqd1sctpQVIubd5RB9EXGQD1P6rOvnoe98WntTlGAnljpl5lbPbYo/rlFQr6OK6RQclNrW8/Tt2v3h+JZJSA5iFQ6ZHXUWGgYdNFXEew5qqNiPtEjkTqmaqLKC6n9Uz7XTnMvmZefN/TAYqt7/SCHOsCgYEA0XM9KkeBmh1XWHHKaICopSKMxzcql94bJ0vwVYA8u9xF15+jD8faubkb0r59FD/Hnc7Zc8iIYXiaf3qr+N+lddo92iDDfXHXDAU98dWbrNIoNrKln04u043cEZNl6aFqSoTaWu9M8eHZugakElHeKyBkExZ+nnGd171Hx+CeDJo=";

    private static String urlSignParams = "?developerId=%s&rtick=%s&signType=rsa&sign=%s";

    public static JSONObject sendPost(String developerId, String host, String method, String requestBody) throws IOException {
        // 生成一个时间戳参数
        String rtick = RSAUtils.getRtick();
        // 计算参数签名
        String paramsSign = RSAUtils.calcRsaSign(developerId, privateKey, host, method, rtick, null, requestBody);
        // 签名参数追加为url参数
        String urlParams = String.format(urlSignParams, developerId, rtick, paramsSign);
        // 发送请求
        String responseBody = HttpClientSender.sendHttpPost(host, method, urlParams, requestBody);
        JSONObject userObj = JSON.parseObject(responseBody);
        return userObj;
    }

    public static byte[] sendHttpGet(String developerId, String host, String method, String urlParams) throws Exception {
        // 生成一个时间戳参数
        String rtick = RSAUtils.getRtick();

        // 计算参数签名
        String paramsSign = RSAUtils.calcRsaSign(developerId, privateKey, host, method, rtick, urlParams, null);
        // 签名参数追加为url参数
        urlParams = String.format(urlSignParams, developerId, rtick, paramsSign) + "&" + urlParams;
        // 发送请求
        byte[] responseBody = HttpClientSender.sendHttpGet(host, method, urlParams);
        // 返回结果解析
        return responseBody;
    }
}
