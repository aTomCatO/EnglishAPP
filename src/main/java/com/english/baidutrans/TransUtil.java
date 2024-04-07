package com.english.baidutrans;

import com.english.Utils.InstanceUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XYC
 */
public class TransUtil {
    /**
     * 百度翻译接口地址
     */
    private static final String TRANS_API_HOST = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    private static final String appid = "20220323001136339";
    private static final String securityKey = "X_wLubR1NLEq2QAgEWdM";

    private static final ObjectMapper json = InstanceUtils.JSON;

    /**
     * 获得翻译结果
     *
     * @param query 翻译对象
     * @param from  对象语种
     * @param to    目标语种
     */
    public static String translate(String query, String from, String to) throws IOException {
        Map<String, String> params = buildParams(query, from, to);
        String jsonStr;
        // 如果翻译内容过长，将用post请求
        if (query.length() >= 2000) {
            jsonStr = HttpUtil.doPost(TRANS_API_HOST, params);
        } else {
            String url = getUrlWithQueryString(TRANS_API_HOST, params);
            jsonStr = HttpUtil.doGet(url);
        }
        try {
            TransResult transResult = json.readValue(jsonStr, TransResult.class);
            return transResult.getTrans_result().get(0).getDst();
        } catch (JsonProcessingException e) {
            return "翻译失败!" + jsonStr;
        }
    }

    /**
     * 构建请求参数
     *
     * @param query 翻译对象
     * @param from  对象语种
     * @param to    目标语种
     */
    private static Map<String, String> buildParams(String query, String from, String to) throws UnsupportedEncodingException {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appid);

        //随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        //签名
        StringBuilder stringBuilder = new StringBuilder();
        params.put(
                "sign",
                MD5.md5(stringBuilder
                        .append(appid)
                        .append(query)
                        .append(salt)
                        .append(securityKey)
                        .toString()));
        return params;
    }

    /**
     * 拼接url，使用get请求方式时，将请求地址和请求参数进行拼接
     *
     * @param url    请求地址
     * @param params 请求参数
     */
    private static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder(url);
        if (url.contains("?")) {
            stringBuilder.append("&");
        } else {
            stringBuilder.append("?");
        }
        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) {
                continue;
            }
            if (i != 0) {
                stringBuilder.append("&");
            }
            String encode = URLEncoder.encode(value, StandardCharsets.UTF_8);
            stringBuilder.append(key).append("=").append(encode);

            i++;
        }
        return stringBuilder.toString();
    }
}
