package com.english.baidutrans;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author XYC
 * 请求工具类
 */
public class HttpUtil {
    /**
     * get 请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static JSONObject doGetStr(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            String resContent = EntityUtils.toString(entity, "UTF-8");
            return JSONObject.parseObject(resContent);
        }
        return null;
    }

    /**
     * post 请求 String装填
     *
     * @param url
     * @param reqContent
     * @return
     * @throws IOException
     */
    public static JSONObject doPostStr(String url, String reqContent) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        if (!StringUtils.isEmpty(reqContent)) {
            httpPost.setEntity(new StringEntity(reqContent, "UTF-8"));
        }
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            String resContent = EntityUtils.toString(entity, "UTF-8");
            return JSONObject.parseObject(resContent);
        }
        return null;
    }

    /**
     * post 请求 String装填
     *
     * @param url
     * @param reqContent
     * @return
     * @throws IOException
     */
    public static JSONObject doPostStr(String url, Map<String, String> reqContent) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        //装填参数
        ArrayList<NameValuePair> names = new ArrayList<>();
        if (reqContent != null) {
            for (Map.Entry<String, String> entry : reqContent.entrySet()) {
                names.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        //设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(names, "UTF-8"));

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            String resContent = EntityUtils.toString(entity, "UTF-8");
            return JSONObject.parseObject(resContent);
        }
        return null;
    }
}
