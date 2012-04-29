package my.spider.fetcher;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import my.crawler.connection.Connection;
import my.spider.parse.FetchURL;


/**
 * Fetcher the urls based on ThreadPool-->DownLoadCall.java<br/>
 * @author MZY
 * @version 0.0.1
 */
public class Fetcher {
	private String[] urls;//url pool
	private Logger log = Logger.getLogger(Fetcher.class.getName());
	private long DELAY_TIME=0;

	public String[] getUrls() {
		return urls;
	}
	public void setUrls(String[] urls) {
		this.urls = urls;
	}
	public long getDELAY_TIME() {
		return DELAY_TIME;
	}
	public void setDELAY_TIME(long DELAY_TIME) {
		this.DELAY_TIME =DELAY_TIME;
	}
	public class DownLoadCall implements Callable<String> {
		private String url;
		public DownLoadCall(String url) {
			this.setUrl(url);
		}
		private void setUrl(String url) {
			this.url=url;	
		}
		@Override
		public String call() throws Exception {
			String content=null;
			content=Connection.downPage(Connection.getInputStreamReader(Connection.URLCon(url)));
			FetchURL.getInstance(url).fetchUrls(content);
			return content;
		}

	}
	public String download(){
		int threads=4;
		String content=null;
		/*create thread pool*/
		ExecutorService threadPool=Executors.newFixedThreadPool(threads);
		Set<Future<String>> futureSet = new HashSet<Future<String>>();
		for(final String url:urls){
			//create task 
			DownLoadCall task = new DownLoadCall(url);
			//submit task
			Future<String> future = threadPool.submit(task);
			futureSet.add(future);
		}
		Iterator<Future<String>> iterator = futureSet.iterator();
		while(iterator.hasNext()){
			try {
				content=iterator.next().get();
				TimeUnit.MILLISECONDS.sleep(DELAY_TIME);
			//	FetchURL.getInstance()
				//log.info("dealling=======>"+content);
			} catch (InterruptedException e) {
				log.log(Level.WARNING,"InterruptedException in Fetcher"+e.getMessage());
			} catch (ExecutionException e) {
				log.log(Level.WARNING,"ExecutionException in Fetcher"+e.getMessage());
			}
		}
		return content;
	}
	public static void main(String args[]){
		String[] urls={"http://product.pconline.com.cn/mobile/"};
		Fetcher fc=new Fetcher();
		fc.setDELAY_TIME(2000);
		fc.setUrls(urls);
		System.exit(0);
		//System.out.println(content);
	}
}
