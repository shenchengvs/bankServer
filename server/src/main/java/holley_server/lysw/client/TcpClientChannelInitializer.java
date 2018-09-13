package holley_server.lysw.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class TcpClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        /*用于   交易代扣用户登记交易（07021）和（撤消登记交易（07022））测试*/
     //  p.addLast("tcpClient",new TcpClientHander());
        
        /*用于   批量扣款交易步骤之一，传送待扣款文件（07024）测试*/
       // p.addLast("tcpClient",new TcpClientFileHander());
        
        /*用于   批量扣款交易步骤之二，传送扣款结果文件(07025) 测试*/
        p.addLast("tcpClient",new TcpClientSendFileHander());

    }
}
