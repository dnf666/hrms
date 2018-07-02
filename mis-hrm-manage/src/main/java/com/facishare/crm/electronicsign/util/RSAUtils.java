package com.facishare.crm.electronicsign.util;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * 参数签名算法工具类
 */
public class RSAUtils {

    /**
     * 获取当前的时间戳参数
     * @return
     */
    public static String getRtick(){
        long timestamp = new Date().getTime();
        int rnd = (int)Math.random() * 1000;
        String rtick = timestamp + "" + rnd;
        return rtick;
    }

    /**
     * 计算参数签名
     * @param developerId 开发者ID
     * @param privateKey 用户私钥
     * @param host 请求的HOST地址（http://ip:port/context）
     * @param methodName 请求的接口方法名
     * @param rtick 时间戳参数
     * @param urlParams url参数（param1=value1&param2=value2&param3=value3）
     * @param requestBody request body 参数（JSON字符串）
     * @return
     */
    public static String calcRsaSign(String developerId, String privateKey, String host, String methodName, String rtick, String urlParams, String requestBody) {
        String url = host + methodName;

        Map<String, String> mySignedURLParams = new TreeMap<String, String>();
        mySignedURLParams.put("developerId", developerId);
        mySignedURLParams.put("rtick", rtick);
        mySignedURLParams.put("signType", "rsa");

        if(urlParams != null && !"".equals(urlParams)){
            String[] params = urlParams.split("&");
            for(String p1 : params){
                String[] p2 = p1.split("=");
                String key = p2[0];
                String value = "";
                if(p2.length == 2){
                    value = p2[1];
                }
                mySignedURLParams.put(key, value);
            }
        }

        String requestPath;
        try {
            requestPath = new URL(url).getPath();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        StringBuilder signStringBuilder = new StringBuilder();
        for (String name : mySignedURLParams.keySet()) {
            String value = mySignedURLParams.get(name);
            signStringBuilder.append(name);
            signStringBuilder.append("=");
            signStringBuilder.append(value);
        }
        signStringBuilder.append(requestPath);

        if (requestBody != null && !"".equals(requestBody) ) {
            String requestMd5 = getRequestMd5(requestBody);
            signStringBuilder.append(requestMd5);
        }

        String signString = signStringBuilder.toString();
        String rsaSign = calcRsaSign(privateKey, signString);
        //rsa算出来的sign，需要urlencode
        try {
            rsaSign = URLEncoder.encode(rsaSign,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            rsaSign = null;
        }
        return rsaSign;
    }

    /**
     * 获取request body 的MD5
     * @param requestBody
     * @return
     */
    private static String getRequestMd5(final String requestBody) {
        byte[] data;

        String newRequestBody = convertToUtf8(requestBody);
        try {
            data = newRequestBody.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return md5(data);
    }

    /**
     * 计算参数RSA签名
     * @param privateKey
     * @param signData
     * @return
     */
    private static String calcRsaSign(String privateKey, final String signData) {
        byte[] data;
        try {
            data = signData.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        byte[] sign = null;
        // 解密由base64编码的私钥
        byte[] privateKeyBytes = base64decode(privateKey.getBytes());

        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        // 取私钥匙对象
        PrivateKey priKey;
        try {
            priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        }
        catch (InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        // 用私钥对信息生成数字签名
        Signature signature;
        try {
            signature = Signature.getInstance("SHA1withRSA");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            signature.initSign(priKey);
        }
        catch (InvalidKeyException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        try {
            signature.update(data);
            sign = signature.sign();
        }
        catch (SignatureException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return new String(base64encode(sign));
    }

    /**
     * 转换字符集到utf8
     *
     * @param src
     * @return
     */
    private static String convertToUtf8(String src) {
        if (src == null || src.length() == 0) {
            return src;
        }
        if ("UTF-8".equalsIgnoreCase(Charset.defaultCharset().name())) {
            return src;
        }

        byte[] srcData = src.getBytes();
        try {
            return new String(srcData, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * md5
     * @param data
     * @return
     */
    public static String md5(byte[] data) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        byte[] btInput = data;
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest mdInst;
        try {
            mdInst = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        // 使用指定的字节更新摘要
        mdInst.update(btInput);
        // 获得密文
        byte[] md = mdInst.digest();
        // 把密文转换成十六进制的字符串形式
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    /**
     * base64编码
     * @param data
     * @return
     */
    public static byte[] base64encode(byte[] data) {
        return Base64.encodeBase64(data);
    }
    
    /**
     * base64编码字符串
     * @param data
     * @return
     */
    public static String base64encodeString(byte[] data) {
        return Base64.encodeBase64String(data);
    }
    
    /**
     * base64解码
     * @param data
     * @return
     */
    public static byte[] base64decode(byte[] data) {
        try {
            return Base64.decodeBase64(data);
        } catch (Exception e) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (int i = 0; i < data.length; i++) {
                byte c = data[i];
                if (c == 13 || c == 10) {
                    continue;
                }
                outputStream.write(c);
            }
            try {
                outputStream.close();
            } catch (IOException e2) {

            }
            data = outputStream.toByteArray();
            return Base64.decodeBase64(data);
        }
    }
}
