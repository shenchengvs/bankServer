package holley_server.lysw.service_http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONObject;
import holley_server.lysw.model.RequestPackage;
import holley_server.lysw.model.ResponsePackage;
import holley_server.lysw.model.RetTypeEnum;
import holley_server.lysw.util.CommonUtil;
import holley_server.lysw.util.Config;
import holley_server.lysw.util.HttpRequestUtil;

public class BankWithholdServiceHttpImpl implements BankWithholdServiceHttp {
	private  static Log         logger = LogFactory.getLog(BankWithholdServiceHttpImpl.class.getName());
	//有一个服务类：  银行传送的信息写入到用户档案中，如果返回成功信息，则签约登记成功。如果返回出错信息，则签约失败
	public ResponsePackage requestRegisterUser(RequestPackage rp) {

		// "返回码｜户号｜户名｜卡号｜装表地址｜电话｜表号｜自来水代码｜";
		JSONObject queryBankContract;
		try {
			boolean flag=true;
			while(true){
				queryBankContract= HttpRequestUtil.queryBankContract(Config.Local_Token, rp.getUserAccount(),
						rp.getCardNumber(), CommonUtil.DateToStr(rp.getTradeDate(), "yyyy-MM-dd"));
				RetTypeEnum retResult = HttpRequestUtil.getRetResult(queryBankContract);
				if(retResult==RetTypeEnum.SUCCESS){
					
					JSONObject decodeData = HttpRequestUtil.getDecodeData(queryBankContract);
					return (ResponsePackage) JSONObject.toBean(decodeData, ResponsePackage.class);
				}else if(retResult==RetTypeEnum.TOKEN_ERROR&&flag){
					flag=false;
					logger.info("token过期，正在重新请求token......");
					CommonUtil.getAndHandleToken();
				}else {
					logger.info("retResult错误信息："+retResult);
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("签约登记失败......");
			logger.info(e.getMessage());
		}
		return null;
	}

	public ResponsePackage cancelRegister(RequestPackage rp) {
		// TODO Auto-generated method stub

		// "返回码｜自来水代码｜";
		JSONObject queryBankContract;
		try {
			boolean flag=true;
			while(true){
				queryBankContract= HttpRequestUtil.queryBankDissolution(Config.Local_Token, rp.getUserAccount(),
						rp.getCardNumber(), CommonUtil.DateToStr(rp.getTradeDate(), "yyyy-MM-dd"));
				RetTypeEnum retResult = HttpRequestUtil.getRetResult(queryBankContract);
				if(retResult==RetTypeEnum.SUCCESS){
					JSONObject decodeData = HttpRequestUtil.getDecodeData(queryBankContract);
					return (ResponsePackage) JSONObject.toBean(decodeData, ResponsePackage.class);
				}else if(retResult==RetTypeEnum.TOKEN_ERROR&&flag){
					flag=false;
					logger.info("token过期，正在重新请求token......");
					CommonUtil.getAndHandleToken();
				}else{
					logger.info("retResult错误信息："+retResult);
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("解约失败......");
			logger.info(e.getMessage());
		}
		return null;
	}

}
