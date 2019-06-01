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
	private boolean flag;//����
	
	public void launch(){
		try {
			s = new Socket("176.135.1.211",8000);
			in = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"));
			//�ǳ�
			System.out.print("��һ���ǳ�:");
			name = new Scanner(System.in).nextLine();
			out.println(name);
			out.flush();
			
			//�����߳�
			new Thread(){
				public void run() {
					receive();
				};
				
			}.start();
			
			//�����߳�
			Thread t1 = new Thread(){
				public void run() {
					input();
				}
			};
			
			//��ӡ�߳�
			Thread t2 = new Thread(){
				public void run() {
					print();
				}
			};
			//t1,t2���óɺ�̨�߳�
			t1.setDaemon(true);
			t2.setDaemon(true);
			t1.start();
			t2.start();
			
		} catch (Exception e) {
			System.out.println("�޷����������ҷ�����");
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
		System.out.println("���س�������������:");
		
		while (true) {
			new Scanner(System.in).nextLine();
			flag = true;//�򿪿���
			System.out.print("������������:");
			String s = new Scanner(System.in).nextLine();
			out.println(s);
			out.flush();
			
			flag = false;//�رտ���
			//֪ͨ��ӡ�߳̿��Լ�����ӡ��
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
					//֪ͨ��ӡ�߳��Ѿ������ݿ��Դ�ӡ��
					list.notify();
				}
			}
		} catch (Exception e) {
			System.out.println("�Ѿ���������Ͽ�����");
		}
	}
	public static void main(String[] args) {
		ChatClient c = new ChatClient();
		c.launch();
	}
}
