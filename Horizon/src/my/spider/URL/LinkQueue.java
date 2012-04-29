package my.spider.URL;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * URL队列，用来存放URL
 * @method 
 * @author root
 * @version 0.0.1
 */
public class LinkQueue {
	private static SimpleBloomFilter visitedURL = new SimpleBloomFilter();// 用来判断以访问的集合
	private static Queue<CrawURL> unVistedURL = new LinkedBlockingQueue<CrawURL>();// 待访问集合
	private static Queue<CrawURL> failedURL = new LinkedBlockingQueue<CrawURL>();//抓取失败的URL集合
	/**
	 * 获取访问失败的队列
	 * @return failedURL:Qeueu<CrawURL>
	 */
	public static Queue<CrawURL> getFailedURL() {
		return failedURL;
	}

	/**
	 * 获取带访问的URL队列
	 * 
	 * @return Queue
	 */
	public synchronized static Queue<CrawURL> getUnVistedURL() {
		return unVistedURL;
	}
	
	public static Integer getUnvisitedCount(){
		return unVistedURL.size();
	}

	/**
	 * 将访问过的URL添加到visitedURL集合
	 * 
	 * @param url
	 */
	public synchronized static void addVistedUrl(String url) {
		visitedURL.add(url);
	}

	/**
	 * 未访问的URL出列（从未访问队列中得到一个待访问的URL）
	 * 
	 * @return url:CrawURL
	 */
	public synchronized static CrawURL getUnVisitedURL() {
		return unVistedURL.poll();
	}

	/**
	 * URL去重后添加到待访问队列
	 * @param url:String
	 */
	public synchronized static void addUnVisitedURL(String url) {
		CrawURL cu = new CrawURL();
		cu.setCriUrl(url);
		if (url != null && !url.trim().equals("") && !visitedURL.contains(url)
				&& !unVistedURL.contains(url)) {
			unVistedURL.add(cu);
		}
	}
	
	/**
	 * URL去重后添加到待访问队列
	 * @param urls:Queue<String>
	 */
	public synchronized static void addUnVisitedURL(Queue<String> urls){
		CrawURL cu;
		String url=null;
		if(urls.isEmpty())return;
		Iterator<String> iterator = urls.iterator();
		while(iterator.hasNext()){
			cu = new CrawURL();
			cu.setCriUrl(url);
			url=iterator.next();
			if (url != null && !url.trim().equals("") && !visitedURL.contains(url)
					&& !unVistedURL.contains(url)) {
				unVistedURL.add(cu);
			}
		}
	}
	/**
	 *取得访问成功的URL 数量
	 * @return visitedURL.getCount():int
	 */
	public static int getVisitedURLNum(){
		return visitedURL.getCount();
	}
	/**
	 * 判断未抓取的链接队列是否为空
	 * @return
	 */
	public synchronized static boolean unVisitedURLISEmpty(){
		return unVistedURL.isEmpty();
	}
	/**
	 * 当抓取任务出现异常时，
	 * 向抓取失败队列添加URL
	 * @param url
	 */
	public synchronized static void addFailedURL(String url){
		CrawURL cri=new CrawURL();
		cri.setUrl(url);
		failedURL.add(cri);
	}
	/**
	 * 获取抓取失败队列的大小
	 * @return
	 */
	public synchronized static Integer getFailedURLCount(){
		return failedURL.size();
	} 
}
