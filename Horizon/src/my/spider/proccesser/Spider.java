package my.spider.proccesser;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import my.spider.URL.LinkQueue;
import my.spider.manager.HttpClientManager;
import my.spider.parse.FetchURL;

/**
 * 爬虫主类
 * 
 * @author MZY
 * 
 */
public class Spider {
	private Integer DOWNLOAD_THREAD;
	private ExecutorService es;
	private CountDownLatch BEGIN;
	private CountDownLatch END;
//	private Queue<HttpClientManager> client = new LinkedList<HttpClientManager>();
	private String dir;
	private static Log log = LogFactory.getLog(Spider.class);

	public Integer getDOWNLOAD_THREAD() {
		return DOWNLOAD_THREAD;
	}

	public void setDOWNLOAD_THREAD(Integer dOWNLOAD_THREAD) {
		DOWNLOAD_THREAD = dOWNLOAD_THREAD;
	}

	public Spider(Integer threadPoolSize, String firstURL, String dir) {
		this.dir = dir;
		setDOWNLOAD_THREAD(threadPoolSize);// 初始化线程池大小
		LinkQueue.addUnVisitedURL(firstURL);// 将种子添加到带抓取队列
		// 创建线程池
		es = Executors.newFixedThreadPool(DOWNLOAD_THREAD);
		// 设置开始位
		BEGIN = new CountDownLatch(1);
		// 设置结束位
		END = new CountDownLatch(DOWNLOAD_THREAD);
	}

	public void initThreadPool() {
		for (int i = 1; i <= DOWNLOAD_THREAD; i++) {
			log.info("初始化线程：" + i);
			es.submit(new _Frontier(i, BEGIN, END));
			///client.add(new HttpClientManager());
		}
		// HtmlParser parser = new HtmlParser();
		HttpClientManager cm = new HttpClientManager(true,this.dir);
		// parser.setHtml(client.get("http://jandan.net/ooxx"));
		String fu=LinkQueue.getUnVisitedURL().getCriUrl();
		System.out.println("====开始抓取首页:"+fu+"======");
		String content=cm.doGet(fu);
		LinkQueue.addUnVisitedURL(FetchURL.getInstance(fu)
				.fetchUrls(content));
		// 将抓取过的信息放入VisitedURL列表进行记录
		LinkQueue.addVistedUrl(fu);
		cm.release();
		System.out.println("首页结束，开始执行多线程抓取");
		BEGIN.countDown();
	}

	public class _Frontier implements Callable<String> {
		private int index;
		CountDownLatch end;
		CountDownLatch begin;

		/**
		 * 初始化抓取线程，信息
		 * 
		 * @param index
		 * @param BEGIN
		 * @param END
		 */
		public _Frontier(int index, CountDownLatch BEGIN, CountDownLatch END) {
			this.index = index;
			this.begin = BEGIN;
			this.end = END;
		}

		@Override
		public String call() {
			try {
				begin.await();
				log.info("线程 " + index + " 开始工作");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpClientManager cm = null;
			String content = null;
			while (LinkQueue.getUnvisitedCount() > 0) {
				try{
					String url = LinkQueue.getUnVisitedURL().getCriUrl();
					cm = new HttpClientManager(true,dir);
					if (url != null&&cm!=null) {
						log.info("线程 "+index+" 抓取 "+url);
						content =cm.doGet(url);
						//System.out.println(content.length());
						LinkQueue.addUnVisitedURL(FetchURL.getInstance(url)
								.fetchUrls(content));
						// 将抓取过的信息放入VisitedURL列表进行记录
						LinkQueue.addVistedUrl(url);
						log.info("待抓取链接数量 ："+LinkQueue.getUnvisitedCount());
					} else {
						LinkQueue.addFailedURL(url);
						log.error("线程 " + index +"抓取失败，添加到失败队列："+LinkQueue.getFailedURLCount()+" "+url);
					}
				}catch(Exception e){
					e.printStackTrace();
					log.error(e.getMessage());
				}finally{
					//cm.release();
				}
			}
			//client.add(cm);
			
			end.countDown();
			log.info("工作结束");
			return content;
		}

	}

}
