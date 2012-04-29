package my.spider.extractor;

import java.io.BufferedWriter;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;

public abstract class Extractor {
	public void extract() {
		BufferedWriter bw = null;
		//创建属性过滤器
		NodeFilter attributes_filter = new AndFilter(new TagNameFilter("td"),
				new OrFilter(new HasAttributeFilter("class", "td1"),
						new HasAttributeFilter("class", "td2")));
		//创建标题过滤器
		NodeFilter title_filter = new TagNameFilter("h1");
		//创建图片过滤器
		NodeFilter image_filter = new AndFilter(new TagNameFilter("img"), new HasAttributeFilter("class","bigimg"));
		//提取标题信息
		

	}

}
