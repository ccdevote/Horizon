package my.spider.parse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import my.spider.URL.URLFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 从爬行到的结果中抽取URL
 * 
 * @author MZY
 * @0.0.1
 * @date 2012-04-24
 */
public class FetchURL {
	private Map<String, String> urlMap = new HashMap<String, String>();
	// save all the urls witch we find from the page
	private List<String> urlList;
	// save the truth urls witch we need
	private Queue<String> urlQueue;
	private URLFilter urlFilter;
	private String baseURL;

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public URLFilter getUrlFilter() {
		return urlFilter;
	}

	private void setUrlFilter(URLFilter urlFilter) {
		this.urlFilter = urlFilter;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	private void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}

	public Queue<String> getUrlQueue() {
		return urlQueue;
	}

	private void setUrlQueue(Queue<String> urlQueue) {
		this.urlQueue = urlQueue;
	}

	private FetchURL(String baseURL) {
		setUrlList(new LinkedList<String>());
		setUrlQueue(new LinkedList<String>());
		setUrlFilter(new URLFilter());
		setBaseURL(baseURL);
	}

	/**
	 * 从爬取的网页信息中抽取URLS
	 * @param content:爬取的网页信息
	 * @return Queue<String> 
	 */
	public Queue<String> fetchUrls(String content) {
		if (content.isEmpty() || content.equals(null) || (content == null))
			return null;
		baseURL = baseURL.substring(baseURL.lastIndexOf("/") + 1,
				baseURL.length()).contains(".") ? baseURL.substring(0,
				baseURL.lastIndexOf("/")) : baseURL;
		Document doc = Jsoup.parse(content, baseURL);
		Elements links = doc.getElementsByTag("a");
		if (!links.isEmpty()) {
			for (Element link : links) {
				String linkHref = link.absUrl("href");
				String linkText = link.text();
				if (getUrlFilter().isURL(linkHref)
						&& getUrlFilter().isPCOnlineURL(linkHref)) {
					urlMap.put(linkHref, linkText);
					// System.out.println(linkText + ":" + linkHref);
				}
			}
			urlQueue.addAll(urlMap.keySet());
			urlMap.clear();
			// System.out.println(urlQueue.size());
			return urlQueue;
		}
		return urlQueue;
	}

	/**
	 * 得到FetchURL的实例
	 * @param baseURL
	 * @return
	 */
	public static FetchURL getInstance(String baseURL) {
		return new FetchURL(baseURL);
	}
}
