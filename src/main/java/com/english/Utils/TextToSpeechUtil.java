package com.english.Utils;

import java.io.IOException;

/**
 * @author XYC
 * TTS文本转语音工具
 */
public class TextToSpeechUtil {

    public static boolean toSpeech(String text, String audioFilePath) {
        if (StringUtils.hasText(text) && StringUtils.hasText(audioFilePath) && audioFilePath.endsWith(".mp3")) {
            // 构建并执行edge-tts命令
            try {
                String command = "edge-tts --voice zh-CN-XiaoyiNeural --text \"" + text + "\" --write-media " + audioFilePath;

                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();

                // 检查命令是否成功执行
                if (process.exitValue() == 0) {
                    InstanceUtils.LOGGER.info("Edge-TTS successfully executed command \"{}\", created audio file \"{}\"", command, audioFilePath);
                    return true;
                }
            } catch (IOException | InterruptedException e) {
                InstanceUtils.LOGGER.error(e.getMessage());
            }
        }
        return false;
    }
}
