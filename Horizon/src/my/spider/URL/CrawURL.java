package my.spider.URL;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

public class CrawURL implements Serializable {

	private static final long serialVersionUID = 4561213939476814841L;
	public CrawURL() {
		// TODO Auto-generated constructor stub
	}
	private String criUrl;//原始URL值，主机部分是域名
	private String url;   //URL值，主机部分是IP，为了防止重复主机的出现
	private int urlNO;	  //URL 的编号
	private int statusCode;//获取URL返回的结果码
	private int hitNum;	  //此URL被其他文章引用的次数
	private String abstractText;//文章摘要
	private String author;//作者
	private int wieght;	  //文章权重，包含的导向词的信息
	private String description;//文章描述
	private int fileSize;//文章大小
	private Timestamp lastUpdateTime;//最后修改时间
	private Date timeToLive;//过期时间
	private String title;//文章名称
	private String type;  //文章类型
	private String[] urlRefrences;//引用的链接
	private int layer;		//爬取的层次，从种子开始，依次为第0层，第1层……第n层
	public String getCriUrl() {
		return criUrl;
	}
	public void setCriUrl(String criUrl) {
		this.criUrl = criUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getUrlNO() {
		return urlNO;
	}
	public void setUrlNO(int urlNO) {
		this.urlNO = urlNO;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public int getHitNum() {
		return hitNum;
	}
	public void setHitNum(int hitNum) {
		this.hitNum = hitNum;
	}
	public String getAbstractText() {
		return abstractText;
	}
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getWieght() {
		return wieght;
	}
	public void setWieght(int wieght) {
		this.wieght = wieght;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Date getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(Date timeToLive) {
		this.timeToLive = timeToLive;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String[] getUrlRefrences() {
		return urlRefrences;
	}
	public void setUrlRefrences(String[] urlRefrences) {
		this.urlRefrences = urlRefrences;
	}
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	

}
