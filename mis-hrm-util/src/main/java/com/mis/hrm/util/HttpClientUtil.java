package com.mis.hrm.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.function.Function;

/**
 * 发送请求，并返回请求的状态（返回的状态码）
 */
@Resource
public class HttpClientUtil {

    private static final String CONTENT_TYPE = "content-Type";
    private static final String JSON_TYPE = "application/json";
    private static final String HTTP_REQURIED = "http://";
    // 　  传入的ｕｒｌ　为空　或者　不符合条件
    private static final int ERROR_URL = -1;
    //    发送请求失败。
    private static final int ERROR_SEND = 404;
    //    全角字符的空格
    private static final char FULL_WIDTH_SPACE = '　';
    //    半角字符的空格
    private static final char HALF_WIDTH_SPACE = ' ';
    //　　　设置三秒的等待时间
    private static final int THREE_SECONDS = 3000;

    //    对所有请求限定相同的规则。
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(THREE_SECONDS)
            .setConnectionRequestTimeout(THREE_SECONDS).build();
    /**
     * 发送一个ｇｅｔ请求，并返回状态码
     * @param url　接口地址
     * @return 状态码
     */
    public static Integer sendGet(String url){
        try {
            url = getUsableUrl(url);
        } catch (StringIsNullException e) {
            return ERROR_URL;
        }

        int statusCode;
//        1.创建一个默认的 client　实例。
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
//        2.创建一个httpGet请求
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
//        3.执行get请求,同时返回状态码
            statusCode = httpClient.execute(httpGet, new StatusCodeResponseHandler());
        } catch (IOException e) {
            return ERROR_SEND;
        }
        return statusCode;
    }

    /**
     * 发送一个ｐｏｓｔ请求，同时带有传递参数
     * @param url　接口地址
     * @param jsonParams　请求参数,json格式
     * @return　int
     */
    public static int sendPost(String url, String jsonParams){
        int statusCode;
        try {
            url = getUsableUrl(url);
        } catch (StringIsNullException e) {
            return ERROR_URL;
        }
//        1.创建一个默认的 client　实例。
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
//        2.创建一个httpGet请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(jsonParams));
            httpPost.setHeader(CONTENT_TYPE,JSON_TYPE);
            httpPost.setConfig(requestConfig);
//        3.执行post请求,同时返回状态码
            statusCode = httpClient.execute(httpPost, new StatusCodeResponseHandler());
        } catch (IOException e) {
            return ERROR_SEND;
        }
        return statusCode;
    }

    /**
     * 发送一个put请求，并返回地址
     * @param url　请求地址
     * @param jsonParams　请求参数
     * @return
     */
    public static int sendPut(String url, String jsonParams){
        try {
            url = getUsableUrl(url);
        } catch (StringIsNullException e) {
            return ERROR_URL;
        }
        int statusCode;
//        1.创建一个默认的 client　实例。
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
//        2.创建一个httpGet请求
            HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(new StringEntity(jsonParams));
            httpPut.setHeader(CONTENT_TYPE,JSON_TYPE);
            httpPut.setConfig(requestConfig);
//        3.执行post请求,同时返回状态码
            statusCode = httpClient.execute(httpPut, new StatusCodeResponseHandler());
        } catch (IOException e) {
            return ERROR_SEND;
        }
        return statusCode;
    }

    public static int sendDelete(String url){
        try {
            url = getUsableUrl(url);
        } catch (StringIsNullException e) {
            return ERROR_URL;
        }
        int statusCode;
//        1.创建一个默认的 client　实例。
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
//        2.创建一个httpGet请求
            HttpDelete httpDelete = new HttpDelete(url);
            httpDelete.setHeader(CONTENT_TYPE,JSON_TYPE);
            httpDelete.setConfig(requestConfig);
//        3.执行post请求,同时返回状态码
            statusCode = httpClient.execute(httpDelete, new StatusCodeResponseHandler());
        } catch (IOException e) {
            return ERROR_SEND;
        }
        return statusCode;
    }

    /**
     * 对字符串进行处理，得到一个可用的ｕｒｌ
     * @param url
     * @return
     * @throws StringIsNullException
     */
    private static String getUsableUrl(String url) throws StringIsNullException {
        if (url == null){
            throw new StringIsNullException("字符串为空");
        }
        //去掉以全角空格开头或者结尾空格的字符。
        url = removeSpace(url);
        if (url.equals("")
                || (url.length() == 7 && url.equals(HTTP_REQURIED))
                || url.length() == 8 || "https://".equals(url)){
            throw new StringIsNullException("传入的全是空格");
        }
        boolean httpIsExist = url.length() >= 7 && url.substring(0,7).equals(HTTP_REQURIED);
        boolean httpsIsExist = url.length() >= 8 && url.substring(0,8).equals("https://");
//        检查是否是以 http://  or https://　开头
        if ( !(httpIsExist || httpsIsExist)){
            url = HTTP_REQURIED + url;
        }

        return url;
    }

    /**
     * 工具类
     * 去掉开始或者结束的全角空格字符和半角字符
     * @param s
     * @return
     */
    private static String removeSpace(String s){
        StringBuilder result = new StringBuilder(s);

        for (int i = 0; i < result.length(); i++) {
            if ((result.charAt(i) == FULL_WIDTH_SPACE || result.charAt(i) == HALF_WIDTH_SPACE)){
                result.delete(i, i+1);
                i--;
            }
        }
        return result.toString();
    }
}

/**
 * 自定义异常处理
 */
class StringIsNullException extends Exception{
    private String msg;
    public StringIsNullException(String message) {
        super(message);
        msg = message;
    }

    public String getMsg() {
        return msg;
    }
}

class StatusCodeResponseHandler implements ResponseHandler<Integer>{

    @Override
    public Integer handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        return response == null ? 404 : response.getStatusLine().getStatusCode();
    }
}