package com.english.netty.handler;

import com.english.util.InstanceUtils;
import com.english.netty.NettyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

/**
 * @author XYC
 * 响应消息的主要内容处理器
 */

@ChannelHandler.Sharable
public class MessageContentHandler extends ChannelInboundHandlerAdapter {
    /**
     * 响应消息的主要内容
     */
    private String msgContent;
    public final ObjectMapper json = InstanceUtils.JSON;

    private void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public <T> T getMsgContent(TypeReference<T> typeReference) {
        T readValue;
        try {
            readValue = json.readValue(msgContent, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return readValue;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        Map<String, String> msgMap = (Map<String, String>) msg;
        if (NettyService.cyclicBarrier.getNumberWaiting() == 1) {
            String msgContent = msgMap.get("msgContent");
            setMsgContent(msgContent);
            NettyService.cyclicBarrier.await();
        }
        super.channelRead(context, msg);
    }
}
