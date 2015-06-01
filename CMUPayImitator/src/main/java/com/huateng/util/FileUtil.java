package main.java.com.huateng.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.java.com.huateng.commons.config.GN;

/**
 * 
 * @version 1.0
 */
public class FileUtil {
	public String filename;

	public FileUtil() {
	}

	public String fileToStr(String as_filePath) {
		String ls_str = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					as_filePath));
			String line;

			// String ls_row[]=null;
			while ((line = reader.readLine()) != null) {
				ls_str += line + "\n";
				// ls_row = StringUtil.split(line,"\t");
			}

			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ls_str;
	}

	public String fileToStrUTF8(String filePath) {
		String str = "";
		try {
			File fileName = new File(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "UTF-8"));
			String line;

			// String ls_row[]=null;
			while ((line = br.readLine()) != null) {
				str += line + "\n";
				// ls_row = StringUtil.split(line,"\t");
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String rs = str;
		
		if(str.indexOf("<TransactionID>")>-1&&str.indexOf("</TransactionID>")>-1){
			rs = XmlStringUtil.relaceNodeContent("<TransactionID>",
					"</TransactionID>", UUIDGenerator.generateUUID(), str);
		}
		return rs;
	}

	public static ArrayList<String> fileToArrayList(String as_filePath) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					as_filePath));
			String line;
			// String ls_row[]=null;
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public void strToFile(String as_str, String as_filePath, boolean append) {
		try {
			BufferedReader reader = new BufferedReader(new StringReader(as_str));
			File file_filePath = new File(as_filePath);// yan_add_20061203
			if (!file_filePath.exists()) {// yan_add_20061203
				String ls_filePath_parent = file_filePath.getParent();
				File file_filePath_parent = new File(ls_filePath_parent);
				file_filePath_parent.mkdirs();
			}

			PrintWriter out = new PrintWriter(new FileWriter(as_filePath,
					append));// as_filePath yan_modi 20061203
			while ((as_str = reader.readLine()) != null) {// yan_modi 20061127
				out.println(as_str);
			}
			out.close();
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void strToFile(String as_str, String as_filePath) {
		strToFile(as_str, as_filePath, false);
	}

	public ArrayList<String> getFileNameByPath(String as_filePath) {
		ArrayList<String> al_fileList = new ArrayList<String>();
		File lf_filePath = new File(as_filePath);
		File[] lf_tmpFileList = null;

		if (!lf_filePath.exists())
			lf_filePath.mkdir();
		if (lf_filePath.isDirectory()) {
			lf_tmpFileList = lf_filePath.listFiles();
		}

		for (int i = 0; i < lf_tmpFileList.length; i++) {
			if (lf_tmpFileList[i].isFile()) {
				al_fileList.add(lf_tmpFileList[i].getPath());
				// System.out.println(lf_tmpFileList[i].getName());
			}
		}

		return al_fileList;
	}

	public Map<String, String> getFileNameAndPath(String as_filePath) {
		Map<String, String> al_fileList = new HashMap<String, String>();
		File lf_filePath = new File(as_filePath);
		File[] lf_tmpFileList = null;

		if (!lf_filePath.exists())
			lf_filePath.mkdir();
		if (lf_filePath.isDirectory()) {
			lf_tmpFileList = lf_filePath.listFiles();
		}

		for (int i = 0; i < lf_tmpFileList.length; i++) {
			if (lf_tmpFileList[i].isDirectory()) {
				al_fileList.put(lf_tmpFileList[i].getName(),
						lf_tmpFileList[i].getPath());
			}
		}

		return al_fileList;
	}

	public Map<String, String> getFileNameAndFile(String as_filePath) {
		Map<String, String> al_fileList = new HashMap<String, String>();
		File lf_filePath = new File(as_filePath);
		File[] lf_tmpFileList = null;

		if (!lf_filePath.exists())
			lf_filePath.mkdir();
		if (lf_filePath.isDirectory()) {
			lf_tmpFileList = lf_filePath.listFiles();
		}

		for (int i = 0; i < lf_tmpFileList.length; i++) {
			if (!lf_tmpFileList[i].isDirectory()) {
				al_fileList.put(lf_tmpFileList[i].getName(),
						lf_tmpFileList[i].getPath());
			}
		}

		return al_fileList;
	}

	public static String getFileParentDirName(String fileName) {
		File file = new File(fileName);
		return file.getParent();
	}

	public static boolean isModified() {
		boolean mflag = false;
		long lastTime = 0;
		File file = new File("");
		long newTime = file.lastModified();
		if (newTime > lastTime) {
			mflag = true;
		}
		file = null;
		return mflag;
	}

	public void createFile_del(String as_fileNamePath) {
		File file = new File(as_fileNamePath);
		if (file.exists()) {
			file.delete();
		}

		File path = file.getParentFile();
		if (!path.exists()) {
			path.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	
	public void createFile(String as_fileNamePath) {
		File file = new File(as_fileNamePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void createDir(String pathName) {
		File file = new File(pathName);

		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void delAllFile(String filePath) {
		File file = new File(filePath);
		File[] fileList = file.listFiles();
		if (fileList != null)
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isFile())
					fileList[i].delete();
				if (fileList[i].isDirectory()) {
				}
			}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}


}
