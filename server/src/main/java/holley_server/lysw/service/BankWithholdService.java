package holley_server.lysw.service;


import holley_server.lysw.model.RequestPackage;

import holley_server.lysw.model.TradingPackage;
import io.netty.channel.ChannelId;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface BankWithholdService {
	
	/**
	 * 代扣用户登记交易（07021）
	 * @param ctx
	 * @param rp    请求包
	 * @throws NotSerializableException
	 * @throws UnsupportedEncodingException
	 */
	void userRegister(ChannelId channelId,RequestPackage rp) throws NotSerializableException, UnsupportedEncodingException ;
	
	/**
	 * 撤消登记交易（07022）
	 * @param ctx   
	 * @param rp	 请求包
	 * @throws NotSerializableException
	 * @throws UnsupportedEncodingException
	 */
	void cancelRegister(ChannelId channelId,RequestPackage rp) throws NotSerializableException, UnsupportedEncodingException ;

	/**
	 * 批量扣款交易步骤之一，传送待扣款文件（07024）
	 * @param ctx   
	 * @param rp  	请求包
	 * @throws NotSerializableException
	 * @throws UnsupportedEncodingException
	 */
	void sendWithheldFile(ChannelId channelId, RequestPackage rp) throws IOException;
	/**
	 * 批量扣款交易步骤之二，传送扣款结果文件(07025)
	 * @param ctx
	 * @param infoList   结果集合
	 * @throws NotSerializableException
	 * @throws UnsupportedEncodingException
	 */
	void receiveAndHandle(ChannelId channelId,RequestPackage rp,List<String> infoList) throws NotSerializableException, UnsupportedEncodingException;
	
	/**
	 * 统一发送包头与结构包
	 * @param ctx    
	 * @param tp         	待返回的交易信息
	 * @param fileName  	 待返回的文件名
	 * @param fileLength 	待返回的文件的长度
	 * @throws UnsupportedEncodingException
	 * @throws NotSerializableException
	 */
	public void sendPackage(ChannelId channelId, TradingPackage tp,String fileName,long fileLength)
			throws UnsupportedEncodingException, NotSerializableException;
}
