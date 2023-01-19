package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.MsgProtocol;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("JMail | Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
//        ScheduledExecutorService ex = new ScheduledThreadPoolExecutor(1);
//        ex.scheduleAtFixedRate(() -> {
//            Socket s = null;
//            try {
//                s = new Socket(InetAddress.getLocalHost(), 8082);
//                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
//                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
//                MsgProtocol<String> req = new MsgProtocol<>(account, MsgProtocol.MsgAction.GET_NOTIFICATION_FOR_USER_REQUEST);
//                out.writeObject(req);
//                out.flush();
//                MsgProtocol<Integer> res = (MsgProtocol<Integer>) in.readObject();
//
//                if(res.getMsg() > 0) {
//                    Platform.runLater(() -> {
//                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setTitle("JMail | Alert");
//                        alert.setHeaderText("Nuove email per " + account);
//                        if(res.getMsg() == 1) {
//                            alert.setContentText("Hai 1 nuova email.");
//                        } else {
//                            alert.setContentText("Hai " + res.getMsg() + " nuove email.");
//                        }
//                        alert.showAndWait();
//                    });
//                }
//
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            } finally {
//                if(s != null) {
//                    try {
//                        s.close();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        launch();
    }
}