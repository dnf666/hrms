package com.mis.hrm.util;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 发送请求，并返回请求的状态（返回的状态码）
 */
@Resource
public class HttpClientUtil {


// 　  传入的ｕｒｌ　为空　或者　不符合条件
    private static final int ERROR_URL = -1;

//    发送请求失败。
    private static final int ERROR_SEND = 404;

    /**
     * 发送一个ｇｅｔ请求，并返回状态码
     * @param url　接口地址
     * @return 状态码
     */
    public static int sendGet(String url){
        try {
            url = getUsableUrl(url);
        } catch (StringIsNullException e) {
            return ERROR_URL;
        }
        int statusCode = 404;
        try {
//        1.创建一个默认的 client　实例。
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        2.创建一个httpGet请求
        HttpGet httpGet = new HttpGet(url.trim());
            System.out.println("发送的ｕｒｌ : " + url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0");
//        3.执行get请求
        CloseableHttpResponse httpResponse;

            httpResponse = httpClient.execute(httpGet);
            System.out.println("我想要的状态码：　" + httpResponse.getStatusLine().getStatusCode());
            System.out.println("我想要的状态行：　" + httpResponse.getStatusLine());
            statusCode = httpResponse.getStatusLine().getStatusCode();
        } catch (IOException e) {
            return ERROR_SEND;
        }
        return statusCode;
    }

    /**
     * 发送一个ｐｏｓｔ请求，同时带有传递参数
     * @param url　接口地址
     * @param jsonParams　请求参数
     * @return　状态码
     */
    public static int sendPost(String url, String jsonParams){
        int statusCode = 404;
        try {
            url = getUsableUrl(url);
        } catch (StringIsNullException e) {
            return ERROR_URL;
        }
//       １．创建一个　client
        CloseableHttpClient httpClient = HttpClients.createDefault();
//       ２．创建一个ｐｏｓｔ
            HttpPost post = new HttpPost(url);


        return 404;
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
//        检查是否是以 http://　开头
        if (url.length() >= 6 && !url.substring(0,7).equals("http://")){
            url = "http://" + url;
        }
        if (url.trim().equals("")){
            throw new StringIsNullException("传入的全是空格");
        }
        return url;
    }

    /**
     * 工具类
     * 去掉开始或者结束的全角空格字符
     * @param s
     * @return
     */
    private static String removeSpace(String s){
        StringBuilder result = new StringBuilder(s);
        while (result.length() > 0 && result.charAt(0) == '　'){
            result.delete(0,1);
        }
        while (result.length() > 1 && result.charAt( result.length()-1 ) == '　'){
            result.delete(result.length() - 1, result.length());
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
