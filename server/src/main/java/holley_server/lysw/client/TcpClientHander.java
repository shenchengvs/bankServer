package holley_server.lysw.client;

import java.io.UnsupportedEncodingException;

import holley_server.lysw.util.CommonUtil;
import holley_server.lysw.util.Config;
import holley_server.lysw.util.MsgToPackageUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class TcpClientHander extends ChannelInboundHandlerAdapter {

	private int times;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("clientChannelActive");
        ctx.channel().write(CommonUtil.sendString("SDB"));
        ctx.channel().flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("clientChannelInactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	String strMsg = CommonUtil.getString(msg);
    	try {
    	if(strMsg.equalsIgnoreCase("ADR")){
    		System.out.println("client：第"+times+"次收到的数据："+strMsg);
    		
    		String returnStr="07021||不同异常的情况||6217|||||||||||||||2018-04-28|||ABC||";
    		String head=CommonUtil.packMsg(Config.Execute_Code,returnStr.getBytes(Config.Coding_Format).length,"",0);
    		//发结构包
//    		ctx.writeAndFlush(CommonUtil.sendString(head));
            //Thread.sleep(1000);
            ChannelFuture cf = ctx.writeAndFlush(CommonUtil.sendString(head+returnStr));
            times=1;
        	
        	cf.addListener(new ChannelFutureListener(){

				public void operationComplete(ChannelFuture future)
						throws Exception {
					// TODO Auto-generated method stub
					System.out.println("client：登记交易请求发送成功");
					
				}});
    	}else if(times==1){
    		System.out.println("client:收到的结构包"+strMsg);
    		MsgToPackageUtil mtp=CommonUtil.createMsgToPackage(strMsg);
    		times=2;
    	}else if(times==2){
    		System.out.println("client:收到的最终数据"+strMsg);
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
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client Read Complete："+times);
        ctx.flush();
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
    	String returnStr="07021||不同异常的情况||6217|||||||||||||||2018-04-28|||ABC||";
    	System.out.println(returnStr.length());
    	System.out.println("07021|991|不同异常的情况||6217|||||||||||||||2018-04-28|||ABC|8002|".getBytes("GBK").length);
	}
}
