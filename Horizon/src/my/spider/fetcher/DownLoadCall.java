package my.spider.fetcher;

import java.util.concurrent.Callable;

import my.crawler.connection.Connection;
import my.spider.parse.FetchURL;

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
