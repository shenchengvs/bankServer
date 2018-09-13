package holley_server.lysw.task;

import holley_server.lysw.util.CommonUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClearFileJob implements Runnable {
	private static Log         logger = LogFactory.getLog(ClearFileJob.class.getName());
	private String logDir;
	private String receiveDir;
	private String sendDir;
	private long	Millisecond;
	
	public ClearFileJob(String logDir, String receiveDir, String sendDir,long day) {
		super();
		this.logDir = logDir;
		this.receiveDir = receiveDir;
		this.sendDir = sendDir;
		this.Millisecond=day*24*60*60*1000;
	}
	public void run() {
		CommonUtil.deleteFile(logDir, Millisecond);
		CommonUtil.deleteFile(receiveDir, Millisecond);
		CommonUtil.deleteFile(sendDir, Millisecond);
		logger.info("线程清理文件完成\n");
	}
	
}
