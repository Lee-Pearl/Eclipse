package application;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Main extends Application {
    Socket socket;
    TextArea textArea;
    public void   startClient(String ip, int port) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    receive();
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        stopClient();
                        System.out.println("[서버 접속 실패]");
                        Platform.exit();
                    }
                }
            }
        };
        thread.start();
    }
    public void stopClient() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void receive() {
        while (true) {
            try {
                InputStream in = socket.getInputStream();
                byte[] buffer = new byte[512];
                int length = in.read(buffer);
                if (length == -1) {
                    throw new IOException();
                }
                String message = new String(buffer, 0, length, "UTF-8");
                Platform.runLater(() -> {
                    textArea.appendText(message);
                });
            } catch (Exception e) {
                stopClient();
                break;
            }
        }
    }
    public void send(String message) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    OutputStream out = socket.getOutputStream();
                    byte[] buffer = message.getBytes("UTF-8");
                    out.write(buffer);
                    out.flush();
                } catch (Exception e) {
                    stopClient();
                }
            }
        };
        thread.start();
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("application.fxml"));
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(5));
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        TextField userName = new TextField();
        userName.setPrefWidth(150);
        userName.setPromptText("닉네임을 입력하세요.");
        HBox.setHgrow(userName, Priority.ALWAYS);
        TextField ipText = new TextField(("192.168.1.23"));
        TextField portText = new TextField("9876");
        portText.setPrefWidth(80);
        hbox.getChildren().addAll(userName, ipText, portText);
        root.setTop(hbox);
        textArea = new TextArea();
        textArea.setEditable(false);
        root.setCenter(textArea);
        TextField input = new TextField();
        input.setPrefWidth(Double.MAX_VALUE);
        input.setDisable(true);
        input.setOnAction(event -> {
           send(userName.getText() + ": " + input.getText() + "\n");
            input.setText("");
            input.requestFocus();
        });
        Button sendButton = new Button("보내기");
        sendButton.setDisable(false);
        sendButton.setOnAction(event -> {
            send(userName.getText() + " : " + input.getText() + "\n");
            input.setText("");
            input.requestFocus();
        });
        Button connectionButton = new Button("접속하기");
        connectionButton.setOnAction(event -> {
            if (connectionButton.getText().equals("접속하기")) {
                int port = 9876;
                try {
                    port = Integer.parseInt(portText.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startClient(ipText.getText(), port);
                Platform.runLater(() -> {
                    textArea.appendText("[채팅방접속]\n");
                });
                connectionButton.setText("종료하기");
                input.setDisable(false);
                input.requestFocus();
            } else {
                stopClient();
                Platform.runLater(() -> {
                    textArea.appendText("[채팅방 퇴장]");
                    connectionButton.setText("접속하기");
                    input.setDisable(true);
                    sendButton.setDisable(true);
                });
            }
        });
        BorderPane pane = new BorderPane();
        pane.setLeft(connectionButton);
        pane.setCenter(input);
        pane.setRight(sendButton);
        root.setBottom(pane);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("[채팅 클라이언트]");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> stopClient());
        primaryStage.show();
        connectionButton.requestFocus();
    }
    public static void main(String[] args) {
        launch(args);
    }
}