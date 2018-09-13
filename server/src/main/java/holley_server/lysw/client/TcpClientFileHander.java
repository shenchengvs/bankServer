package holley_server.lysw.client;

import holley_server.lysw.util.CommonUtil;
import holley_server.lysw.util.Config;
import holley_server.lysw.util.MsgToPackageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class TcpClientFileHander extends ChannelInboundHandlerAdapter {

	private OutputStreamWriter writer;
	private BufferedWriter bw;
	private int times;
	//存放文件信息
	private MsgToPackageUtil mtp;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("clientChannelActive");
        ctx.channel().write(CommonUtil.sendString("SDB"));
        ctx.channel().flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("clientChannelInactive");
        if(bw!=null)
        	bw.flush();
    		bw.close();
    	if(writer!=null)
    		writer.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	try {
    	    if(times==3){
        		System.out.println("client:收到的文件2"+msg);
         		String str=(String)msg+"\r\n";
                System.out.println("本次接收内容长度：" + str.toString().length());
                try {
                    bw.write(str);
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        	}else{
    		String strMsg = CommonUtil.getString(msg);
    		
    	if(strMsg.equalsIgnoreCase("ADR")){
    		times=1;
    		System.out.println("client第"+times+"次收到的数据："+strMsg);
    		String returnStr="07024|||||||||||||||||||2018-07-29|||ABC||";
    		String head=CommonUtil.packMsg(Config.Execute_Code,returnStr.getBytes(Config.Coding_Format).length,"",0);
    		//发送包头
    		ctx.write(CommonUtil.sendString(head));
            ChannelFuture cf = ctx.writeAndFlush(CommonUtil.sendString(returnStr));
        	
        	cf.addListener(new ChannelFutureListener(){

				public void operationComplete(ChannelFuture future)
						throws Exception {
					System.out.println("等待传输待扣款文件。。。");
					
				}});
        	
    	}else if(times==1){
    		mtp=CommonUtil.createMsgToPackage(strMsg);
    		System.out.println("client:收到的结构包"+strMsg);
    		times=2;
    		ctx.pipeline().addFirst("lengthDecoder3",new FixedLengthFrameDecoder((int) mtp.getSlen()));
    	}else if(times==2){
    		System.out.println("client:收到的结构包"+strMsg);
    		//如果先stringDecoder编码  两个都不会进？？？
    		ctx.pipeline().addBefore("tcpClient", "lineDecoder",  new LineBasedFrameDecoder(8192));
    		ctx.pipeline().addBefore("tcpClient", "stringDecoder", new StringDecoder());
            System.out.println("创建文件");
            File file = new File("F:\\data\\receive\\"+mtp.getFname().trim());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
            	writer = new OutputStreamWriter(new FileOutputStream(file), Config.Coding_Format);
                bw = new BufferedWriter(writer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    		times=3;
    	}
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * exceptionCaught() 事件处理方法是当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时。在大部分情况下，捕获的异常应该被记录下来 并且把关联的
         * channel 给关闭掉。然而这个方法的处理方式会在遇到不同异常的情况下有不 同的实现，比如你可能想在关闭连接之前发送一个错误码的响应消息。
         */
        // 出现异常就关闭
        cause.printStackTrace();
        if(bw!=null)
    		bw.close();
    	if(writer!=null)
    		writer.close();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client Read Complete："+times);
        ctx.flush();
    }
}
