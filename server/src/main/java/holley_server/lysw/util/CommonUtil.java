package holley_server.lysw.util;


import holley_server.lysw.model.RequestPackage;
import holley_server.lysw.model.ResponsePackage;
import holley_server.lysw.model.RetTypeEnum;
import holley_server.lysw.model.TradeCodeEnum;
import holley_server.lysw.model.TradingPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.File;
import java.io.NotSerializableException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonUtil {
	private static ExecutorService     cacheThreadPool = Executors.newCachedThreadPool();//线程池
    public static ChannelGroup  group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);//通道存放
    private static Log         logger = LogFactory.getLog(CommonUtil.class.getName());
    private static String charset = "GBK";
    
    public static String getDataString(JSONObject data, String key) {
        if (data == null) {
            return "";
        }
        return data.containsKey(key) ? data.getString(key) : "";
    }

    public static int getDataInt(JSONObject data, String key) {
        if (data == null) {
            return 0;
        }
        return data.containsKey(key) ? NumberUtils.toInt(data.getString(key)) : 0;
    }
    public static double getDataDouble(JSONObject data, String key) {
        if (data == null) {
            return 0;
        }
        return data.containsKey(key) ? NumberUtils.toDouble(data.getString(key)) : 0;
    }
    public static void getParam(String s, List<String> list) {
        if (s.indexOf("|") != -1) {
            String tem = s.substring(0, s.indexOf("|"));
            String tem2 = s.substring(s.indexOf("|") + 1, s.length());
            list.add(tem);
            getParam(tem2, list);
        }
    }
    public static Channel getChannel(ChannelId channelId){
    	return group.find(channelId);
    }
    public static void addChannel(Channel channel){
    	group.add(channel);
    }
    public static void removeChannel(ChannelId channelId){
    	group.remove(channelId);
    }
    /**
     * 执行线程
     * @param r
     */
    public static void execute(Runnable r) {
        cacheThreadPool.execute(r);
    }
    
    /**
     * 停止Cache线程池
     */
    public static void stopCacheJob() {
        if (cacheThreadPool != null) {
            cacheThreadPool.shutdown();
        }
    }
    /**
     * @param buf
     * @return
     */

    public static byte[] getByte(Object msg) {
        byte[] con = null;
        try {
            ByteBuf temp = (ByteBuf) msg;
            ByteBuf buffer = temp.copy();
            con = new byte[buffer.readableBytes()];
            buffer.readBytes(con);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return con;
    }

    public static ByteBuf sendByte(byte[] baseMsg) {
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(baseMsg);
        return pingMessage;
    }

    /*
     * 从ByteBuf中获取信息 使用UTF-8编码返回
     */
    public static String getString(Object msg) {

        try {
            ByteBuf temp = (ByteBuf) msg;
            byte[] req = new byte[temp.readableBytes()];
            temp.readBytes(req);
            // msg = (BaseMsg) SerializableUtils.deserializeFromByteArray(con);
            return new String(req,charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteBuf sendString(String baseMsg) throws UnsupportedEncodingException, NotSerializableException {
        // byte[] req = SerializableUtils.serializeToByteArray(baseMsg);
        // byte[] req = baseMsg.getBytes("UTF-8");
        // ByteBuf pingMessage = Unpooled.buffer();
        ByteBuf pingMessage = Unpooled.copiedBuffer(baseMsg.getBytes(charset));
        // pingMessage.writeBytes(req);
        return pingMessage;
    }

    public static String getClientId(String info) {
        try {
            String str = info.substring(1, info.length() - 1);
            String[] strs = str.split(",");
            String temp = strs[0];
            strs = temp.split(":");
            return strs[1].trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
    public static String DateToStr(Date date, String Format) {
        if (date == null) return "";
        SimpleDateFormat formater = new java.text.SimpleDateFormat(Format);
        return formater.format(date);
    }
    
  //转换日期格式
    public static Date getDate(String dateStr){
    	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
         try {
			return simpleDateFormat.parse(dateStr);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
         return null;
    }
    //获得上个月
    public static String getLastMonth(String dateStr) {
    	try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMM");
            Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date); 
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
            date = calendar.getTime();
     
            return dateFormat1.format(date);
    	} catch (ParseException e) {
			e.printStackTrace();
		}
    	logger.info("日期转化失败");
    	return "error";
    }
    
	//根据参数信息生成50位包头
    public static String packMsg(String trtype,long slen,String fname,long flen){
    	String packHead=changeToString(trtype, 6);
    	packHead+=changeToString(slen, 10);
    	packHead+=changeToString(fname, 24);
    	packHead+=changeToString(flen, 10);
		return packHead;
    }
    
    //根据包头生成MsgToPackageUtil
    public static MsgToPackageUtil createMsgToPackage(String strMsg){
    	if(strMsg.length()==50){
			try{
				String trtype=strMsg.substring(0, 6);
				long slen = Long.valueOf(strMsg.substring(6, 16).trim());
				String fname=strMsg.substring(16, 40);
				String trim = strMsg.substring(40).trim();
				long flen;
				if(trim==null||trim.equals(""))
					flen=0;
				else
					flen=Long.valueOf(trim);
				//对包头的非法情况进行判断
				if(!trtype.equals(Config.Execute_Code))
					return null;
				if(slen<=0||flen<0)
					return null;
				return new MsgToPackageUtil(trtype,slen,fname,flen);
			}catch(Exception e){
				return null;
			}
		}else{
			return null;
		}
    }

    //将数值转化为指定长度的字符串
    public static String changeToString(long val,int count){
    	String string = String.format("%"+count+"d", val); 
    	return string;
    }
    //将字符串转化为指定长度的字符串   用空格代替位置
    public static String changeToString(String val,int count){
    	StringBuilder sb=new StringBuilder();
    	for(int i=0;i<count-val.length();i++){
    		sb.append(" ");
    	}
    	return sb.toString()+val;
    }
    //获取并解析token
    public static void getAndHandleToken(){
    	JSONObject data;
		try {
			for(int i=0;i<5;i++){
				data = HttpRequestUtil.queryToken();
				RetTypeEnum retResult = HttpRequestUtil.getRetResult(data);
				if(retResult==RetTypeEnum.SUCCESS){
					JSONObject decodeData = HttpRequestUtil.getDecodeData(data);
					if(decodeData.get("accessToken")!=null){
						Config.Local_Token=(String) decodeData.get("accessToken");
						logger.info("token重设成功：值"+Config.Local_Token);
						break;
					}else{
						continue;
					}
				}else{
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			logger.info("token重设失败");
		}
    }
    //生成交易包结构
    public static TradingPackage getTradingPackage(RequestPackage rp,ResponsePackage rep){
    	TradingPackage tp = new TradingPackage();
    	if(rp!=null){
    		tp.setTradeCode(rp.getTradeCode());
    		tp.setUserAccount(rp.getUserAccount());
    		tp.setCardNumber(rp.getCardNumber());
    		tp.setPayDate(rp.getTradeDate());
    		tp.setBankCode(rp.getBankCode());
    	}
    	if(rep!=null){
    		tp.setReturnCode(rep.getReturnCode());
    		tp.setUserName(rep.getUserName());
    		tp.setAddress(rep.getMeterAddress());
    		tp.setPhone(rep.getPhone());
    		tp.setMeterNumber(rep.getMeterNumber());
    		tp.setTapCompanyCode(rep.getTapwaterCode());
    	}
    	return tp;
    }
    /**
     * 交易包  生成返回串
     * @param tp
     * @return
     */
    public static String getReturnStr(TradingPackage tp){
    	/*交易码｜返回码｜户号｜户名｜卡号｜装表地址｜电话｜表号|(隔了11个｜)交易日期｜(隔了2个｜)银行代码｜自来水代码｜*/
    	StringBuilder sb=new StringBuilder();
    	sb.append((tp.getTradeCode()!=null?tp.getTradeCode().getValue():"")+"|");
    	sb.append((tp.getReturnCode()!=null?tp.getReturnCode().getValue():"")+"|");
    	sb.append((tp.getUserAccount()!=null?tp.getUserAccount():"")+"|");
		//在户名或地址中，不可有｜号，如有的话则要进行替换成I
		if(tp.getUserName()!=null){
			if(tp.getUserName().contains("|"))
				sb.append(tp.getUserName().replaceAll("\\|", "I"));
			else
				sb.append(tp.getUserName());
		}
		sb.append("|");
		sb.append((tp.getCardNumber()!=null?tp.getCardNumber():"")+"|");
		if(tp.getAddress()!=null){
			if(tp.getAddress().contains("|"))
				sb.append(tp.getAddress().replaceAll("\\|", "I"));
			else
				sb.append(tp.getAddress());
		}
		sb.append("|");
		sb.append((tp.getPhone()!=null?tp.getPhone():"")+"|");
		sb.append((tp.getMeterNumber()!=null?tp.getMeterNumber():"")+"|");
		sb.append("|||||||||||");
		sb.append((tp.getPayDate()!=null?CommonUtil.DateToStr(tp.getPayDate(), "yyyy-MM-dd"):"")+"|");
		sb.append("||");
		sb.append((tp.getBankCode()!=null?tp.getBankCode():"")+"|");
		sb.append((tp.getTapCompanyCode()!=null?tp.getTapCompanyCode():"")+"|");
		return new String(sb);
    }
	/**
	 * 用于装换list信息为RequestPackage
	 * @param list
	 * @return
	 */
	public static RequestPackage createRequest(List<String> list){
		//请求包格式  交易码｜户号｜卡号｜交易日期｜银行代码｜
		RequestPackage rp=new RequestPackage();
		rp.setTradeCode(TradeCodeEnum.getEnmuByValue(list.get(0)));
		rp.setUserAccount(list.get(2));
		rp.setCardNumber(list.get(4));
		rp.setTradeDate(CommonUtil.getDate(list.get(19)));
		rp.setBankCode(list.get(22));
		return rp;
	}
	//清理dirFileName文件夹下的超时文件
		public static void deleteFile(String dirFileName,long Millisecond){
	        File dirFile = new File(dirFileName);
	        if (!dirFile.exists()) {  
	        	logger.info("清理文件出错："+dirFileName+"目录 不存在！");  
	            return ;
	        }  
	        if (!dirFile.isDirectory()) {  
	        	logger.info("清理文件出错："+dirFileName+"不是一个目录！"); 
	            return ;  
	        }  
	        //获得当前毫秒
	        long currentTime = System.currentTimeMillis();
	        //获取此目录下的所有文件名与目录名  
	        String[] fileList = dirFile.list();  
	        for (int i = 0; i < fileList.length; i++) {  
	            //遍历文件目录  
	            String string = fileList[i];  
	            File file = new File(dirFile.getPath(),string);  
	            String name = file.getName();  
	            if (file.isFile()) {  
	                //如果是文件，判断时间是否超时
	            	long modified = file.lastModified();
	            	if(currentTime-modified>Millisecond){
		            	if(file.delete()){
		            		logger.info(name+"删除成功");
		            	}else{
		            		logger.info(name+"删除失败");
		            	}
	            	}
	            }  
	        }
		}
}
