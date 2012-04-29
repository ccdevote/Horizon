package my.spider.writer;

import java.util.HashMap;
import java.util.Map;

/**
 * Writer工厂类，用来生成相应的Writer实例
 * 
 * @author MZY
 * 
 */
public class WriterFactory {
	private static Map<String, Class<? extends Writer>> map = new HashMap<String, Class<? extends Writer>>();
	private static Class<? extends Writer> writer;
	private static String WRITER_PACKAGE = "my.spider.wirter";

	/**
	 * 根据传入的class，生成实例
	 * 
	 * @param clazz
	 * @return Writer实例
	 */
	@SuppressWarnings("unchecked")
	public synchronized static Writer getWriter(String writerName) {
		writer = map.get(writerName);
		if (writer != null) {
			try {
				return writer.newInstance();
			} catch (InstantiationException e) {
				throw new WriterException(e, "实例化Writer类的方法出错");
			} catch (IllegalAccessException e) {
				throw new WriterException(e, "实例化Writer类的方法出错");
			}
		}
		try {
			String writerURI=WRITER_PACKAGE+"."+writerName;
			System.out.println(writerURI);
			System.out.println("my.spider.writer.MirrorWriter");
			writer = (Class<? extends Writer>) Class.forName("my.spider.writer.MirrorWriter");
			System.out.println(writer.getName());
			if (writer == null)
				throw new WriterException(null,
						"实例化Writer类的方法出错,未取得实例，或传入的实现类不符和要求");
			map.put(writerName, writer);
			return writer.newInstance();
		} catch (InstantiationException e) {
			throw new WriterException(e, "实例化Writer类的方法出错");
		} catch (IllegalAccessException e) {
			throw new WriterException(e, "实例化Writer类的方法出错");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new WriterException(e, "实例化Writer类的方法出错:未找到相应的类");
		}
	}
}
