package com.huateng.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * 文件处理工具类
 * 
 * @author Gary
 * 
 */
public class FileUtil {
	private static Logger logger = Logger.getLogger(FileUtil.class);

	/**
	 * 读取文件,路径名和文件名之间已添加分隔符
	 * 
	 * @param fileName
	 *            文件路径
	 * @return 返回文件内容
	 */
	public static String readFile(String path, String fileName) {
		String fullPath = path + File.separator + fileName;
		if (!FileUtil.isExist(fullPath)) {
			logger.debug("目录" + path + "下不存在文件" + fileName);
			return "目录" + path + "下不存在文件" + fileName;
		}
		String result = "";
		FileInputStream fis = null;
		BufferedReader br = null;
		InputStreamReader is = null;
		try {
			fis = new FileInputStream(fullPath);
			is = new InputStreamReader(fis);
			br = new BufferedReader(is);
			String line = "";
			StringBuffer buf = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buf.append(line);
			}
			result = buf.toString();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());			
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (is != null) {
					is.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 *            文件路径
	 * @return 返回 true or false
	 */
	public static boolean isExist(String fullPath) {
		File file = new File(fullPath);
		if (!file.exists()) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		URL url = FileUtil.class.getClassLoader().getResource("/config/ProvinceCode.xml");
		System.out.println(FileUtil.readFile("", url.getPath()));
	}

}
