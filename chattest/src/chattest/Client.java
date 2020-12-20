package chattest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private Socket clientSocket;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;

	//1. 데이터를 지속적으로 송신해줄 스레드
	//2. 데이터를 지속적으로 수신해줄 스레드
	//이 두가지 작업을 지속적으로 해줄 스레드가 필요함 -> 두 개의 메소드에 스레드 생성
	public void connect() {
		try {
			System.out.println("접속 시도");
			clientSocket = new Socket("127.0.0.1",10004);
			System.out.println("접속 완료");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dataSend() {
		new Thread(new Runnable() {
			Scanner in = new Scanner(System.in);
			boolean isThread = true;
			@Override
			public void run() {
				while(isThread){
					try {
						String sendData = in.nextLine();
						if(sendData.equals("/quit"))
							isThread = false;
						else
							dataOutputStream.writeUTF(sendData);//연결된 출력스트림에 메세지 실어보냄
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	public void dataRecv() {
		new Thread(new Runnable() {
			boolean isThread = true;
			@Override
			public void run() {
				while(isThread) {
					try {
						String recvData = dataInputStream.readUTF();//연결된 InputSteram 객체의 readUTF 메소드를 호출하여 데이터 읽어들임
						if(recvData.equals("/quit"))
							isThread = false;
						else
							System.out.println("서버 : "+recvData);
					} catch (Exception e) {

					}
				}
				closeAll();
				System.out.println("종료되었습니다.");
			}
		}).start();
	}

	public void StreamSetting() {
		try {
			dataInputStream = new DataInputStream(clientSocket.getInputStream()); // clientSocket에 InputStream 객체를 연결
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream()); //clientSocket에 OutputStream 객체를 연결
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void closeAll() {
		try {
			//소켓 사용 후 반납
			clientSocket.close();
			dataInputStream.close();
			dataOutputStream.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Client() {
		connect();
		StreamSetting();
		dataSend();
		dataRecv();
	}


	public static void main(String[] args) {
		new Client();
	}
}

