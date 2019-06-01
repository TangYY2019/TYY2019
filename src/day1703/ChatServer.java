package day1703;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ChatServer {
	private ArrayList<TongXinThread> list = new ArrayList<>();
	
	public void launch() {
		//���������߳�
		new Thread(){
			@Override
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(8000);
					System.out.println("�����ҷ�����������");
					while (true) {
						Socket s = ss.accept();
						TongXinThread t = new TongXinThread(s);
						t.start();
					}
				} catch (Exception e) {
					System.out.println("�����޷�����,������Ѿ�ֹͣ");
				}
			}
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
		public void sendAll(String mag) {
			synchronized (list) {
				for (TongXinThread t : list) {
					t.send(mag);
				}
			}
		}
		@Override
		public void run() {
			try {
				s.setSoTimeout(5000);//�������ݵȴ�ʱ��
				
				 in = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
				 out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"));
				 //��ÿͻ��˵��ǳ�
				 this.name = in.readLine();
				 //�ѵ�ǰͨ���̼߳��뼯��
				 synchronized (list) {
					 list.add(this);
				}
				 //���ͻ�ӭ��Ϣ
				 send("��ӭ���뼤��������");
				 //Ⱥ��������Ϣ
				 sendAll(name+"����������,��������:"+list.size());
				int count = 0;//��ʱ����
				String line;
				while (true) {
					try {
						line = in.readLine();
						if (line == null) {
							break;
						}
						count = 0;
					} catch (SocketTimeoutException e) {
						if (count == 3) {
							send("���Ѿ����߳�����������");
							s.close();//�Ͽ�����
							break;
						}
						count++;
						send("�������������("+count+"/3)");
						continue;
					}
					sendAll(name+"˵: "+line);
				}
				//�Ͽ�
			} catch (Exception e) {
				//�Ͽ�
			}
			synchronized (list) {
				list.remove(this);
			}
			//Ⱥ��������Ϣ
			sendAll(name+"���뿪������,��������:"+list.size());
			System.out.println("һ���ͻ����ѶϿ�");
		}
	}
	public static void main(String[] args) {
		ChatServer s =  new ChatServer();
		s.launch();
	}
}