package my.spider.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.net.ssl.SSLHandshakeException;
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
 * @author MZY<ccdevote@gmail.com>
 * @date 2012-04-18 10:51
 * @version0.0.1
 * 
 */
public class HttpClientManager {

	private static ThreadSafeClientConnManager cm;
	public final static int MAX_TOTAL_CONNECTIONS = 200;
	public final static int MAX_ROUTE_CONNECTIONS = 400;
	public final static int CONNECT_TIMEOUT = 100000;
	public final static int WAITING_FOR_CONTINUE = 5000;
	private static final String CHARSET_UTF8 = "UTF-8";
	private DefaultHttpClient httpClient;
	private static final String DEFAULT_SUB = ".html";
	private boolean WRITE;
	private String dir;
	private String url;
	private static Log log = LogFactory.getLog(HttpClientManager.class
			.getName());

	public HttpClientManager(boolean write, String dir) {
		this.WRITE = write;
		this.dir = dir;
		initHttpClient();
	}

	public HttpClientManager() {
		initHttpClient();
	}

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
	private ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
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
				String content = new String(EntityUtils.toByteArray(entity),
						charset);
				if (WRITE)
					downloadFile(url, dir);
				return content;
			} else {
				return null;
			}
		}
	};

	public void initHttpClient() {
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
		// 设置访问间隔，缓解目标服务器压力,单位：毫秒
		httpClient.getParams().setParameter(
				CoreProtocolPNames.WAIT_FOR_CONTINUE, WAITING_FOR_CONTINUE);
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
	private static int count = 0;
	HttpGet httpget;

	public String doGet(String url) {
		httpget = new HttpGet(url);
		this.url = url;
		String content = null;
		try {
			content = httpClient.execute(httpget, responseHandler);
			log.info("第 " + (++count) + " 次抓取内容成功");
		} catch (ClientProtocolException e) {
			log.error("ClientProtocolException in doGet(" + url + ")" + "   \n"
					+ e.getMessage());
		} catch (IOException e) {
			log.error("IOException  in doGet(" + url + ")" + " >>>>:  \n      "
					+ e.getMessage() + ">>> \n");
			e.printStackTrace();
		}
		return content;
	}

	public void release() {
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
		if (httpget != null) {
			httpget.abort();
		}
	}

	public void downloadFile(String url, String dir) {
		String fullPath = dealDir(url, dir);
		FileOutputStream output = null;
		try {
			HttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream input = entity.getContent();
			File file = new File(fullPath);
			output = FileUtils.openOutputStream(file);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(output);
		}

	}

	private String dealDir(String url, String dir) {
		if (url == null || url.equals("")) {
			log.error("url is null!!!");
			return null;
		}
		if (dir == null || "".equals(dir)) {
			log.error("dir is null!!!(the dir mustn't be null");
			return null;
		}
		// File.separator is in window is "\" in linux is "/"
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		try {
			File desPathFile = new File(dir);
			if (!desPathFile.exists()) {
				log.info("create dir" + desPathFile.getPath());
				desPathFile.mkdirs();
			}
		} catch (Exception e) {
			log.error("failed to mkdir" + e.getMessage());
		}
		String picName = url.substring(url.indexOf("/") + 2, url.length());
		boolean NoSub = false;
		if (picName.indexOf("/") == -1
				|| !picName.substring(picName.lastIndexOf("/") + 1,
						picName.length()).contains(".")) {
			NoSub = true;
		}
		String fullPath = dir;
		String dirs[] = picName.split("/");
		for (int i = 0; i < dirs.length; i++) {
			if (i == 0) {
				fullPath += dirs[0];
				continue;
			}
			fullPath += "/" + dirs[i];
		}
		if (NoSub) {
			if (fullPath.endsWith("/"))
				fullPath += "index" + DEFAULT_SUB;
			else
				fullPath += "/index" + DEFAULT_SUB;
		}
		log.info(fullPath);
		return fullPath;
	}
}
