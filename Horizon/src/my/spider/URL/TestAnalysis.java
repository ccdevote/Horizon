package my.spider.URL;

public class TestAnalysis {
	public static void main(String args[]){
		String url=" http://product.pconline.com.cn/mobile/samsung";
		String dir="/home/mzy/";
		String DEFAULT_SUB=".html";
		String picName = url.substring(url.indexOf("/") + 2, url.length());
		boolean NoSub = false;
		if (picName.indexOf("/") == -1
				|| !picName.substring(picName.lastIndexOf("/") + 1,
						picName.length()).contains(".")) {
			NoSub = true;
		}
		String fullPath = dir;
		System.out.println(fullPath);
		String dirs[] = picName.split("/");
		for (int i = 0; i < dirs.length; i++) {
			if (i == 0) {
				fullPath += dirs[0];
				continue;
			}
			System.out.println(fullPath+"    >>>>>"+i);
			fullPath += "/" + dirs[i];

		}
		System.out.println(fullPath);
		if (NoSub) {
			if (fullPath.endsWith("/"))
				fullPath += "index" + DEFAULT_SUB;
			else
				fullPath += "/index" + DEFAULT_SUB;
		}
		System.out.println(fullPath);
	}
}
