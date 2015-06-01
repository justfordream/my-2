package main.java.com.huateng.util;

/**
 * @date 2013-03-04
 * @author panliguan
 *	功能：判断是windows系统还是linux/unix系统
 */
public class IsWinOrLinux {
	
	public int IsWinOrLinux(){
		int flag = 1; //用来判断操作体系类型：1-windows系统；2-linux/unix系统
		
		String osName = System.getProperty("os.name");
	    if(osName.toLowerCase().indexOf("windows")>-1){//是window系统
	    	flag = 1;
	    }else{//其他系统
	    	flag = 2;
	    }
		
		return flag;
	}
	
	public static void main(String[] args) {
		IsWinOrLinux is = new IsWinOrLinux();

	}

}
