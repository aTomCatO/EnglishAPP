package com.english.util;

import java.io.IOException;

/**
 * @author XYC
 * TTS文本转语音工具
 */
public class TextToSpeechUtil {

    public static boolean toSpeech(String text, String audioFilePath) {
        if (StringUtil.hasText(text) && StringUtil.hasText(audioFilePath) && audioFilePath.endsWith(".mp3")) {
            // 构建并执行edge-tts命令
            try {
                // 创建命令及其参数
                ProcessBuilder processBuilder = new ProcessBuilder(
                        "edge-tts",
                        "--voice", "zh-CN-XiaoyiNeural",
                        "--text", text,
                        "--write-media", audioFilePath
                );
                Process process = processBuilder.start();
                process.waitFor();
                // 检查命令是否成功执行
                if (process.exitValue() == 0) {
                    InstanceUtil.LOGGER.info("Edge-TTS successfully created audio file \"{}\"", audioFilePath);
                    return true;
                }
            } catch (IOException | InterruptedException e) {
                InstanceUtil.LOGGER.error(e.getMessage());
            }
        }
        return false;
    }
}
