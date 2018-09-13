package holley_server.lysw.util;


public class MsgToPackageUtil {
/*	TrType：char[6]			//执行码：”000001”
			Slen：	char[10]			//结构长度
			Fname：	char[24]			//文件名
			Flen：	char[10]*/
	private String trtype;
	private long slen;
	private String fname;
	private long flen;
	public MsgToPackageUtil(){
		super();
	}
	
	public MsgToPackageUtil(String trtype, long slen, String fname, long flen) {
		super();
		this.trtype = trtype;
		this.slen = slen;
		this.fname = fname;
		this.flen = flen;
	}

	public String getTrtype() {
		return trtype;
	}
	public void setTrtype(String trtype) {
		this.trtype = trtype;
	}
	public long getSlen() {
		return slen;
	}
	public void setSlen(long slen) {
		this.slen = slen;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public long getFlen() {
		return flen;
	}
	public void setFlen(long flen) {
		this.flen = flen;
	}
}
