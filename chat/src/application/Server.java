package application;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    public static ExecutorService threadPool;
    public static Vector<Client> clients = new Vector<Client>();
    ServerSocket serverSocket;
    public void startServer(String ip, int port) {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
        } catch (Exception e) {
            e.printStackTrace();
            if (!serverSocket.isClosed()) {
                stopServer();
            }
            return;
        }
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        clients.add(new Client(socket));
                        System.out.println("[클라이언트 접속]" + socket.getRemoteSocketAddress() + " : " + Thread.currentThread().getName());
                    } catch (Exception e) {
                        if (!serverSocket.isClosed()) {
                            stopServer();
                        }
                        break;
                    }
                }
            }
        };
        threadPool = Executors.newCachedThreadPool();
        threadPool.submit(thread);
    }
    public void stopServer() {
        try {
            Iterator<Client> iterator = clients.iterator();
            while (iterator.hasNext()) {
                Client client = iterator.next();
                client.socket.close();
                iterator.remove();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        root.setPadding(new Insets(5));
        //내가 추가 : 보더팬 색깔
//        root.setStyle("fx-background-color: #f4ed24");
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("나눔고딕", 15));
        root.setCenter(textArea);
        Button toggleButton = new Button("시작하기");
        //내가 추가
        toggleButton.setStyle("-fx-background-color: #f4ed24");
        toggleButton.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setMargin(toggleButton, new Insets(1, 0, 0, 0));
        root.setBottom(toggleButton);
        String ip = "192.168.1.23";
        int port = 9876;
        toggleButton.setOnAction(event -> {
            if (toggleButton.getText().equals("시작하기")) {
                startServer(ip, port);
                Platform.runLater(() -> {
                    String message = String.format("[서버 시작]\n", ip, port);
                    textArea.appendText(message);
                    toggleButton.setText("종료하기");
                });
            } else {
                stopServer();
                Platform.runLater(() -> {
                    String message = String.format("[서버 종료]\n", ip, port);
                    textArea.appendText(message);
                    toggleButton.setText("시작하기");
                });
            }
        });
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}