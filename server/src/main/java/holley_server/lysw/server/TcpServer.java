package holley_server.lysw.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TcpServer {

	private String 	host;
    private int            port;
    private EventLoopGroup bossGroup   = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public TcpServer(String host,int port) {
    	this.host=host;
        this.port = port;
    }
    public void openDev() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
        .option(ChannelOption.SO_BACKLOG, 128)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new TcpChannelInitializer());
        ChannelFuture server = null;
        try {
            server = b.bind(host,port).sync();
            server.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
