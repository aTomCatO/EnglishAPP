package com.english.netty.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XYC
 * 第一层消息处理器
 */
public class MessageHandler extends ChannelDuplexHandler {
    private final ObjectMapper json = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        Map<String, String> msgMap;
        try {
            msgMap = json.readValue(msg.toString(), new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            msgMap = new HashMap<>();
            msgMap.put("msgContent", msg.toString());
        }
        logger.info("read msg => {}", msgMap);
        super.channelRead(context, msgMap);
    }

    /***
     * 该方法配合 LengthFieldBasedFrameDecoder 解决接收消息的粘包和半包问题
     * new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4)
     * maxFrameLength      可以接收的最大范围的字节数（超过此长度的部分将当作垃圾包丢弃）
     * lengthFieldOffset   内容长度（字段）的偏移量，表示偏移多少个字节后才是 内容长度
     * lengthFieldLength   记录 内容长度（字段）的 空间 所占的字节数（它不是指内容长度有多少个字节，而是指用多少个字节的空间来记录 内容长度 字段。在上面示例中，第3个参数4就是该空间占有的字节数）
     * lengthAdjustment    内容长度（字段）后距离几个字节才是具体的消息内容
     * initialBytesToStrip 从头开始剥离几个字节
     *
     * （当 lengthFieldOffset 为 0 且 lengthAdjustment 为 0 时（长度字段后接着的是具体的消息内容时），
     *   则 initialBytesToStrip 的值则为 lengthFieldLength，
     *   表示从头开始剥离 记录内容长度（字段）的空间占有的字节数后，剩下的就是具体的消息内容）
     */
    @Override
    public void write(ChannelHandlerContext context, Object msg, ChannelPromise promise) throws Exception {
        String message;
        if (msg instanceof String) {
            message = msg.toString();
        } else {
            message = json.writeValueAsString(msg);
        }
        logger.info("write msg => {}  /  length => {}", message, message.length());
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        // 先向缓冲区中写入 内容长度
        buffer.writeInt(bytes.length);
        // 再向缓冲区中写内容
        buffer.writeBytes(bytes);
        super.write(context, buffer, promise);
    }
}
