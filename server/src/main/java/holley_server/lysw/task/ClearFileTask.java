package holley_server.lysw.task;

import java.util.Date;

import holley_server.lysw.util.CommonUtil;
import holley_server.lysw.util.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ClearFileTask {
	
    private static Log         logger = LogFactory.getLog(ClearFileTask.class.getName());
	
	public void execute(){
		//TODO
		logger.info("定时向服务器开始清除超过"+Config.File_Max_Exit_Time+"天的文件,当前时间:"+CommonUtil.DateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		CommonUtil.execute(new ClearFileJob(Config.Server_Log_Path
				,Config.Server_Store_Path,Config.Server_Send_Path,Config.File_Max_Exit_Time));
		
	}
}
