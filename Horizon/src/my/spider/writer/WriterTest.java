package my.spider.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WriterTest {
	public static void main(String args[]) throws IOException{
		//MirrorWriter writer=new MirrorWriter();
		//writer.downloadFile("http://product.pconline.com.cn/mobile/p169/index.html", "/home/spider/", "中华人们共和国");
	/*	File dir=new File("/home/mzy/FileTest/");
		
		try {
			if(!dir.exists())
				dir.mkdir();
			File file=new File(dir+"test.html");
			if(!file.exists())file.createNewFile();
			OutputStream os = new FileOutputStream(file);
			os.write("中国".getBytes());
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		
		}*/
		File file = new File("/home/mzy/FileTesttest.html");
		File file2 = new File("/home/mzy/FileTesttest2.html");
		try {
			InputStream in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(file2);
			int i;
			String str=null;
			//byte[] b=new byte[64];
			//通过available方法取得流的最大字符数
			byte[] inOutb = new byte[in.available()];
			in.read(inOutb);  //读入流,保存在byte数组 \
			System.out.println(str=new String(inOutb));
			out.write(str.getBytes());
			out.close();
			in.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
