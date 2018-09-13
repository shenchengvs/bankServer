package holley_server.lysw.server;

import net.sf.json.JSONObject;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import holley_server.lysw.model.RetTypeEnum;
import holley_server.lysw.util.Config;
import holley_server.lysw.util.HttpRequestUtil;
import holley_server.lysw.util.JarUtil;

public class MainServer {

	private  static Log  logger = LogFactory.getLog(MainServer.class.getName());
	
    public static void main(String[] args) {

    	MainServer server = new MainServer();
        server.init();
        server.start();
        
    }

	private void init() {
		 JarUtil ju = new JarUtil(MainServer.class);
	     PropertyConfigurator.configure(ju.getJarPath() + "/config"  + "/log4j.properties");
	     // 初始化spring
        if (SpringSupport.springHandle == null) {
            SpringSupport.initHandle();
        }
        //初始化token
	   try {
 			//Config.Local_Token;
 			JSONObject tt = HttpRequestUtil.queryToken();
 			RetTypeEnum retResult = HttpRequestUtil.getRetResult(tt);
 			if(retResult==RetTypeEnum.SUCCESS){
 				JSONObject decodeData = HttpRequestUtil.getDecodeData(tt);
 				Config.Local_Token=decodeData.get("accessToken")==null?"":(String) decodeData.get("accessToken");
 				logger.info("token初始化成功:"+Config.Local_Token);
 			}
 			
 		} catch (Exception e) {
 			logger.info("token初始化失败");
 		}
    }
	
	private void start() {
		new TcpServer(Config.Server_Host,Config.Server_Port).openDev();
		
	}
}
