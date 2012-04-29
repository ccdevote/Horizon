package my.crawler.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * 建立与目标网址的链接 下载目标页面<br>
 * 主要流程：<br>
 * 
 * <pre>
 * 		通过静态方法URLCon或者SocketCon建立与目标网址的链接
 * 将返回的对象用来作为getInputStreamReader方法的参数，从而得到
 * InputStreamReader，将得到deInputStreamReader对象传递给
 * downPage方法，从而实现页面下载
 * </pre>
 * 
 * @author mzy
 * @version 0.0.1
 */
public class Connection {

	// private Logger logger=Logger.getLogger(Connection.class.getName());
	/**
	 * 通过URL的方式建立链接
	 * 
	 * @param path
	 * @return URL
	 */
	public static URL URLCon(String path) {
		try {
			URL pageURL = new URL(path);
			return pageURL;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过socekt方式建立链接
	 * 
	 * @param path
	 * @param params
	 * @return Socket
	 */
	public static Socket SocketCon(String path, String... params) {
		String host = path;// 主机名
		String file = "/index.html";// 网页路经
		int port = 80;
		try {
			Socket s = new Socket(host, port);
			OutputStream out = s.getOutputStream();
			PrintWriter writer = new PrintWriter(out, false);
			writer.print(params[0] + file + " HTTP/1.0\r\n");
			writer.print("Accept: text/plain, text/html, text/*\r\n");
			writer.print("\r\n");
			writer.flush();// 发送GET

			return s;

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用BufferedReader的形式读取数据
	 * 
	 * @param isr
	 * @return
	 */
	public static String downPage(InputStreamReader isr) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(isr);
			String line;
			StringBuilder pageBuffer = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				pageBuffer.append(line + "\n");
			}
			reader.close();
			return pageBuffer.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 使用Scanner读取数据
	 * 
	 * @param isr
	 * @param pattern
	 * @return
	 */
	public static String downPage(InputStreamReader isr, String pattern) {
		Scanner scanner = new Scanner(isr);
		scanner.useDelimiter(pattern);// 通过正则表达式读取
		StringBuilder pageBuffer = new StringBuilder();
		while (scanner.hasNext()) {
			pageBuffer.append(scanner.next());
		}
		return pageBuffer.toString();
	}

	/**
	 * 以Socket 对象为参数 建立InputStreamReader 提供给downPage使用
	 * 
	 * @param s
	 * @return
	 */
	public static InputStreamReader getInputStreamReader(Socket s) {
		try {
			return new InputStreamReader(s.getInputStream());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以URL为参数建立InputStreamReader 提供给downPage使用
	 * 
	 * @param url
	 * @return
	 */
	public static InputStreamReader getInputStreamReader(URL url) {
		try {
			return new InputStreamReader(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 取得网站响应的头信息
	 * 
	 * @param url
	 * @param fieldKey
	 * @return
	 */
	public static String getHeaderFields(URL url, String fieldKey) {
		try {
			URLConnection con = url.openConnection();
			Iterator<String> iterator = con.getHeaderFields().keySet()
					.iterator();
			String key = null;
			StringBuffer sb = new StringBuffer();
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (key == null) {
					if (fieldKey == null) {
						return (String) ((List<?>) (con.getHeaderFields()
								.get(null))).get(0);
					}
				} else {
					if (key.equalsIgnoreCase(fieldKey)) {
						return (String) ((List<?>) (con.getHeaderFields()
								.get(key))).get(0);
					}else{
						sb.append(key+": "+(String) ((List<?>) (con.getHeaderFields()
								.get(key))).get(0)+"\n");
					}
				}
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
	
		// System.out.println(Connection.downPage(getInputStreamReader(URLCon("http://product.pconline.com.cn/mobile/"))));
		 System.out.println(getHeaderFields(URLCon("http://product.pconline.com.cn/mobile/"),""));
	}
}
