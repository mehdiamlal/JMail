package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.MsgProtocol;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class AskForNewEmails implements Runnable{

    private String account;
    private HomeController homeController;

    public AskForNewEmails(String account, HomeController homeController) {
        this.account = account.trim().toLowerCase();
        this.homeController = homeController;
    }

    @Override
    public void run() {
        Socket s = null;
        try {
            s = new Socket(InetAddress.getLocalHost(), 8082);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            MsgProtocol<String> req = new MsgProtocol<>(account, MsgProtocol.MsgAction.GET_NOTIFICATION_FOR_USER_REQUEST);
            out.writeObject(req);
            out.flush();
            MsgProtocol<Integer> res = (MsgProtocol<Integer>) in.readObject();

            if(res.getMsg() > 0) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("JMail | Alert");
                    alert.setHeaderText("New emails for " + account);
                    if(res.getMsg() == 1) {
                        alert.setContentText("You have 1 new email.");
                    } else {
                        alert.setContentText("You have " + res.getMsg() + " new emails.");
                    }
                    alert.showAndWait();
                    homeController.getInbox(); //aggiorna la listview se sono nella home
                });
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
