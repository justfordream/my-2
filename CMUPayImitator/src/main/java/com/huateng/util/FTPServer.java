package main.java.com.huateng.util;

import org.apache.commons.io.IOUtils; 
import org.apache.commons.net.ftp.FTPClient; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.io.FileOutputStream;
import java.net.InetAddress;

public class FTPServer {
	   /** 
     * FTP上传单个文件测试 
	 * @throws IOException 
     */ 
    public int FTPUpload(String IP, int port, String filePath, String uploadPath, String sysuser, String passward) throws IOException { 
        FTPClient ftpClient = new FTPClient(); 
        FileInputStream fis = null; 
        String fileName = "";
        
        //System.out.println("filePath.lastIndexOf(/) = " + filePath.lastIndexOf("/"));
        //System.out.println("filePath.lastIndexOf(\\) = " + filePath.lastIndexOf("\\"));
        if(filePath.lastIndexOf("/") > 0){
        	fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
        }
        
        if(filePath.lastIndexOf("\\") > 0){
        	fileName = filePath.substring(filePath.lastIndexOf("\\")+1, filePath.length());
        }
        
        try { 
            ftpClient.connect(IP); 
            ftpClient.login(sysuser, passward); 

            File srcFile = new File(filePath); 
            fis = new FileInputStream(srcFile); 
            //设置上传目录 
            ftpClient.changeWorkingDirectory(uploadPath); 
            ftpClient.setBufferSize(4096); 
            ftpClient.setControlEncoding("GBK"); 
            //设置文件类型（二进制） 
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            ftpClient.storeFile(fileName, fis); 
        } catch (IOException e) {
            e.printStackTrace(); 
            return 0;//失败
        } finally { 
            IOUtils.closeQuietly(fis); 
            
            try { 
                ftpClient.disconnect(); 
            } catch (IOException e) { 
            	e.printStackTrace(); 
            	return 0;//失败
            } 
        } 
        fis.close();
        return 1;//上传成功
    } 

    /** 
     * FTP下载单个文件测试 
     * @throws IOException 
     */ 
    public static void FTPDownload() throws IOException { 
        FTPClient ftpClient = new FTPClient(); 
        FileOutputStream fos = null; 

        try { 
            ftpClient.connect("192.168.14.117"); 
            ftpClient.login("admin", "123"); 

            String remoteFileName = "/admin/pic/3.gif"; 
            fos = new FileOutputStream("c:/down.gif"); 

            ftpClient.setBufferSize(1024); 
            //设置文件类型（二进制） 
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            ftpClient.retrieveFile(remoteFileName, fos); 
        } catch (IOException e) { 
            e.printStackTrace(); 
            throw new RuntimeException("FTP客户端出错！", e); 
        } finally { 
            IOUtils.closeQuietly(fos); 
            fos.close();
            try { 
                ftpClient.disconnect(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
                throw new RuntimeException("关闭FTP连接发生异常！", e); 
            } 
        } 
    } 
	
}
