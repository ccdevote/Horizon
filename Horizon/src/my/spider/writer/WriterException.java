package my.spider.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Writer的异常处理类
 * @author MZY
 *
 */
public class WriterException extends RuntimeException {
	private static final long serialVersionUID = 686877112435361336L;
	private Log log = LogFactory.getLog(this.getClass());
	public WriterException(Exception e,String message) {
		log.error("Error in do Writer \n"+message);
	}
}
