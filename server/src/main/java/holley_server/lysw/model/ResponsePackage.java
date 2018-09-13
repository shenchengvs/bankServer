package holley_server.lysw.model;

import com.alibaba.fastjson.JSON;

/**
 * 用于接收从云平台返回用户登记交易的字段值
 * @author xhx
 *
 */
public class ResponsePackage {
	/*返回码｜户号｜户名｜卡号｜装表地址｜电话｜表号｜自来水代码｜*/
	private BankReturnStatusEnum returnCode;
	
	private String userAccount;
	
	private String userName;
	
	private String cardNumber;
	
	private String meterAddress;
	
	private String phone;
	
	private String meterNumber;
	
	private String tapwaterCode;

	public BankReturnStatusEnum getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(BankReturnStatusEnum returnCode) {
		this.returnCode = returnCode;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getMeterAddress() {
		return meterAddress;
	}

	public void setMeterAddress(String meterAddress) {
		this.meterAddress = meterAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMeterNumber() {
		return meterNumber;
	}

	public void setMeterNumber(String meterNumber) {
		this.meterNumber = meterNumber;
	}

	public String getTapwaterCode() {
		return tapwaterCode;
	}

	public void setTapwaterCode(String tapwaterCode) {
		this.tapwaterCode = tapwaterCode;
	}
	
	public static void main(String[] args) {
		ResponsePackage test = new ResponsePackage();
		test.setReturnCode(BankReturnStatusEnum.ARREARS_NOT_CASH);
		String jsonstr = JSON.toJSONString(test);
		ResponsePackage t2 = JSON.parseObject(jsonstr, ResponsePackage.class);
		System.out.println(t2.getReturnCode().getValue());
	}
}
