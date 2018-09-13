package holley_server.lysw.task;

import holley_server.lysw.model.RetTypeEnum;


import holley_server.lysw.util.CommonUtil;
import holley_server.lysw.util.Config;
import holley_server.lysw.util.HttpRequestUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileJob implements Runnable {
	private static Log         logger = LogFactory.getLog(FileJob.class.getName());
	private BufferedWriter bw;
	private OutputStreamWriter writer;
	private String type;
	private List<String> infoList;
	public FileJob(String type,List<String> infoList){
		this.type=type;
		this.infoList=infoList;
	}

	@SuppressWarnings("deprecation")
	public void run() {
		// TODO Auto-generated method stub
		JSONObject data;
		RetTypeEnum retResult = null;
		int time = 0;
		try {
			while (true) {
				if (type.equals("file")) {
					logger.info("正在请求账单。。。");
					/*判断文件是否存在*/
					// 把当前日期上个月Config.withhold_file_yyyy-MM作为文件名
					String dateToStr = CommonUtil.DateToStr(new Date(),
							"yyyy-MM-dd");
					String fileName = Config.WithHold_FilePrefix
							+ CommonUtil.getLastMonth(dateToStr);
					
					data = HttpRequestUtil.queryBankBills(Config.Local_Token);
					retResult = HttpRequestUtil.getRetResult(data);
					if (retResult == RetTypeEnum.SUCCESS) {
						logger.info("账单文件请求成功");
						//先判断服务器存放账单文件目录是否存在  不存在则创建一个目录
						File dirFile = new File(Config.Server_Send_Path);
						if(!dirFile.exists()){
							boolean b = dirFile.mkdirs();
							if(b){
								logger.info("存放目录生成成功");
							}else{
								logger.info("存放目录生成失败!");
								break;
							}
						}
						File file = new File(Config.Server_Send_Path+fileName);
			             try {
			                 file.createNewFile();
			             } catch (IOException e) {
			                 logger.info("server创建账单文件失败");
			                 break;
			             }
						JSONObject bills = HttpRequestUtil.getDecodeData(data);
						JSONArray array = bills.getJSONArray("datas");
						@SuppressWarnings("unchecked")
						List<String> list = JSONArray.toList(array,String.class);
						createFile(file, list);
						break;
					}
				} else if (type.equals("txt")) {
					logger.info("正在向云平台传送账单。。。");
					data = HttpRequestUtil.queryBankBillsResult(
							Config.Local_Token, infoList);
					retResult = HttpRequestUtil.getRetResult(data);
					if (retResult == RetTypeEnum.SUCCESS) {
						logger.info("账单传送成功");
						/*JSONObject decodeData = HttpRequestUtil
								.getDecodeData(data);*/
						// JSONObject.toBean(decodeData,
						// ResponsePackage.class);
						break;
					}
				}

				if (retResult == RetTypeEnum.TOKEN_ERROR) {
					logger.info("token过期，正在重新请求token......");
					CommonUtil.getAndHandleToken();
				}
				if (time == 0)
					time = 5;
				else if (time < 160)
					time *= 2;
				else
					break;
				int count = time;
				while (count-- > 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.info(e.getMessage());
					}
				}
				logger.info("服务器繁忙，正在尝试重新发送请求。。。");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//生成文件
	private void createFile(File file,List<String> infoList){
		 try {
	         writer = new OutputStreamWriter(new FileOutputStream(file), Config.Coding_Format);
	         bw = new BufferedWriter(writer);
	         for(int i=0;i<infoList.size();i++){
	        	 String str=infoList.get(i)+"\r\n";
			         bw.write(str);
			         bw.flush();
	         }
	         logger.info("server保存账单成功");
		 } catch (IOException e) {
	         logger.info(e.getMessage());
	     }finally{
	    	 	if (bw != null) {
					try {
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	     }
         
	}
}
