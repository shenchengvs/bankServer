package holley_server.lysw.model;

public enum TradeCodeEnum {
	 REGISTER("07021", "代扣用户登记交易"),
	 CANCEL("07022", "撤消登记交易"), 
	 FILE_WITHHOLD("07024", "传送待扣款文件"), 
	 FILE_RESULT("07025", "传送扣款结果文件");

	    private final String    value;
	    private final String text;

	    TradeCodeEnum(String value, String text) {
	        this.value = value;
	        this.text = text;
	    }

	    public String getValue() {
	        return value;
	    }

	    public String getText() {
	        return text;
	    }

	    public static String getText(String value) {
	    	TradeCodeEnum task = getEnmuByValue(value);
	        return task == null ? null : task.getText();
	    }

	   /* public Short getShortValue() {
	        Integer obj = value;
	        return obj.shortValue();
	    }*/

	    /**
	     * 通过传入的值匹配枚举
	     * 
	     * @param value
	     * @return
	     */
	    public static TradeCodeEnum getEnmuByValue(String value) {
	        for (TradeCodeEnum record : TradeCodeEnum.values()) {
	            if (value.equals(record.getValue())) {
	                return record;
	            }
	        }
	        return null;
	    }
}
