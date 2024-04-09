package com.english.baidutrans;

import com.english.util.InstanceUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    private static String appid = "20220323001136339";
    private static String securityKey = "X_wLubR1NLEq2QAgEWdM";

    private static ObjectMapper json = InstanceUtils.JSON;

    /**
     * 获得翻译结果
     *
     * @param query
     * @param from
     * @param to
     * @return
     * @throws IOException
     */
    public static String getTransResult(String query, String from, String to) throws IOException {
        Map<String, String> params = buildParams(query, from, to);
        String jsonStr;
        //当请求翻译内容过长时,用post请求方式
        if (query.length() >= 2000) {
            jsonStr = HttpUtil.doPostStr(TRANS_API_HOST, params);
        } else {
            String url = getUrlWithQueryString(TRANS_API_HOST, params);
            jsonStr = HttpUtil.doGetStr(url);
        }
        try {
            TransResult transResult = json.readValue(jsonStr, TransResult.class);
            return transResult.getTrans_result().get(0).getDst();
        } catch (JsonProcessingException e) {
            return "翻译失败!" + jsonStr;
        }

    }

    /**
     * 构建参数map
     *
     * @param query
     * @param from
     * @param to
     * @return
     * @throws UnsupportedEncodingException
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
     * 拼接url get方式拼接参数  返回url
     *
     * @param url
     * @param params
     * @return
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
            stringBuilder.append(key).append("=").append(encode(value));

            i++;
        }
        return stringBuilder.toString();
    }

    /**
     * 对输入的字符串进行URL编码
     *
     * @param input 原文
     * @return URL编码.如果编码失败, 则返回原文
     */
    private static String encode(String input) {
        if (input == null) {
            return "";
        }
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return input;
    }
}
