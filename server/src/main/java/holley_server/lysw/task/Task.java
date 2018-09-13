package holley_server.lysw.task;

import java.util.Date;

import holley_server.lysw.util.CommonUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Task {
	
    private static Log         logger = LogFactory.getLog(Task.class.getName());
	
	public void execute(){
		//TODO
		logger.info("定时向服务器请求上个月账单,当前时间:"+CommonUtil.DateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		CommonUtil.execute(new FileJob("file",null));
	}
	
}
