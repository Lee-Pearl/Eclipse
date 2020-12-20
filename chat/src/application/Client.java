package application;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    Socket socket;
    public Client(Socket socket) {
        this.socket = socket;
        receive();
    }
    public void receive() {
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        InputStream in = socket.getInputStream();
                        byte[] buffer = new byte[512];
                        int length = in.read(buffer);
                        while (length == -1) throw new IOException();
                        System.out.println("[메세지 수신 성공]" + socket.getRemoteSocketAddress() + " : " + Thread.currentThread().getName());
                        String message = new String(buffer, 0, length, "UTF-8");
                        String[] msgArr = message.split(": ");
                        String ename = msgArr[0];
                        String text = msgArr[1];
                        System.out.println(ename);
                        System.out.println(text);
//                        System.out.println(message);
//                        String ename = Thread.currentThread().getName();
                        Database.dbWrite(ename, text);
                        for (Client client : Main.clients) {
                            client.send(message);
                        }
                    }
                } catch (Exception e) {
                    try {
                        System.out.println("[메세지 수신 오류]" + socket.getRemoteSocketAddress() + " : " + Thread.currentThread().getName());
                        e.printStackTrace();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        };
        Main.threadPool.submit(thread);
    }
    public void send(String message) {
        Runnable thread = new Runnable() {
            public void run() {
                try {
                    OutputStream out = socket.getOutputStream();
                    byte[] buffer = message.getBytes("UTF-8");
                    out.write(buffer);
                    out.flush();
                } catch (Exception e) {
                    try {
                        System.out.println("[메시지 송수신 오류]" + socket.getRemoteSocketAddress() + " : " + Thread.currentThread().getName());
                        Main.clients.remove(Client.this);
                        socket.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        };
        Main.threadPool.submit(thread);
    }
}