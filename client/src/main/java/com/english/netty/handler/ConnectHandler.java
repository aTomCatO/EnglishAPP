package com.english.netty.handler;

import com.english.netty.NettyService;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XYC
 * 客户端连接处理器
 */
public class ConnectHandler extends ChannelDuplexHandler {
    private final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        logger.info("【channelInactive】10秒后开始重新建立连接");
        EventLoop eventLoop = context.channel().eventLoop();
        eventLoop.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    for (int i = 0; i < 10; i++) {
                        if (NettyService.connect()) {
                            logger.info("建立连接成功");
                            break;
                        }
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 搭配 IdleStateHandler 使用
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object event) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent) event;
        if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
            context.channel().writeAndFlush("客户端心跳机制触发");
        }
    }
}
