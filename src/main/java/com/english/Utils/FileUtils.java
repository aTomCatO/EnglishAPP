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

    public static Properties load(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(Paths.get(filePath));
        } catch (IOException e) {
            inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(filePath);
        }
        if (inputStream == null) {
            throw new RuntimeException("文件读取异常！filePath:" + filePath);
        }
        Properties properties = null;
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
