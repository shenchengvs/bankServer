package holley_server.lysw.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.IOException;

public class TcpClientServer {

    private int            port;
    private String         host;
    private EventLoopGroup group = new NioEventLoopGroup();
    private Channel        channel;
    File                   file;

    public TcpClientServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void openClient() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, true)
        .handler(new LoggingHandler(LogLevel.INFO))
        .handler(new TcpClientChannelInitializer());

        try {
            ChannelFuture future = bootstrap.connect(host, port);
            channel = future.channel();
            future.sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
