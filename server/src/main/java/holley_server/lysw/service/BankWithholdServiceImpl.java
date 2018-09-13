package holley_server.lysw.service;

import holley_server.lysw.model.BankReturnStatusEnum;
import holley_server.lysw.model.RequestPackage;
import holley_server.lysw.model.ResponsePackage;
import holley_server.lysw.model.TradeCodeEnum;
import holley_server.lysw.model.TradingPackage;
import holley_server.lysw.service_http.BankWithholdServiceHttp;
import holley_server.lysw.task.FileJob;
import holley_server.lysw.util.CommonUtil;
import holley_server.lysw.util.Config;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BankWithholdServiceImpl implements BankWithholdService {
	
	//有一个http服务类：  负责从自来水厂平台向云平台请求数据
	private  static Log         logger = LogFactory.getLog(BankWithholdServiceImpl.class.getName());
	
//	private BankWithholdServiceHttp bankWithholdServiceHttp=(BankWithholdServiceHttp) SpringSupport.springHandle.getBean("bankWithholdServiceHttp");
	private BankWithholdServiceHttp bankWithholdServiceHttp;
	
	public BankWithholdServiceHttp getBankWithholdServiceHttp() {
		return bankWithholdServiceHttp;
	}

	public void setBankWithholdServiceHttp(
			BankWithholdServiceHttp bankWithholdServiceHttp) {
		this.bankWithholdServiceHttp = bankWithholdServiceHttp;
	}

	//银行传送的信息写入到用户档案中，如果返回成功信息，则签约登记成功。如果返回出错信息，则签约失败
	public void userRegister(ChannelId channelId,RequestPackage rp) throws NotSerializableException, UnsupportedEncodingException {
	
		//判断关键字段不能为空(户号｜卡号｜交易日期)
		if(rp.getCardNumber().equals("")||rp.getUserAccount().equals("")||rp.getTradeDate()==null){
			TradingPackage tp = CommonUtil.getTradingPackage(rp, null);
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("993"));
			sendPackage(channelId,tp,"",0);
			return;
		}
		ResponsePackage rep=bankWithholdServiceHttp.requestRegisterUser(rp);
		TradingPackage tp = CommonUtil.getTradingPackage(rp, rep);
		if(rep==null){
			logger.info("http请求结果为空");
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
		}
		sendPackage(channelId,tp,"",0);
	}

	public void cancelRegister(ChannelId channelId,RequestPackage rp) throws NotSerializableException, UnsupportedEncodingException {
		//判断关键字段不能为空(户号｜卡号｜交易日期)
		if(rp.getCardNumber().equals("")||rp.getUserAccount().equals("")||rp.getTradeDate()==null){
			TradingPackage tp = CommonUtil.getTradingPackage(rp, null);
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("993"));
			sendPackage(channelId,tp,"",0);
			return;
		}
		ResponsePackage rep=bankWithholdServiceHttp.cancelRegister(rp);
		TradingPackage tp = CommonUtil.getTradingPackage(rp, rep);
		if(rep==null){
			logger.info("http请求结果为空");
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
		}
		sendPackage(channelId,tp,"",0);
	}

	public void sendWithheldFile(ChannelId channelId,
			RequestPackage rp) throws IOException {
		//判断关键字段不能为空(交易日期)
		if(rp.getTradeDate()==null){
			TradingPackage tp = CommonUtil.getTradingPackage(rp, null);
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("993"));
			sendPackage(channelId,tp,"",0);
			return;
		}
		
		//根据交易日期获得上个月的扣款文件名
		String lastMonth = CommonUtil.getLastMonth(CommonUtil.DateToStr(rp.getTradeDate(),"yyyy-MM-dd"));
		//modify by sc
		String path=Config.Server_Send_Path+Config.WithHold_FilePrefix+lastMonth;
		RandomAccessFile raf = null;
		long length = -1;  
		boolean flag=false;
		try {  
			raf = new RandomAccessFile(path, "r");  
			length = raf.length();  
		} catch (Exception e) {  
			logger.info(("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage() + '\n'));  
			flag=true;
			if(raf != null){
				raf.close();  
			}
		} finally {  
			if (length < 0) {  
				flag=true;
				logger.info("传输文件不存在，检查本地是否存在文件或文件名是否正确！");
				if(raf != null){
					raf.close();  
				}
			}  
		}  
		//发送的文件名  以文件前缀加当前请求日期
		String fileName = Config.WithHold_FilePrefix+CommonUtil.DateToStr(rp.getTradeDate(),"yyyyMMdd");
		TradingPackage tp = CommonUtil.getTradingPackage(rp, null);
		if(flag){
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
			sendPackage(channelId, tp,"",0);
			return;
		}else{
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("000"));
			sendPackage(channelId, tp,fileName,raf.length());
		}
		Channel channel =  CommonUtil.getChannel(channelId);
		channel.pipeline().addBefore("tcpHandler", "stringEncoder", new StringEncoder(Charset.forName(Config.Coding_Format)));
		logger.info("文件ok: " + raf.length());
		ChannelFuture fileFuture = channel.writeAndFlush(new DefaultFileRegion(raf.getChannel(), 0, length));
		final long l=length;
		final RandomAccessFile raf2 = raf;
		fileFuture.addListener(new ChannelFutureListener(){
			
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if(raf2 != null){
					raf2.close();
				}
				logger.info("待扣款文件写回成功，文件长度："+l);
				
			}});
		//让文件能有足够的时间被写回   xhx有没有方法能把raf.close()加到上面的监听器里?
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} 
		//raf.close();
	}
	

	public void receiveAndHandle(ChannelId channelId,RequestPackage rp,
			List<String> infoList) throws NotSerializableException, UnsupportedEncodingException {
		TradingPackage tp = CommonUtil.getTradingPackage(rp, null);
		if(infoList.size()==0){
			//没有接收到有用信息
			logger.info("结果文件无内容");
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
			sendPackage(channelId,tp,"",0);
		}else if(infoList.size()>0){
			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("000"));
			sendPackage(channelId,tp,"",0);
			logger.info("结果文件发送成功");
			CommonUtil.execute(new FileJob("txt",infoList));
		}
		
	}
	
	public void sendPackage( ChannelId channelId, TradingPackage tp,String fileName,long fileLength)
			throws UnsupportedEncodingException, NotSerializableException {
		final Channel channel = CommonUtil.getChannel(channelId);
		//生成返回交易码
		final String returnStr = CommonUtil.getReturnStr(tp);
		//生成包头
		final String packHead=CommonUtil.packMsg(Config.Execute_Code,returnStr.getBytes(Config.Coding_Format).length,fileName,fileLength);
		    	
		ChannelFuture cf1 = channel.writeAndFlush(CommonUtil.sendString(packHead));
    	cf1.addListener(new ChannelFutureListener(){
    		public void operationComplete(ChannelFuture future)
    				throws Exception {
    			logger.info(" 发送 <"+channel.id()+">"+packHead);
    			ChannelFuture cf2= channel.writeAndFlush(CommonUtil.sendString(returnStr));
    			cf2.addListener(new ChannelFutureListener(){
    				public void operationComplete(ChannelFuture future)
    						throws Exception {
    					logger.info(" 发送 <"+channel.id()+">"+returnStr);
    				}});
    		}});
    	//留有时间让包头与结构包发送   防止后面的发送任务先于此执行xhx（文件传输时需要？）
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
