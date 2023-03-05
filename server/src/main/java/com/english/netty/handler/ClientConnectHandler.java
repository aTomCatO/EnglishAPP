package com.english.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XYC
 * 服务端处理客户端连接的处理器
 */
public class ClientConnectHandler extends ChannelDuplexHandler {
    private final Logger logger = LoggerFactory.getLogger(ClientConnectHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object event) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent) event;
        if (idleStateEvent.state() == IdleState.READER_IDLE) {
            logger.info("客户端已掉线");
            context.channel().close();
        }
        super.userEventTriggered(context, event);
    }
}
