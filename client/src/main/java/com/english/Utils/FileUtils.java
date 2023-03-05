package com.english.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author XYC
 */
public class FileUtils {

    /**
     * 加载配置文件
     *
     * @param filePath 文件路径
     * @return Properties
     */
    public static Properties load(String filePath) {
        InputStream inputStream;
        try {
            inputStream = Files.newInputStream(Paths.get(filePath));
        } catch (IOException e) {
            inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filePath);
        }
        if (inputStream == null) {
            throw new RuntimeException("【ERROR】文件路径异常！filePath:" + filePath);
        }
        Properties properties;
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    /**
     * 读取 resources下的文件内容以 list 的方式返回
     * 注意：只能获取类路径下的资源
     * <p>
     * resources下的文件是存在于jar这个文件里面，在磁盘上是没有真实路径存在的，它是位于jar内部的一个路径。
     *
     * @param filePath resources下的文件路径
     * @return list 文件的每行数据
     */
    public static List<String> fileContentList(String filePath) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        FileUtils.class.getClassLoader().getResourceAsStream(filePath))))) {
            return reader.lines().toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取 key value 内容形式的文件的做映射
     *
     * @param filePath  resources下的文件路径
     * @param separator key value 分隔符
     * @return Map
     */
    public static Map<String, String> fileContentMapped(String filePath, String separator) {
        List<String> lines = fileContentList(filePath);
        if (lines != null) {
            Map<String, String> map = new HashMap<>(6);
            for (String line : lines) {
                // 将 每行（line） 分割为 key 和 value
                String[] kv = line.split(separator);
                map.put(kv[0], kv[1]);
            }
            return map;
        }
        return null;
    }
}
