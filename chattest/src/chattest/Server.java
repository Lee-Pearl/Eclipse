package chattest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	private ServerSocket serverSocket; //서버 소켓(서비스를 제공하기 위한 용도) 생성
	private Socket clientSocket;//들어오는 정보가 저장되는, 클라이언트와 통신을 위한 소켓

	private DataInputStream dataInputStream;//서버가 받은 데이터
	private DataOutputStream dataOutputStream;

	//1. 데이터를 지속적으로 송신해줄 스레드
	//2. 데이터를 지속적으로 수신해줄 스레드
	//이 두가지 작업을 지속적으로 해줄 스레드가 필요함

	public void serverSetting() {
		try {
			serverSocket = new ServerSocket(10004);//생성과 바인드. IP주소를 안주면 localhost가 default 값.
			clientSocket = serverSocket.accept(); // 어셉트의 결과로 클라이언트가 접속하면 해당 클라이언트를 관리할 소켓을 생성하여 리턴. 이걸  clientSocket에 받음.
			//실질적으로 소켓에 접속 완료된 시점
			System.out.println("클라이언트 소켓 연결");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeAll() {
		try {
			serverSocket.close(); //소켓 사용 후 반납
			clientSocket.close();
			dataInputStream.close();
			dataOutputStream.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void StreamSetting() {
		try {
			dataInputStream = new DataInputStream(clientSocket.getInputStream()); // clientSocket에 InputStream 객체를 연결
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream()); //clientSocket에 OutputStream 객체를 연결
		}catch(Exception e) {
			e.printStackTrace();
		}
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
							System.out.println("클라이언트 : "+recvData);
					} catch (Exception e) {
					}
				}
				closeAll();
				System.out.println("종료되었습니다.");
			}

		}).start();
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

	public Server() {
		serverSetting();
		StreamSetting();
		dataRecv();
		dataSend();
	}

	public static void main(String[] args) {
		new Server();
	}
}

