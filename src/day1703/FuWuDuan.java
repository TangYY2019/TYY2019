package day1703;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class FuWuDuan {
	private ArrayList<TongXinThread> list = new ArrayList<>();
	
	public void launch(){
		new Thread(){
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(8000);
					System.out.println("聊天室服务器已启动");
					while(true){
						Socket s = ss.accept();
						TongXinThread t = new TongXinThread(s);
						t.start();
					}
				} catch (Exception e) {
					System.out.println("服务器无法启动,或服务已经停止");
				}
			};
		}.start();
	}
	class TongXinThread extends Thread{
		Socket s;
		BufferedReader in;
		PrintWriter out;
		private String name;
		
		public TongXinThread(Socket s) {
			this.s = s;
		}
		public void send(String mag){
			out.println(mag);
			out.flush();
		}
		public void  sendAll(String mag) {
			synchronized (list) {
				for (TongXinThread t : list) {
					t.send(mag);
				}
			}
		}
		@Override
		public void run() {
			try {
				s.setSoTimeout(5000);
				
				in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
				out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"));
				
				this.name = in.readLine();
				
				synchronized (list) {
					list.add(this);
				}
				send("欢迎进入激情聊天室");
				sendAll(name+"进入聊天室,在线人数:"+list.size());
				
				int count = 0;
				String line;
				while(true){
					try {
						line = in.readLine();
						if(line == null){
							break;
						}
						count = 0;
					} catch (SocketTimeoutException e) {
						if(count==3){
							send("您已经被踢出聊天室");
							s.close();
							break;
						}
						count++;
						send("请积极参与聊天("+count+"/3");
						continue;
					}
					sendAll(name+"说:"+line);
				}
			} catch (Exception e) {

			}
			synchronized (list) {
				list.remove(this);
			}
			sendAll(name+"已离开聊天室,在线人数:"+list.size());
			System.out.println("一个客户端已断开");
		}
		
	}
	public static void main(String[] args) {
		FuWuDuan s = new FuWuDuan();
		s.launch();
	}
}











