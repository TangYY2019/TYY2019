package day1703;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;



public class KeHuDuan {
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	private String name;
	private LinkedList<String> list =new LinkedList<>();
	private boolean flag;
	
	public void launch() {
		try {
			s = new Socket("176.135.1.210", 8000);
			in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"));
			System.out.println("起个名字:");
			name = new Scanner(System.in).nextLine();
			out.println(name);
			out.flush();
			
			new Thread(){
				public void run() {
					Receiver();
				};
			}.start();
			
			Thread t1 = new Thread(){
				@Override
				public void run() {
					input();
				}
			};
			
			Thread t2 = new Thread(){
				@Override
				public void run() {
					print();
				}
			};
			t1.setDaemon(true);
			t2.setDaemon(true);
			t1.start();
			t2.start();
			
		} catch (Exception e) {
			System.out.println("无法连接聊天室");
			e.printStackTrace();
		}

	}

	protected void Receiver() {
		try {
			String line;
			while((line = in.readLine())!=null){
				synchronized (list) {
					list.add(line);
					list.notify();
				}
			}
		} catch (Exception e) {
			System.out.println("已经与服务器断开连接");
		}
		
		
	}

	protected void input() {
		System.out.println("按回车键输入聊天内容");
		while (true) {
			new Scanner(System.in).nextLine();
			flag = true;
			System.out.println("输入聊天内容");
			String s = new Scanner(System.in).nextLine();
			out.println(s);
			out.flush();
			
			flag = false;
			synchronized (list) {
				list.notify();
			}
		}
	}

	protected void print() {
		while(true){
			synchronized (list) {
				while(list.isEmpty() || flag){
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
	public static void main(String[] args) {
		KeHuDuan c = new KeHuDuan();
		c.launch();
	}
}














