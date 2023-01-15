package com.english.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XYC
 */
public class StringUtils {
    public static String substringRange(String str, String range) {
        String regex1 = "\\[\\d,\\d\\]";
        String regex2 = "\\[\\d,\\d\\)";
        String regex3 = "\\(\\d,\\d\\]";
        String regex4 = "\\(\\d,\\d\\)";

        int d1 = Character.getNumericValue(range.charAt(1));
        int d2 = Character.getNumericValue(range.charAt(3));
        int max = str.length();

        //范围约束
        if (d1 < 1 || d1 > d2 || d2 > max) {
            throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
        }
        String result = "";
        if (range.matches(regex1)) {
            if (d1 == 1 && d2 == max) {
                //如 str="123456" range="[1,6]" 截取后结果仍为 "123456" （无意义）
                throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
            }
            result = str.substring(d1 - 1, d2);
        } else if (range.matches(regex2)) {
            if (d1 - 1 == d2 - 1) {
                //如 [2,2) 既要包含，又要不包含（前后矛盾）
                throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
            }
            result = str.substring(d1 - 1, d2 - 1);
        } else if (range.matches(regex3)) {
            if (d1 == d2) {
                //如 (2,2] 既要不包含，又要包含（前后矛盾）
                throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
            }
            result = str.substring(d1, d2);
        } else if (range.matches(regex4)) {
            if (d1 == d2 - 1) {
                //如 (2,2) 截取后结果为 “” （无意义）
                throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
            }
            result = str.substring(d1, d2 - 1);
        } else {
            throw new IllegalArgumentException("【ERROR】非法区间符号：" + range);
        }
        return result;
    }

    public static String deleteRange(String str, String range) {
        String regex1 = "\\[\\d,\\d\\]";
        String regex2 = "\\[\\d,\\d\\)";
        String regex3 = "\\(\\d,\\d\\]";
        String regex4 = "\\(\\d,\\d\\)";

        int d1 = Character.getNumericValue(range.charAt(1));
        int d2 = Character.getNumericValue(range.charAt(3));
        int max = str.length();
        //范围约束
        if (d1 < 1 || d1 > d2 || d2 > max) {
            throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
        }
        StringBuilder sb = new StringBuilder(str);
        String residue = "";
        if (range.matches(regex1)) {
            if (d1 == 1 && d2 == max) {
                //如 str="123456" range="[1,6]" 删除后结果为 "" （无意义）
                throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
            }
            residue = sb.delete(d1 - 1, d2).toString();
        } else if (range.matches(regex2)) {
            if (d1 - 1 == d2 - 1) {
                //如 [2,2) 既要包含，又要不包含（前后矛盾）
                throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
            }
            residue = sb.delete(d1 - 1, d2 - 1).toString();
        } else if (range.matches(regex3)) {
            if (d1 == d2) {
                //如 (2,2] 既要不包含，又要包含（前后矛盾）
                throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
            }
            residue = sb.delete(d1, d2).toString();
        } else if (range.matches(regex4)) {
            if (d1 == d2 - 1) {
                //如 (2,2) 没有删除任何字符 （无意义）
                throw new IllegalArgumentException("【ERROR】非法区间范围：" + range);
            }
            residue = sb.delete(d1, d2 - 1).toString();
        } else {
            throw new IllegalArgumentException("【ERROR】非法区间符号：" + range);
        }
        return residue;
    }

    /**
     * 获取字符串中的每个字符出现的次数 入口
     * 去除标点符号和空格
     */
    public HashMap<String, Integer> getCharNumEntrance(String str) {
        String newStr = str.replaceAll("[\\pP\\p{Punct}\\s*]", "");
        return getCharNum(newStr.toLowerCase(Locale.ENGLISH));
    }

    /**
     * 获取字符串中的每个字符出现的次数 实现
     */
    private HashMap<String, Integer> getCharNum(String str) {
        HashMap<String, Integer> map = new HashMap<>(16);
        if (str.length() > 1) {
            String firstChar = String.valueOf(str.charAt(0));
            Pattern compile = Pattern.compile(firstChar);
            Matcher matcher = compile.matcher(str);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            map.put(firstChar, count);
            String newStr = str.replaceAll(firstChar, "");
            map.putAll(getCharNum(newStr));
        } else if (str.length() == 1) {
            map.put(str, 1);
        }
        return map;
    }
}
