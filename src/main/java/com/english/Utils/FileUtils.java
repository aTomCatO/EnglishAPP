package com.english.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

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
            inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(filePath);
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
}
