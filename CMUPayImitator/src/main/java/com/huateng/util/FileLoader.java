package main.java.com.huateng.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 将响应报文加载到内存中
 * 
 * @author zeng.j
 * 
 */
public class FileLoader {
	public static final String CRM_RSP_PATH = "file" + File.separator
			+ "template" + File.separator + "response" + File.separator + "CRM";
	public static final String BANK_RSP_PATH = "file" + File.separator
			+ "template" + File.separator + "response" + File.separator
			+ "bank";

	public Map<String, String> load(String sysPath) {
		File crmDir = new File(sysPath + CRM_RSP_PATH);
		File bankDir = new File(sysPath + BANK_RSP_PATH);
		File[] crmFiles = crmDir.listFiles();
		File[] bankFiles = bankDir.listFiles();

//		System.out.println("dir path:" + crmDir.getPath());

		// TODO 将响应报文加载到map中
		Map<String, String> map = new HashMap<String, String>();
		for (File file : crmFiles) {
			String fileName = file.getName();
			if (fileName.endsWith(".xml")) {
				String key = fileName.substring(0, fileName.indexOf(".xml"));
				String cont = parseStringFromFile(file);
				map.put(key, cont);
			}
		}

		for (File file : bankFiles) {
			String fileName = file.getName();
			if (fileName.endsWith(".xml")) {
				String key = fileName.substring(0, fileName.indexOf(".xml"));
				String cont = parseStringFromFile(file);
				map.put(key, cont);
			}
		}
		return map;
	}

	/**
	 * 读取文件
	 * 
	 * @param file
	 * @return
	 */
	private String parseStringFromFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file),Charset.forName("UTF-8")));
			StringBuffer sb = new StringBuffer();
			String line = br.readLine();
			while (null != line) {
				sb.append(line).append("\n");
				if (!"".equals(line)) {
					line = br.readLine();
				}
			}
			br.close();
			if (null != line && !"".equals(line)) {
				sb.append(line);
				sb.append("\n");
			}

			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

	}
}
