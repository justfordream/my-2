package main.java.com.huateng.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SocketClient {
	public SocketClient(){}
	
	public byte[] sendRequest(InetAddress serverIp, int serverPort, byte[] buffer, int timeout){
		if(serverIp==null || serverPort<=0 || buffer==null || buffer.length>9999) return null;
		if(timeout<=450000) timeout=450000;
		SocketChannel sc = null;
//		System.out.println();
//		System.out.println("开始：");
		try{
			sc = SocketChannel.open();
			sc.configureBlocking(true);
			sc.socket().setSoTimeout(timeout);
//			System.out.println("serverIp:" + serverIp);
//			System.out.println("serverPort:" + serverPort);
			sc.connect(new InetSocketAddress(serverIp, serverPort));
			
			sc.finishConnect();
			
			sc.write(ByteBuffer.wrap(buffer));
			
			ByteBuffer bfBody = ByteBuffer.allocate(4096);
			
			int length = sc.read(bfBody);
			
//			System.out.println("长度：" + length);
			byte[] bt = bfBody.array();
			byte[] bb = new byte[length];
			for(int i = 0; i < length; i++){
				bb[i] = bt[i];
			}
			sc.close();
			
			return bb;
		}catch(Exception e){
			System.err.println("error e :"+e.getMessage());
//			System.out.println("sc：" + sc.toString());
			
			try{ 
				if(sc != null && sc.isOpen()){
					sc.close(); 
				}
			}catch(Exception ex) {
				System.err.println("error ex :"+ex.getMessage());
			}
			
//			System.out.println("sc2：" + sc.toString());
			return null;
		}
	}
	
	
	public static void main(String[] agrs){
		byte[] rebuff = new byte[4096];
		//Message message = new Message();
		InetAddress IP = null;
		try {
			IP = InetAddress.getByName("127.0.0.1");
			//IP = InetAddress.getByName("10.3.3.104");
		} catch (UnknownHostException e) {
//			System.out.println("这里出差错");
			e.printStackTrace();
		}
		
		SocketClient client = new SocketClient();
		//rebuff = message.getMessage();
		//System.out.println("客户端向服务器端发�?数据�? + new String(rebuff)); 

//		System.out.println();
//		System.out.print("写  入   的：");
		for(int i = 0; i < rebuff.length; i++){
//			System.out.print(rebuff[i]);
		}
		
		rebuff = client.sendRequest(IP, 61000, rebuff, 5000);
//		System.out.println("客户端收到服务端返回的数据：" + new String(rebuff));
	}
}
