package com.facishare.crm.deliverynote.predefine.manager.kdapi;

import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.crm.deliverynote.util.ConfigCenter;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * <a href="http://www.kdniao.com/api-all">快递鸟物流API</a>
 *
 */
@Component
@Slf4j
public class KdniaoAPI {

    /**
     * Json方式 单号识别
     * <br/>
     * <a href="http://www.kdniao.com/api-recognise">单号识别API</a>
     * @throws Exception
     */
    public OrderDistinguishResult orderDistinguish(String expNo)  {
        String requestData= "{'LogisticCode':'" + expNo + "'}";
        Map<String, String> params = getReqParam(requestData, "2002");
        String resultJson = sendPost(ConfigCenter.Kdniao_OrderDistinguishApiReqURL, params);
        OrderDistinguishResult result = new Gson().fromJson(resultJson, OrderDistinguishResult.class);
        if (!"true".equals(result.getSuccess())) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.GET_LOGISTICS_INFO_ERROR);
        }
        return result;
    }

    @Data
    public static class BaseResult {
        @SerializedName("EBusinessID")
        private String eBusinessID;
        @SerializedName("Success")
        private String success;
    }

    @Data
    public static class OrderDistinguishResult extends BaseResult {
        @SerializedName("logisticCode")
        private String logisticCode;
        @SerializedName("Shippers")
        private List<Shipper> shippers;
    }

    @Data
    public static class Shipper {
        @SerializedName("ShipperCode")
        private String code;
        @SerializedName("ShipperName")
        private String name;
    }


    /**
     * Json方式 即时查询订单物流轨迹
     * * <br/>
     * <a href="http://www.kdniao.com/api-track">即时查询API</a>
     * @throws Exception
     */
    public GetOrderTracesResult getOrderTraces(String expCode, String expNo) {
        String requestData = "{'OrderCode':'','ShipperCode':'" + expCode + "','LogisticCode':'" + expNo + "'}";
        Map<String, String> params = getReqParam(requestData, "1002");
        String resultJson = sendPost(ConfigCenter.Kdniao_TrackQueryApiReqURL, params);
        return new Gson().fromJson(resultJson, GetOrderTracesResult.class);
    }

    @Data
    public static final class GetOrderTracesResult extends BaseResult {
        @SerializedName("OrderCode")
        private String orderCode;
        @SerializedName("ShipperCode")
        private String shipperCode;
        @SerializedName("LogisticCode")
        private String logisticCode;
        @SerializedName("Reason")
        private String reason;
        @SerializedName("State")
        private String state;
        @SerializedName("Traces")
        private List<Trace> traces;
    }

    @Data
    public static final class Trace {
        @SerializedName("AcceptTime")
        private String acceptTime;
        @SerializedName("AcceptStation")
        private String acceptStation;
        @SerializedName("Remark")
        private String remark;
    }

    private Map<String, String> getReqParam(String requestData, String requestType) {
        Map<String, String> params = Maps.newHashMap();
        try {
            params.put("RequestData", urlEncoder(requestData, "UTF-8"));
            params.put("EBusinessID", ConfigCenter.Kdniao_EBusinessID);
            params.put("RequestType", requestType);
            String dataSign=encrypt(requestData, ConfigCenter.Kdniao_AppKey, "UTF-8");
            params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
            params.put("DataType", "2");
        } catch (Exception e) {
            log.error("getReqParam error", e);
        }
        return params;
    }

    /**
     * MD5加密
     * @param str 内容
     * @param charset 编码方式
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * base64编码
     * @param str 内容
     * @param charset 编码方式
     * @throws UnsupportedEncodingException
     */
    private String base64(String str, String charset) throws UnsupportedEncodingException{
        String encoded = base64Encode(str.getBytes(charset));
        return encoded;
    }

    @SuppressWarnings("unused")
    private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
        String result = URLEncoder.encode(str, charset);
        return result;
    }

    /**
     * 电商Sign签名生成
     * @param content 内容
     * @param keyValue Appkey
     * @param charset 编码方式
     * @throws UnsupportedEncodingException ,Exception
     * @return DataSign签名
     */
    @SuppressWarnings("unused")
    private String encrypt (String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception
    {
        if (keyValue != null)
        {
            return base64(MD5(content + keyValue, charset), charset);
        }
        return base64(MD5(content, charset), charset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param params 请求的参数集合
     * @return 远程资源的响应结果
     */
    @SuppressWarnings("unused")
    private String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(6000);
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (params != null) {
                StringBuilder param = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if(param.length()>0){
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                }
                out.write(param.toString());
            }
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error("KdniaoAPI.sendPost error. url[{}], params[{}]", url, params, e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SYSTEM_ERROR, e.getMessage());
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                log.error("close error. ", ex);
            }
        }
        return result.toString();
    }


    private static char[] base64EncodeChars = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/' };

    public static String base64Encode(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len)
            {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len)
            {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        KdniaoAPI kdniaoAPI = new KdniaoAPI();
        try {
            KdniaoAPI.OrderDistinguishResult result = kdniaoAPI.orderDistinguish("3967950525457");
            System.out.print(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            GetOrderTracesResult result = kdniaoAPI.getOrderTraces("ANE", "210001633605");
            System.out.print(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
