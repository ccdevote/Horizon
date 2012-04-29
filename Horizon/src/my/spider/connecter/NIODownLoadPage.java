package my.spider.connecter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Connect to a website and down some page we want by NIO<br/>
 * 
 * @author MZY
 * @version 0.0.1
 */
public class NIODownLoadPage {
	private static Selector sel = null;
	private static Map<SocketChannel, String> socket2path = new HashMap<SocketChannel, String>();

	/**
	 * build connect by this param
	 * 
	 * @param ip
	 * @param path
	 * @param port
	 */
	public void setConnect(String ip, String path, int port) {
		try {
			SocketChannel client = SocketChannel.open();
			client.configureBlocking(false);
			client.connect(new InetSocketAddress(ip, port));

			client.register(sel, SelectionKey.OP_CONNECT | SelectionKey.OP_READ
					| SelectionKey.OP_WRITE);
			socket2path.put(client, path);

		} catch (Exception e) {
			System.out.println("<<ERROR>> in setConnect");
		}

	}

	/**
	 * @param selKey
	 */
	public void processSelectionKey(SelectionKey selKey) {
		
		SocketChannel schannel = (SocketChannel) selKey.channel();
		if (selKey.isValid() && selKey.isConnectable()) {
			try {
				boolean success = schannel.finishConnect();
				if (!success) {
					selKey.cancel();
				}
				sendMessage(schannel, "GET " + socket2path.get(schannel)
						+ " HTTP/1.0\r\nAccept: */*\r\n\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (selKey.isReadable()) {
			String content = readMessage(schannel);
			if (content != null && content.length() > 0) {
				System.out.println("======================"+selKey.channel().keyFor(sel)+"===================");
				System.out.println(content);
				
			} else {
				selKey.cancel();
			}
		}
	}

	/**
	 * Send a message to target SocketChannel
	 * 
	 * @param schannel
	 * @param msg
	 * @return
	 */
	private boolean sendMessage(SocketChannel schannel, String msg) {
		try {
			ByteBuffer buf = ByteBuffer.allocate(1024);
			buf = ByteBuffer.wrap(msg.getBytes());
			schannel.write(buf);
		} catch (IOException e) {
			return true;
		}
		return false;
	}

	/**
	 * down page
	 * 
	 * @param schannel
	 * @return
	 */
	private String readMessage(SocketChannel schannel) {
		String result = null;
		ByteBuffer buf = ByteBuffer.allocate(1024);
		try {
			int i = schannel.read(buf);
			buf.flip();
			if (i != -1) {
				result = new String(buf.array(), 0, i);
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("null")
	public static void main(String args[]) {
		NIODownLoadPage ndlp=null;
		try {
			sel = Selector.open();
			ndlp = new NIODownLoadPage();
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		ndlp.setConnect("www.baidu.com", "/index.htm", 80);
		ndlp.setConnect("www.lietu.com", "/index.jsp", 80);
		while (!sel.keys().isEmpty()) {
			SelectionKey key = null;
			try {
				if (sel.select(100) > 0) {
					Iterator<SelectionKey> it = sel.selectedKeys().iterator();
					while (it.hasNext()) {
						key = it.next();
						it.remove();
						ndlp.processSelectionKey(key);
						
					}
				}
			} catch (IOException e) { // TODO Auto-generated catch block
				key.cancel();
			}
		}
	}

}
