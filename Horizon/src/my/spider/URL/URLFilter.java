package my.spider.URL;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFilter {
	private static StringSearch ss;
	private Set<String> pconlineURL=new HashSet<String>();
	public boolean isURL(String url){
		 Pattern abs_pattern=Pattern.compile("^[a-zA-z]+://[^\\s]*");
		 Pattern rela_pattern=Pattern.compile("![http://][0-9a-zA-Z]/*");
		 Matcher matcher=abs_pattern.matcher(url);
		 if(matcher.matches()){
			 return true;
		 }else{
			 matcher=rela_pattern.matcher(url);
			 if(matcher.matches()){
				 return true;
			 }
		 }
		 return false;
	}
	public boolean isPCOnlineURL(String url){
		pconlineURL.add("http://product.pconline.com.cn/mobile/");
		pconlineURL.add("http://product.pconline.com.cn/pdlib/");
		pconlineURL.add("img.pconline.com.cn/images/product/");
		ss=new StringSearch(pconlineURL);
		long length=ss.findAll(url).length;
		//System.out.println(length);
		if(length>=1){
			return true;
		}
			
		return false;
	}
}
