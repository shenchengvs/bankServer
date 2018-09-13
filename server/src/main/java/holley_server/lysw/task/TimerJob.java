package holley_server.lysw.task;

import java.io.NotSerializableException;
import java.io.UnsupportedEncodingException;

import holley_server.lysw.model.BankReturnStatusEnum;
import holley_server.lysw.model.RequestPackage;
import holley_server.lysw.model.TradingPackage;
import holley_server.lysw.service.BankWithholdService;
import holley_server.lysw.util.CommonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

public class TimerJob implements Runnable {

	private BankWithholdService bankWithholdService;
	private RequestPackage rp;
	private ChannelId channelId;
	private int time;
	
	public TimerJob(BankWithholdService bankWithholdService,RequestPackage rp, ChannelId channelId,int time) {
		super();
		this.bankWithholdService=bankWithholdService;
		this.rp=rp;
		this.channelId = channelId;
		this.time = time;
	}

	public void run() {
		// TODO Auto-generated method stub
		long count=time;
		Channel channel = CommonUtil.getChannel(channelId);
		while(count-->0){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!channel.isActive()){
				return;
			}
		}
		if(channel.isActive()){
			 TradingPackage tp = CommonUtil.getTradingPackage(rp, null);
			 tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
			 try {
				bankWithholdService.sendPackage(channelId, tp,"",0);
			} catch (NotSerializableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			channel.close();
		}
	}

}
