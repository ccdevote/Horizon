package my.spider.URL;

import java.util.HashSet;
import java.util.Set;

public class BloomFilterTest {
	private static Set<String> pconlineURL=new HashSet<String>();
	private static StringSearch ss;
	public static void main(String args[]){
		boolean flag=isPCOnlineURL("http://product.pconline.com.cn/pdlib/aaa");
		System.out.println(flag);
	}
	public static boolean isPCOnlineURL(String url){
		pconlineURL.add("http://product.pconline.com.cn/mobile/");
		pconlineURL.add("http://product.pconline.com.cn/pdlib/");
		pconlineURL.add("img.pconline.com.cn/images/product/");
		ss=new StringSearch(pconlineURL);
		long length=ss.findAll(url).length;
		System.out.println(length);
		if(length>=1){
			return true;
		}
			
		return false;
	}
}
