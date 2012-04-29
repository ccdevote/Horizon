package my.spider.URL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;

/**
 * <h1>URL去重（布隆过滤器）</h1> 如何不采集重复的网页？去重可以使用布隆过滤器，每个线程使用一个BitSet
 * 里面保存本批源页面上次抓取的页面的哈希值情况，抓取下来的源页面分析链接后，
 * 去这个BitSet里判断以前有没有抓过这个页面，没有的话就抓下来，抓过的话就不管了。
 * 
 * @author 小猪
 * @date 2012-04-23
 * @version 0.0.1
 */
public class SimpleBloomFilter {
	private static final int DEFAULT_SIZE = 2 << 24;
	private static final int[] seeds = new int[] { 7, 11, 13, 31, 37, 61 };
	private int size;
	private BitSet bits;
	private int count;
	private SimpleHash[] func = new SimpleHash[seeds.length];

	/**
	 * 构造方法，初始化url集合（bits）和哈希表，默认大小为24位
	 */
	public SimpleBloomFilter() {

		bits = new BitSet(DEFAULT_SIZE);
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
	}

	/**
	 * 构造方法，初始化url集合（bits）和哈希表，大小为指定大小
	 * 
	 * @param number
	 *            用于指定bits和哈希表的大小，单位是 位
	 */
	public SimpleBloomFilter(int number) {

		size = 2 << number;
		bits = new BitSet(size);
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(size, seeds[i]);
		}
	}

	/**
	 * 此方法用于构建url集合（bits），每次都将一个url加入集合
	 * 
	 * @param url
	 *            需要添加进集合的url
	 */
	public void add(String url) {
		for (SimpleHash f : func)
			bits.set(f.hash(url), true);
		count++;
	}

	/**
	 * 此方法用于判断是否包含指定url
	 * 
	 * @param url
	 *            需要作判断的url
	 * @return 返回判断的结果
	 */
	public boolean contains(String url) {
		if (url == null) {
			return false;
		}
		boolean ret = true;
		for (SimpleHash f : func) {
			ret = ret && bits.get(f.hash(url));
		}
		return ret;
	}

	/**
	 * 此方法作用是将url集合（bits）保存成文件，以便以后使用
	 * 
	 * @param filename
	 *            保存的文件名
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveBit(String filename) throws FileNotFoundException,
			IOException {
		File file = new File(filename);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				file, false));
		oos.writeObject(bits);
		oos.flush();
		oos.close();
	}

	/**
	 * 此方法用于读取上次保存的url集合文件
	 * 
	 * @param filename
	 *            需要读取的文件名
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readBit(String filename) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		File file = new File(filename);
		if (!file.exists()) {
			return;
		}
		bits.clear();
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		bits = (BitSet) ois.readObject();
		ois.close();
	}

	public static class SimpleHash {
		private int cap;
		private int seed;

		public SimpleHash(int cap, int seed) {
			this.cap = cap;
			this.seed = seed;
		}

		public int hash(String value) {
			int result = 0;
			int len = value.length();
			for (int i = 0; i < len; i++) {
				result = seed * result + value.charAt(i);
			}
			return (cap - 1) & result;
		}
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}
}