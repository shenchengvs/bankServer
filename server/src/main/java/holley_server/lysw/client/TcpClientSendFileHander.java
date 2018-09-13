package holley_server.lysw.client;

import holley_server.lysw.model.TradeCodeEnum;
import holley_server.lysw.model.TradingPackage;
import holley_server.lysw.service.BankWithholdServiceImpl;
import holley_server.lysw.util.CommonUtil;
import holley_server.lysw.util.Config;
import holley_server.lysw.util.MsgToPackageUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.ReferenceCountUtil;

import java.io.NotSerializableException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;

public class TcpClientSendFileHander extends ChannelInboundHandlerAdapter {

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
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	try {
    		String strMsg = CommonUtil.getString(msg);
    		
    	if(strMsg.equalsIgnoreCase("ADR")){
    		times=1;
    		System.out.println("client:第"+times+"次收到的数据："+strMsg);
    		TradingPackage tp=new TradingPackage();
    		tp.setTradeCode(TradeCodeEnum.getEnmuByValue("07025"));
    		tp.setPayDate(new Date());
    		tp.setBankCode("ABCabc");
    	/*	//发送包头
    		ctx.writeAndFlush(CommonUtil.sendString("0000010000000021           send22222.txt0000000129"));
            //Thread.sleep(1000);
            ChannelFuture cf = ctx.writeAndFlush(CommonUtil.sendString("07025|2018-04-18|ABC|"));
            */
            String path="F:\\data\\send\\FHJHSF_201805";
       	    RandomAccessFile raf = null;  
            long length = -1;  
            try {  
                raf = new RandomAccessFile(path, "r");  
                length = raf.length();  
                System.out.println("文件路长度："+length);
            } catch (Exception e) {  
            	System.out.println("client：文件路径不存在");
                return;  
            } finally {  
                if (length < 0 && raf != null) {  
                    raf.close();  
                    return ;
                }  
            }  
            sendPackage(ctx, tp, "FHJHSF_201804", length);
            ctx.pipeline().addBefore("tcpClient", "stringEncoder", new StringEncoder(Charset.forName(Config.Coding_Format)));
            //Thread.sleep(1000);
            ChannelFuture fileFuture = ctx.writeAndFlush(new DefaultFileRegion(raf.getChannel(), 0, length));
            final long l=length;
            fileFuture.addListener(new ChannelFutureListener(){

				public void operationComplete(ChannelFuture future)
						throws Exception {
					// TODO Auto-generated method stub
					System.out.println("client:扣款文件发送成功，文件长度："+l);
				}});
        
    	}else if(times==1){
    		mtp=CommonUtil.createMsgToPackage(strMsg);
    		times=2;
    	}else if(times==2){
    		System.out.println("client:收到的结构包"+strMsg);
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
    public void sendPackage( final ChannelHandlerContext ctx, TradingPackage tp,String fileName,long fileLength)
			throws UnsupportedEncodingException, NotSerializableException {
		//生成返回交易码
		final String returnStr = CommonUtil.getReturnStr(tp);
		//生成包头
		final String packHead=CommonUtil.packMsg(Config.Execute_Code,returnStr.length(),fileName,fileLength);
		    	
		ChannelFuture cf1 = ctx.writeAndFlush(CommonUtil.sendString(packHead));
    	cf1.addListener(new ChannelFutureListener(){
    		public void operationComplete(ChannelFuture future)
    				throws Exception {
    			// TODO Auto-generated method stub
    			System.out.println(" 发送 <"+ctx.channel().id()+">"+packHead);
    			ChannelFuture cf2= ctx.channel().writeAndFlush(CommonUtil.sendString(returnStr));
    			cf2.addListener(new ChannelFutureListener(){
    				public void operationComplete(ChannelFuture future)
    						throws Exception {
    					// TODO Auto-generated method stub
    					System.out.println(" 发送 <"+ctx.channel().id()+">"+returnStr);
    				}});
    		}});
    	//留有时间让包头与结构包发送   防止后面的发送任务先于此执行xhx（文件传输时需要？）
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
