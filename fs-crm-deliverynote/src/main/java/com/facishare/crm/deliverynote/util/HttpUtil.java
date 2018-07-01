package com.facishare.crm.deliverynote.util;

import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.rest.proxy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@SuppressWarnings("all")
public class HttpUtil {
    private static CloseableHttpClient closeableHttpClient;

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(50);
        SocketConfig.Builder sb = SocketConfig.custom();
        sb.setSoKeepAlive(true);
        sb.setTcpNoDelay(true);
        connectionManager.setDefaultSocketConfig(sb.build());
        HttpClientBuilder hb = HttpClientBuilder.create();
        hb.setConnectionManager(connectionManager);
        RequestConfig.Builder rb = RequestConfig.custom();
        rb.setSocketTimeout(5000);
        rb.setConnectTimeout(2000);
        hb.setDefaultRequestConfig(rb.build());
        closeableHttpClient = hb.build();
    }

    public static <T> T post(String url, Map<String, String> headers, Object body, Type resultClazz) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.post(url);
        setHttpHeader(headers, requestBuilder);
        setEntity(requestBuilder, body);
        log.info("request url:{},headers:{},body:{}", url, headers, body);
        return handler(requestBuilder, resultClazz);
    }

    private static <T> T handler(RequestBuilder requestBuilder, Type resultClazz) throws IOException {
        Throwable var8 = null;
        T var10;
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(requestBuilder.build());
            HttpEntity in = response.getEntity();
            String responseBody = new String(EntityUtils.toByteArray(in), "UTF-8");
            log.info("response url:{},response body:{}", requestBuilder.getUri(), responseBody);
            if ("ok".equals(responseBody)) {
                return (T) Boolean.TRUE;
            }
            T ret = JsonUtil.fromJson(responseBody, resultClazz);
            if (ret == null) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.OUT_INVOKE_RESULT_ISNULL);
            }
            var10 = ret;
        } catch (Throwable var19) {
            var8 = var19;
            throw var19;
        } finally {
            if (response != null) {
                if (var8 != null) {
                    try {
                        response.close();
                    } catch (Throwable var18) {
                        var8.addSuppressed(var18);
                    }
                } else {
                    response.close();
                }
            }
        }
        return var10;
    }

    private static void setEntity(RequestBuilder requestBuilder, Object body) {
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContentEncoding("UTF-8");
        if (body != null) {
            if (body instanceof String) {
                byte[] bytes = ((String) body).getBytes();
                entity.setContentLength(bytes.length);
                entity.setContent(new ByteArrayInputStream(bytes));
            } else {
                byte[] bytes = JsonUtil.toJson(body).getBytes();
                entity.setContentLength((long) bytes.length);
                entity.setContent(new ByteArrayInputStream(bytes));
            }
            requestBuilder.setEntity(entity);
        }
    }

    private static void setHttpHeader(Map<String, String> headers, RequestBuilder requestBuilder) {
        Iterator var3 = headers.entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry<String, String> e = (Map.Entry<String, String>) var3.next();
            requestBuilder.addHeader(e.getKey(), e.getValue());
        }
    }
}
