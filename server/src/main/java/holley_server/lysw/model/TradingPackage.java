package holley_server.lysw.model;

import holley_server.lysw.util.Config;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易包字段
 * @author xhx
 *
 */
public class TradingPackage {
	
	private TradeCodeEnum   tradeCode;      //交易码
	private BankReturnStatusEnum returnCode;//返回码
	private String userAccount;             //用户号
	private String userName;                //用户名
	private String cardNumber;              //银行卡号
	private String address;                 //地址
	private String phone;                   //电话
	private String meterNumber;             //表号
	private String waterPriceName;          //水价名
	private BigDecimal price;               //单价
	private BigDecimal lastDegrees;         //上期抄度
	private BigDecimal additionalDegrees;   //附加度
	private BigDecimal currentDegrees;      //本期抄度
	private BigDecimal degrees;             //度数
	private BigDecimal waterCharge;         //水费
	private BigDecimal recoveryCharge;      //复接费
	private BigDecimal lateMoney;           //滞纳金
	private Date lastPayDate;               //上次交费期
	private Date endDate;                   //截止期间
	private Date payDate;                   //交费日期   代表交易日期？xhx
	private String BankFlowNumber;          //银行流水号
	private String companyFlowNumber;       //自来水公司流水号
	private String BankCode;                //银行代码
	private String tapCompanyCode=Config.Water_Company_Code; //自来水公司代码
	//private Date tradeDate;//交易日期去哪了？
	
	public TradeCodeEnum getTradeCode() {
		return tradeCode;
	}
	public void setTradeCode(TradeCodeEnum tradeCode) {
		this.tradeCode = tradeCode;
	}
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getWaterPriceName() {
		return waterPriceName;
	}
	public void setWaterPriceName(String waterPriceName) {
		this.waterPriceName = waterPriceName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getLastDegrees() {
		return lastDegrees;
	}
	public void setLastDegrees(BigDecimal lastDegrees) {
		this.lastDegrees = lastDegrees;
	}
	public BigDecimal getAdditionalDegrees() {
		return additionalDegrees;
	}
	public void setAdditionalDegrees(BigDecimal additionalDegrees) {
		this.additionalDegrees = additionalDegrees;
	}
	public BigDecimal getCurrentDegrees() {
		return currentDegrees;
	}
	public void setCurrentDegrees(BigDecimal currentDegrees) {
		this.currentDegrees = currentDegrees;
	}
	public BigDecimal getDegrees() {
		return degrees;
	}
	public void setDegrees(BigDecimal degrees) {
		this.degrees = degrees;
	}
	public BigDecimal getWaterCharge() {
		return waterCharge;
	}
	public void setWaterCharge(BigDecimal waterCharge) {
		this.waterCharge = waterCharge;
	}
	public BigDecimal getRecoveryCharge() {
		return recoveryCharge;
	}
	public void setRecoveryCharge(BigDecimal recoveryCharge) {
		this.recoveryCharge = recoveryCharge;
	}
	public BigDecimal getLateMoney() {
		return lateMoney;
	}
	public void setLateMoney(BigDecimal lateMoney) {
		this.lateMoney = lateMoney;
	}
	public Date getLastPayDate() {
		return lastPayDate;
	}
	public void setLastPayDate(Date lastPayDate) {
		this.lastPayDate = lastPayDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public String getBankFlowNumber() {
		return BankFlowNumber;
	}
	public void setBankFlowNumber(String bankFlowNumber) {
		BankFlowNumber = bankFlowNumber;
	}
	public String getCompanyFlowNumber() {
		return companyFlowNumber;
	}
	public void setCompanyFlowNumber(String companyFlowNumber) {
		this.companyFlowNumber = companyFlowNumber;
	}
	public String getBankCode() {
		return BankCode;
	}
	public void setBankCode(String bankCode) {
		BankCode = bankCode;
	}
	public String getTapCompanyCode() {
		return tapCompanyCode;
	}
	public void setTapCompanyCode(String tapCompanyCode) {
		this.tapCompanyCode = tapCompanyCode;
	}
	
	
}
