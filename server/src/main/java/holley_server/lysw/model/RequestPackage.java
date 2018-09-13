package holley_server.lysw.model;

import java.util.Date;

/**
 * 请求包结构
 * @author xhx
 *
 */
public class RequestPackage {
//交易码｜户号｜卡号｜交易日期｜银行代码｜
	private TradeCodeEnum   tradeCode;
	
	private String userAccount;
	
	private String cardNumber;
	
	private Date tradeDate;
	
	private String BankCode;

	public TradeCodeEnum getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(TradeCodeEnum tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getBankCode() {
		return BankCode;
	}

	public void setBankCode(String bankCode) {
		BankCode = bankCode;
	}
	
}
