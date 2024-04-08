package com.english.baidutrans;

import com.english.Utils.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author XYC
 * Http请求工具类
 */
public class HttpUtil {
    private static final AbstractHttpClientResponseHandler<String> responseHandler = new AbstractHttpClientResponseHandler<>() {
        @Override
        public String handleEntity(HttpEntity httpEntity) throws IOException {
            if (httpEntity != null) {
                try {
                    return EntityUtils.toString(httpEntity, "UTF-8");
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    };

    /**
     * get 请求
     *
     * @param url 请求地址
     */
    public static String doGet(String url) throws IOException {
        String responseData;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            responseData = httpClient.execute(httpGet, responseHandler);
        }
        return responseData;
    }

    /**
     * post 请求
     *
     * @param url        请求地址
     * @param reqContent 请求内容
     */
    public static String doPost(String url, String reqContent) throws IOException {
        String responseData;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            if (StringUtils.hasText(reqContent)) {
                httpPost.setEntity(new StringEntity(reqContent, StandardCharsets.UTF_8));
            }
            responseData = httpClient.execute(httpPost, responseHandler);
        }
        return responseData;
    }

    /**
     * post 请求
     *
     * @param url        请求地址
     * @param reqContent 请求内容
     */
    public static String doPost(String url, Map<String, String> reqContent) throws IOException {
        String responseData;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            // 装填参数
            ArrayList<NameValuePair> names = new ArrayList<>();
            if (reqContent != null) {
                for (Map.Entry<String, String> entry : reqContent.entrySet()) {
                    names.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            // 设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(names, StandardCharsets.UTF_8));
            responseData = httpClient.execute(httpPost, responseHandler);
        }
        return responseData;
    }
}
