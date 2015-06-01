package main.java.com.huateng.commons.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import main.java.com.huateng.util.IsWinOrLinux;
import main.java.com.huateng.util.XmlDomImp;


public class ServerStartup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ServerStartup() {
        super();
    }

	
	public void init(ServletConfig config) throws ServletException {
		String iniFilePath = "";
		XmlDomImp xmldom = new XmlDomImp();
		IsWinOrLinux iswinorlinux = new IsWinOrLinux();
		GN.isWinOrLinux = iswinorlinux.IsWinOrLinux();
		String sysPath = config.getServletContext().getRealPath("/");			//取得工程的绝对路径
		if(GN.isWinOrLinux == 1){
			iniFilePath = config.getServletContext().getRealPath("/") + "init\\filePath_windows.xml";
		}else{
			iniFilePath = config.getServletContext().getRealPath("/") + "init/filePath_linux.xml";
		}
		GN.backupfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "backupfilepath");
		GN.receivefilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "receivefilepath");
		GN.sendfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "sendfilepath");
		GN.bankreqtempreqfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "bankreqtempreqfilepath");
		GN.CRMreqtempreqfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "CRMreqtempreqfilepath");
		GN.CRMtemprepfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "CRMtemprepfilepath");
		GN.banktemprepfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "banktemprepfilepath");
		GN.mmallreqtempreqfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "mmallreqtempreqfilepath");
		GN.mmalltemprepfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "mmalltemprepfilepath");
		GN.tmallreqtempreqfilepath = sysPath + xmldom.geSonNdVal(iniFilePath, "tmallreqtempreqfilepath");
		GN.tmalltemprepfilepath  = sysPath + xmldom.geSonNdVal(iniFilePath, "tmalltemprepfilepath");
	}

}
