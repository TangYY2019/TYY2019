package day1703;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;


public class ChatClient {
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	private String name;
	private LinkedList<String> list = new LinkedList<>();
	private boolean flag;//开关
	
	public void launch(){
		try {
			s = new Socket("176.135.1.211",8000);
			in = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"));
			//昵称
			System.out.print("起一个昵称:");
			name = new Scanner(System.in).nextLine();
			out.println(name);
			out.flush();
			
			//接收线程
			new Thread(){
				public void run() {
					receive();
				};
				
			}.start();
			
			//输入线程
			Thread t1 = new Thread(){
				public void run() {
					input();
				}
			};
			
			//打印线程
			Thread t2 = new Thread(){
				public void run() {
					print();
				}
			};
			//t1,t2设置成后台线程
			t1.setDaemon(true);
			t2.setDaemon(true);
			t1.start();
			t2.start();
			
		} catch (Exception e) {
			System.out.println("无法连接聊天室服务器");
			e.printStackTrace();
		}
	}
	protected void print() {
		while (true) {
			synchronized (list) {
				while (list.isEmpty() || flag) {
					try {
						list.wait();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				String msg = list.removeFirst();
				System.out.println(msg);
			}
		}
	}
	protected void input() {
		System.out.println("按回车输入聊天内容:");
		
		while (true) {
			new Scanner(System.in).nextLine();
			flag = true;//打开开关
			System.out.print("输入聊天内容:");
			String s = new Scanner(System.in).nextLine();
			out.println(s);
			out.flush();
			
			flag = false;//关闭开关
			//通知打印线程可以继续打印了
			synchronized (list) {
				list.notify();
			}
		}
	}
	protected void receive() {
		try {
			String line;
			while ((line = in.readLine())!=null) {
				synchronized (list) {
					list.add(line);
					//通知打印线程已经有数据可以打印了
					list.notify();
				}
			}
		} catch (Exception e) {
			System.out.println("已经与服务器断开连接");
		}
	}
	public static void main(String[] args) {
		ChatClient c = new ChatClient();
		c.launch();
	}
}
