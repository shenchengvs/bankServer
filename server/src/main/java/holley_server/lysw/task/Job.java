package holley_server.lysw.task;


import holley_server.lysw.model.BankReturnStatusEnum;


import holley_server.lysw.model.RequestPackage;
import holley_server.lysw.model.TradeCodeEnum;
import holley_server.lysw.model.TradingPackage;
import holley_server.lysw.service.BankWithholdService;
import holley_server.lysw.util.CommonUtil;
import io.netty.channel.ChannelId;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Job implements Runnable{
	private  static Log  logger = LogFactory.getLog(Job.class.getName());
	
	private BankWithholdService bankWithholdService;
	RequestPackage rp;
	private ChannelId channelId;
	//用于判断是否是文件传输   
	private boolean flag;
	private List<String> infoList;
	public Job(BankWithholdService bankWithholdService,RequestPackage rp,
			ChannelId channelId,boolean flag,List<String> infoList){
		this.bankWithholdService = bankWithholdService;
		this.rp=rp;
		this.channelId=channelId;
		this.infoList = infoList;
		this.flag=flag;
	}

	public void run() {
		 //将接收到的文件传给云平台
		 if(flag){
			 logger.info("传送扣款结果文件交易正在进行。。。");
			 try {
				bankWithholdService.receiveAndHandle(channelId, rp,infoList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("传送扣款结果文件交易失败");
			}
		}else if(rp.getTradeCode()==TradeCodeEnum.REGISTER){
         	
			 logger.info("代扣用户登记交易正在进行。。。");
         	try {
				bankWithholdService.userRegister(channelId,rp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("登记交易失败");
			} 
         	
         }else if(rp.getTradeCode()==TradeCodeEnum.CANCEL){
        	 logger.info("撤消登记交易正在进行。。。");
         	try {
				bankWithholdService.cancelRegister(channelId,rp);
			} catch (Exception e) {
				logger.info("撤消登记失败");
			}
         	
         }else if(rp.getTradeCode()==TradeCodeEnum.FILE_WITHHOLD){
        	 logger.info("批量扣款交易步骤之一，传送待扣款文件交易正在进行。。。");
         	try {
				bankWithholdService.sendWithheldFile(channelId,rp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("批量扣款交易步骤之一，传送待扣款文件交易正在进行");
			}
         	
         }else{
        	 logger.info("非法交易码");
        	 //非法交易码
     		TradingPackage tp = CommonUtil.getTradingPackage(rp, null);
        	tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("992"));
        	try {
				bankWithholdService.sendPackage(channelId, tp, "",0);
			} catch (NotSerializableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
	
	}

}
