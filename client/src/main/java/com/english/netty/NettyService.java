package com.english.netty;

import com.english.Utils.FileUtils;
import com.english.netty.handler.ConnectHandler;
import com.english.netty.handler.MessageContentHandler;
import com.english.netty.handler.MessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.CyclicBarrier;

/**
 * @author XYC
 */
@Data
public abstract class NettyService {
    public static final NioEventLoopGroup GROUP = new NioEventLoopGroup();
    protected static final Bootstrap BOOTSTRAP = new Bootstrap().group(GROUP).channel(NioSocketChannel.class);
    protected static ChannelFuture channelFuture;
    protected static Channel channel;

    protected static final String HOST;
    protected static final Integer PORT;
    /**
     * 请求消息管理器
     */
    public static final MessageManager MESSAGE_MANAGER = new MessageManager();
    /**
     * 响应消息的主要内容处理器
     */
    protected static MessageContentHandler messageContentHandler = new MessageContentHandler();
    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyService.class);
    /**
     * 用来同步 service 获取 服务端响应的经过处理后的数据
     * 1、客户端 service 向服务端发送数据请求后，将调用 cyclicBarrier#await() 进入阻塞
     * 2、服务端响应的消息在会通过 MessageContentHandler#channelRead处理主要的消息内容
     * 3、将处理好的主要的内容setMsgContent()中，然后调用 cyclicBarrier#await() 让 service 解除阻塞状态
     */
    public static final CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> {
        LOGGER.info("MessageContentHandler#getMsgContent()");
    });

    static {
        Properties properties = FileUtils.load("client.properties");
        HOST = properties.getProperty("host");
        PORT = Integer.valueOf(properties.getProperty("port"));
        BOOTSTRAP.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(16 * 1024, 0, 4, 0, 4),
                                new StringEncoder(StandardCharsets.UTF_8),
                                new StringDecoder(StandardCharsets.UTF_8),
                                new LoggingHandler(LogLevel.INFO),
                                new IdleStateHandler(0, 600, 0),
                                new MessageHandler(),
                                messageContentHandler,
                                new ConnectHandler());
            }
        });
        connect();
    }

    public static boolean connect() {
        try {
            channelFuture = BOOTSTRAP.connect(HOST, PORT);
            channel = channelFuture.channel();
            if (channel != null && channel.isActive()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void close() {
        GROUP.shutdownGracefully();
        channel.close();
    }

    public static void sendMessage() {
        channel.writeAndFlush(MESSAGE_MANAGER.getMessageMap());
    }
}
