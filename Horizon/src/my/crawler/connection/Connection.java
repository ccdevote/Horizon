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
 * ������Ŀ����ַ������ ����Ŀ��ҳ��<br>
 * ��Ҫ���̣�<br>
 * 
 * <pre>
 * 		ͨ����̬����URLCon����SocketCon������Ŀ����ַ������
 * �����صĶ���������ΪgetInputStreamReader�����Ĳ������Ӷ��õ�
 * InputStreamReader�����õ�deInputStreamReader���󴫵ݸ�
 * downPage�������Ӷ�ʵ��ҳ������
 * </pre>
 * 
 * @author mzy
 * @version 0.0.1
 */
public class Connection {

	// private Logger logger=Logger.getLogger(Connection.class.getName());
	/**
	 * ͨ��URL�ķ�ʽ��������
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
	 * ͨ��socekt��ʽ��������
	 * 
	 * @param path
	 * @param params
	 * @return Socket
	 */
	public static Socket SocketCon(String path, String... params) {
		String host = path;// ������
		String file = "/index.html";// ��ҳ·��
		int port = 80;
		try {
			Socket s = new Socket(host, port);
			OutputStream out = s.getOutputStream();
			PrintWriter writer = new PrintWriter(out, false);
			writer.print(params[0] + file + " HTTP/1.0\r\n");
			writer.print("Accept: text/plain, text/html, text/*\r\n");
			writer.print("\r\n");
			writer.flush();// ����GET

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
	 * ʹ��BufferedReader����ʽ��ȡ����
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
	 * ʹ��Scanner��ȡ����
	 * 
	 * @param isr
	 * @param pattern
	 * @return
	 */
	public static String downPage(InputStreamReader isr, String pattern) {
		Scanner scanner = new Scanner(isr);
		scanner.useDelimiter(pattern);// ͨ��������ʽ��ȡ
		StringBuilder pageBuffer = new StringBuilder();
		while (scanner.hasNext()) {
			pageBuffer.append(scanner.next());
		}
		return pageBuffer.toString();
	}

	/**
	 * ��Socket ����Ϊ���� ����InputStreamReader �ṩ��downPageʹ��
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
	 * ��URLΪ��������InputStreamReader �ṩ��downPageʹ��
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
	 * ȡ����վ��Ӧ��ͷ��Ϣ
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
