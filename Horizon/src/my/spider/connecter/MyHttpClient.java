package my.spider.connecter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import javax.net.ssl.SSLHandshakeException;
import my.spider.URL.SimpleBloomFilter;
import my.spider.parse.FetchURL;
import my.spider.writer.MirrorWriter;
import my.spider.writer.WriterFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * @author MZY
 * @date : 2012-04-23
 * @describe:
 */
public class MyHttpClient {
	private static final String CHARSET_UTF8 = "UTF-8";
	private static final String DEFAULT_SUB = ".html";
	private static ClientConnectionManager cm = null;
	private static DefaultHttpClient httpClient;
	private static Log log = LogFactory.getLog(MyHttpClient.class.getName());
	// 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		// 自定义的恢复策略
		public boolean retryRequest(IOException exception, int executionCount,
				HttpContext context) {
			// 设置恢复策略，在发生异常时候将自动重试5次
			if (executionCount >= 5) {
				// Do not retry if over max retry count
				log.error("connecting to target url timeout...");
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				log.info("retry to connect to the url CAUSE: server dropped connection on us!");
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				log.info("SSL handshake Exception,we will not retry.");
				return false;
			}
			HttpRequest request = (HttpRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				log.info("Retry. the request is considered idempotent");
				return true;
			}
			return false;
		}
	};
	// 使用ResponseHandler接口处理响应，HttpClient使用ResponseHandler会自动管理连接的释放，解决了对连接的释放管理
	private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		// 自定义响应处理
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			// 处理重定向
			StatusLine statusLine = response.getStatusLine();
			int statucode = statusLine.getStatusCode();
			if (statucode == HttpStatus.SC_MOVED_TEMPORARILY
					|| statucode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statucode == HttpStatus.SC_SEE_OTHER
					|| statucode == HttpStatus.SC_TEMPORARY_REDIRECT) {
				// System.out.println("Redirect:");
				log.info("Redirect:");
				Header header = response.getFirstHeader("location");
				if (header != null) {
					String newuri = header.getValue();
					if ((newuri == null) || (newuri.equals("")))
						newuri = "/";
					log.info("To:" + newuri);
					try {
						return doGet(newuri);
					} catch (Exception e) {
						log.info("redirect Exception");
					}
				}
				log.info("Invalid redirect");
			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String charset = EntityUtils.getContentCharSet(entity) == null ? CHARSET_UTF8
						: EntityUtils.getContentCharSet(entity);
				return new String(EntityUtils.toByteArray(entity), charset);
			} else {
				return null;
			}
		}
	};

	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
				.getSocketFactory()));

		cm = new ThreadSafeClientConnManager(schemeRegistry);
		httpClient = new DefaultHttpClient(cm);

		HttpProtocolParams
				.setUserAgent(
						httpClient.getParams(),
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.9) Gecko/20100315 Firefox/3.5.9");
		httpClient.getParams().setParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		httpClient.getParams().setParameter(
				CoreProtocolPNames.HTTP_CONTENT_CHARSET, CHARSET_UTF8);
		httpClient.setHttpRequestRetryHandler(requestRetryHandler);
	}

	/**
	 * 处理GET请求，返回整个页面
	 * 
	 * @param url
	 *            访问地址
	 * @param params
	 *            编码参数
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public static synchronized String doGet(String url) {
		HttpGet httpget = new HttpGet(url);
		String content=null;
		try {
			content = httpClient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			log.error("ClientProtocolException in doGet("+url+")"+"   \n"+e.getMessage());
		} catch (IOException e) {
			log.error("IOException  in doGet("+url+")"+" >>>>:  \n      "+e.getMessage()+">>> \n");
			e.printStackTrace();
		}
		httpget.abort();
		return content;
	}

	public static void release() {
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public static void main(String[] args) {

		int count = 0;
		try { //
				// System.err.println(doGet("http://www.baidu.com"));
			String first = "http://mobile.pconline.com.cn/";
			Queue<String> urlQueue = new LinkedList<String>();
			SimpleBloomFilter fetchedURL = new SimpleBloomFilter();
			urlQueue.add(first);
			String content = null;
			Date before = new Date();
			System.out.println("Start>>>>>>" + before.getTime());
			while (!urlQueue.isEmpty()) {
				String url = urlQueue.remove();
				log.info("Fetching: " + url);
				if (url == null || url.equals(""))
					return;
				if (fetchedURL.contains(url))
					continue;
				if (count == 0) {
					content = doGet(url);
				}
				if(content!=null){
					urlQueue.addAll(FetchURL.getInstance(url).fetchUrls(content));
					WriterFactory.getWriter("MirrorWriter").downloadFile(url, "/home/mzy/NewPCOnline/", content);
					fetchedURL.add(url);
				}
				count++;
				System.out.println("fetch count: " + count);
			}
			Date after = new Date();
			System.out.println("Total:>>>>>"
					+ (after.getTime() - before.getTime()) / 1000);
			//
			// downloadFile(u, "/home/mzy/NewPage/");
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}