package my.spider.proccesser;

public class SpiderTest {
	public static void main(String args[]){
		String firstUrl="http://mobile.pconline.com.cn";
		String dir = "/home/mzy/spider";
		new Spider(4, firstUrl, dir).initThreadPool();
	}
}
