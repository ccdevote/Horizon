package my.spider.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MirrorWriter implements Writer {
	private static final String DEFAULT_SUB = ".html";
	private static Log log = LogFactory.getLog(MirrorWriter.class.getName());
	
	/**
	 * 下载文件
	 * 
	 * @param url
	 *            文件http地址
	 * @param dir
	 *            目标文件
	 * @throws IOException
	 */
	public  void downloadFile(String url, String dir,
			String content)  {
		if (url == null || url.equals("")) {
			log.error("url is null!!!");
			return;
		}
		if (dir == null || "".equals(dir)) {
			log.error("dir is null!!!(the dir mustn't be null");
			return;
		}
		// File.separator is in window is "\" in linux is "/"
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		try {
			File desPathFile = new File(dir);
			if (!desPathFile.exists()) {
				log.info("create dir" + desPathFile.getPath());
				desPathFile.mkdirs();
			}
		} catch (Exception e) {
			log.error("failed to mkdir" + e.getMessage());
		}
		// remove the "http://" from the url
		String picName = url.substring(url.indexOf("/") + 2, url.length());
		boolean NoSub = false;
		if (picName.indexOf("/") == -1
				|| !picName.substring(picName.lastIndexOf("/") + 1,
						picName.length()).contains(".")) {
			NoSub = true;
		}
		String fullPath = dir;
		// System.out.println(fullPath);
		String dirs[] = picName.split("/");
		for (int i = 0; i < dirs.length; i++) {
			if (i == 0) {
				fullPath += dirs[0];
				continue;
			}
			// System.out.println(fullPath + "    >>>>>" + i);
			fullPath += "/" + dirs[i];

		}
		// System.out.println(fullPath);
		if (NoSub) {
			if (fullPath.endsWith("/"))
				fullPath += "index" + DEFAULT_SUB;
			else
				fullPath += "/index" + DEFAULT_SUB;
		}
		// System.out.println(fullPath);
		log.info(fullPath);
		File file;
		FileOutputStream output = null;
		try {
			file = new File(fullPath);
			output = new FileOutputStream(file);
			if(!file.exists()){
				file.createNewFile();
			}
			output.write(content.getBytes());
		} catch (IOException e1) {
			log.error("openOutputStream(file)失败>>>>>\n"+e1.getMessage());
		}
		try {
			log.info("write file");
		
			//IOUtils.write(content.getBytes(), output);
		} catch (Exception e) {
			log.info("Error in write file>>>>>>>>\n" + e.getMessage());
		} finally {
			try {
				output.flush();
				log.info("flush output");
				output.close();
				log.info("close IO");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//IOUtils.closeQuietly(output);
		}

	}

	@Override
	public synchronized void downLoadImage(String url, String dir) {
		
	}


}
