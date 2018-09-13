package holley_server.lysw.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

public class Config {
	
	//服务器ip地址
	public static String Server_Host;
	
	//服务器端口号
	public static int Server_Port;

	//用于定义向客户端发送文件的父级目录
	public  static  String Server_Send_Path;
	
	//用于定义接受客户端文件的父级目录
	public  static  String Server_Store_Path;
	
	//用于定义日志文件的父级目录
	public  static  String Server_Log_Path;
	
	//待扣款文件名前缀
	public static String WithHold_FilePrefix="FHJHSF_";
	
	//自来水公司代码
	public static final String Water_Company_Code="8002";
	
	//执行码
	public static final String Execute_Code="000001";
	
	//本地token
	public static  String Local_Token;
	
	//客户端与服务器文件传输时所用的编码格式（不包括单纯的结构交易的字符编码）
	public static String Coding_Format="GBK";
	
	//服务端接收文件数据最大允许时间（单位：秒）
	public static int Max_Time;
	
	//服务端文件最大存放时间（单位：日）
	public static long File_Max_Exit_Time;
	static {
		// 从配置文件中得到字段值
		InputStream inStream = HttpRequestUtil.class.getClassLoader()
				.getResourceAsStream("openActive.properties");
		Properties prop = new Properties();
		try {
			prop.load(inStream);
			Server_Host = prop.getProperty("Server_Host");
			Server_Port = Integer.valueOf(prop.getProperty("Server_Port"));
			Server_Send_Path = prop.getProperty("Server_Send_Path");
			Server_Store_Path = prop.getProperty("Server_Store_Path");
			Server_Log_Path = prop.getProperty("Server_Log_Path");
			Max_Time = Integer.valueOf(prop.getProperty("Max_Time"));
			File_Max_Exit_Time = Long.valueOf(prop.getProperty("File_Max_Exit_Time"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
