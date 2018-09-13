package holley_server.lysw.server;


import holley_server.lysw.model.BankReturnStatusEnum;
import holley_server.lysw.model.RequestPackage;
import holley_server.lysw.model.TradeCodeEnum;
import holley_server.lysw.model.TradingPackage;
import holley_server.lysw.service.BankWithholdService;


import holley_server.lysw.task.Job;
import holley_server.lysw.task.TimerJob;
import holley_server.lysw.util.CommonUtil;
import holley_server.lysw.util.Config;
import holley_server.lysw.util.MsgToPackageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.ReferenceCountUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TcpHandler extends ChannelInboundHandlerAdapter {

	private  static Log logger = LogFactory.getLog(TcpHandler.class.getName());
	
	private BufferedWriter bw;
	private OutputStreamWriter writer;
    private RandomAccessFile raf = null; 
    private int times;
    private MsgToPackageUtil mtp;
    private RequestPackage rp;
    private long fileLength=0;//接收的文件长度
    private boolean flag=false;     //用于标志文件是否接收完成

    //用于存放文件接收结束之后的所有记录信息
    private List<String> infoList=new ArrayList<String>();
    
    private BankWithholdService bankWithholdService=(BankWithholdService) SpringSupport.springHandle.getBean("bankWithholdService");
    
    public TcpHandler() {
    	logger.info("TcpHandlerInit.....");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	String addressStr = ctx.channel().remoteAddress().toString();
        logger.info("建立联接"+ctx.channel().id()+" 地址："+addressStr.substring(1,addressStr.lastIndexOf(":"))
        		+" 端口："+addressStr.substring(addressStr.lastIndexOf(":")+1)+ "已连接");
        CommonUtil.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	
    	if(raf!=null)
    	   raf.close();
        CommonUtil.group.remove(ctx.channel());
        logger.info(ctx.channel().id() + "已断开");
        logger.info("channelInactive");
        if (bw != null) {
        	bw.close();
        }
        if(writer!=null){
        	writer.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	try {
    		if(times==3){
	    		infoList.add((String)msg);
	    		//windows ANSI 换行\r\n
	    		String str=(String)msg+"\r\n";
	    		fileLength+= str.getBytes(Config.Coding_Format).length;
                try {
                    bw.write(str);
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               
	    	 }else{
	    		 TradingPackage tp=new TradingPackage();
	    		//用于接收解析的交易码
    	    	List<String> tradeList=new ArrayList<String>();
    	    	byte[] strMsgBytes = CommonUtil.getByte(msg);
    	    	String strMsg = new String(strMsgBytes,Config.Coding_Format);
		    	if(strMsg.equalsIgnoreCase("SDB")){
		    		times=1;
		    		logger.info("接收 <"+ctx.channel().id()+">"+strMsg);
		    		
		    		//为pipeLine设置定长解码器（用于规定收到的包头长度为50 ） 
		    		ctx.pipeline().addFirst("lengthDecoder1",new FixedLengthFrameDecoder(50));
		    		
		    		//发送ACCEPT_DATA_RIGHT包
		    		ctx.channel().write(CommonUtil.sendString("ADR"));
		    		ctx.channel().flush();
		    		logger.info("发送 <"+ctx.channel().id()+">ADR");
		    	}else if(times==1){
		    		
		    		logger.info("接收 <"+ctx.channel().id()+">"+strMsg);
		    		//转化msg为包头类
		    		mtp=CommonUtil.createMsgToPackage(strMsg);
		    		if(mtp==null){
		    			//包头错误   发送错误给客户端
		    			logger.info("包头错误");
		    			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
		    			bankWithholdService.sendPackage(ctx.channel().id(), tp,"",0);
		    			ctx.channel().close();
		    			return ;
		    		}
		    		times=2;
		    		ctx.pipeline().addFirst("lengthDecoder2",new FixedLengthFrameDecoder((int) mtp.getSlen()));
					ctx.pipeline().remove("lengthDecoder1");
		    	}else if(times==2){
		    		//判断结构长度是否正确
		    		logger.info("接收 <"+ctx.channel().id()+">"+strMsg);
		    		if(strMsgBytes.length!=mtp.getSlen()){
		    			logger.info("交易包长度错误");
		    			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
		    			bankWithholdService.sendPackage(ctx.channel().id(), tp,"",0);
		    			ctx.channel().close();
		    			return ;
		    		}
	    			CommonUtil.getParam(strMsg, tradeList);
	    			//判断结构包中字段数是否正确
		    		if(tradeList.size()!=24){
		    			logger.info("交易包字段数错误");
		    			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("993"));
		    			bankWithholdService.sendPackage(ctx.channel().id(), tp,"",0);
		    			ctx.channel().close();
		    			return;
		    		}
		    		 rp = CommonUtil.createRequest(tradeList);
		    		 if(rp==null){
		    			 logger.info("结构包转化为请求对象失败，请检查字段类型是否匹配");
		    			 tp = CommonUtil.getTradingPackage(null, null);
		    			 tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
		    			 bankWithholdService.sendPackage(ctx.channel().id(), tp,"",0);
		    			 ctx.channel().close();
		    			 return;
		    		 }
		    		//判断结构包中是否含有银行代码
		    		if(rp.getBankCode()==null||rp.getBankCode().trim().equals("")){
		    			logger.info("请求包中无银行代码");
		    			tp = CommonUtil.getTradingPackage(rp, null);
		    			tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("994"));
		    			bankWithholdService.sendPackage(ctx.channel().id(), tp,"",0);
		    			ctx.channel().close();
		    			return;
		    		}
	    			if(mtp.getFlen()==0){
	    				CommonUtil.execute(new Job(bankWithholdService,rp,ctx.channel().id(),false,tradeList));
					} else if (mtp.getFlen() > 0) {
						 if(rp.getTradeDate()==null){
			    			 logger.info("请求中缺少交易日期字段");
			    			 tp = CommonUtil.getTradingPackage(rp, null);
			    			 tp.setReturnCode(BankReturnStatusEnum.getEnmuByValue("993"));
			    			 bankWithholdService.sendPackage(ctx.channel().id(), tp,"",0);
			    			 ctx.channel().close();
			    			 return;
			    		 }
						if (rp.getTradeCode()==TradeCodeEnum.FILE_RESULT) {
							logger.info("批量扣款交易步骤之二，传送扣款结果文件");
							ctx.pipeline().addBefore("tcpHandler","lineDecoder", new LineBasedFrameDecoder(8192));
				    		ctx.pipeline().addBefore("tcpHandler", "stringDecoder", new StringDecoder(Charset.forName(Config.Coding_Format)));
				    		File dirFile = new File(Config.Server_Store_Path);
				    		if(!dirFile.exists()){
								boolean b = dirFile.mkdirs();
								if(b){
									logger.info("存放目录生成成功");
								}else{
									logger.info("存放目录生成失败!");
								}
							}
							logger.info("保存客户端传输的文件");
				            File file = new File(Config.Server_Store_Path+mtp.getFname().trim());
				            //if (!file.exists()) {
				                try {
				                    file.createNewFile();
				                } catch (IOException e) {
				                    e.printStackTrace();
				                }
				           // }
				            try {
				            	writer = new OutputStreamWriter(new FileOutputStream(file), Config.Coding_Format);
				                bw = new BufferedWriter(writer);
				            } catch (FileNotFoundException e) {
				                e.printStackTrace();
				                if (bw != null) {
				                	bw.close();
				                }
				                if(writer!=null){
				                	writer.close();
				                }
				            }
				    		times=3;
				    		//使用定时器解决读文件长度不够的问题
				    		CommonUtil.execute(new TimerJob(bankWithholdService,rp,ctx.channel().id(),Config.Max_Time));
						}else{
							logger.info("非法文件结构交易码");
							TradingPackage tp1 = CommonUtil.getTradingPackage(rp, null);
				        	tp1.setReturnCode(BankReturnStatusEnum.getEnmuByValue("992"));
				        	try {
								bankWithholdService.sendPackage(ctx.channel().id(), tp1, "",0);
							} catch (NotSerializableException e) {
								e.printStackTrace();
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
				        	return;
						}
		    		}
	    			ctx.pipeline().remove("lengthDecoder2");
		    	}
	    	} 
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		/**
    		 * ByteBuf是一个引用计数对象，这个对象必须显示地调用release()方法来释放。 请记住处理器的职责是释放所有传递到处理器的引用计数对象。
    		 */
    		// 抛弃收到的数据
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
        if (bw != null) {
        	bw.close();
        }
        if(writer!=null){
        	writer.close();
        }
        ctx.channel().close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server:ReadComplete:"+times);
        //当文件接收完成时，进行业务处理并向客户端返回处理结果
    	if(times==3&&mtp!=null&&fileLength==mtp.getFlen()&&!flag){
    		flag=true;
    		CommonUtil.execute(new Job(bankWithholdService,rp,ctx.channel().id(),true,infoList));
    	}
    	if(times==3&&mtp!=null&&fileLength>mtp.getFlen()&&!flag){
    		//接收的文件长度大于给定长度      小于怎么判断呢xhx
    		logger.info("非法文件长度");
			TradingPackage tp1 = CommonUtil.getTradingPackage(rp, null);
        	tp1.setReturnCode(BankReturnStatusEnum.getEnmuByValue("991"));
        	try {
				bankWithholdService.sendPackage(ctx.channel().id(), tp1, "",0);
			} catch (NotSerializableException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	return;
    	}
    }

}
