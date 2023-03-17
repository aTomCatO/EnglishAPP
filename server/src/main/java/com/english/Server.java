package com.english;

import com.english.Utils.FileUtils;
import com.english.netty.handler.ClientConnectHandler;
import com.english.netty.handler.MessageContentHandler;
import com.english.netty.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author XYC
 */
public class Server {
    public static final NioEventLoopGroup BOSS_GROUP = new NioEventLoopGroup();
    public static final NioEventLoopGroup WORKER_GROUP = new NioEventLoopGroup();
    // public static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final ServerBootstrap SERVER_BOOTSTRAP =
            new ServerBootstrap()
                    .group(BOSS_GROUP, WORKER_GROUP)
                    .channel(NioServerSocketChannel.class);
    private static final String HOST;
    private static final Integer PORT;

    static {
        Properties properties = FileUtils.load("server.properties");
        HOST = properties.getProperty("host");
        PORT = Integer.valueOf(properties.getProperty("port"));
    }

    public static void main(String[] args) {
        SERVER_BOOTSTRAP.childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(
                                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4),
                                new StringEncoder(StandardCharsets.UTF_8),
                                new StringDecoder(StandardCharsets.UTF_8),
                                new LoggingHandler(LogLevel.INFO),
                                new IdleStateHandler(1200, 0, 0),
                                new MessageHandler(),
                                new ClientConnectHandler(),
                                new MessageContentHandler()
                        );
                    }
                })
                .bind(HOST, PORT);
    }
}
