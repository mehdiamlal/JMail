package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.model.Inbox;
import com.example.progetto_prog_3.model.MsgProtocol;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class ReaderController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String account;
    private ScheduledExecutorService notificationExecutor;

    @FXML
    private TextField mittente;
    private Email email; //oggetto email che si sta leggendo
    @FXML
    private TextField oggetto;

    @FXML
    private ChoiceBox<String> destinatari;

    @FXML
    private TextArea messaggio;

    public void setAccount(String account) {
        this.account = account.trim().toLowerCase();
    }

    public void setNotificationExecutor(ScheduledExecutorService notificationExecutor) {
        this.notificationExecutor = notificationExecutor;
    }

    public void setEmail(Email e) {
        email = e;
        mittente.setText(email.getMittente());
        oggetto.setText(email.getArgomento());
        destinatari.getItems().addAll(email.getDestinatari());
        messaggio.setText(email.getTesto());
    }

    public void home(ActionEvent event) throws IOException {
        notificationExecutor.shutdown();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home-view.fxml"));
        root = loader.load();

        HomeController homeController = loader.getController();
        homeController.setAccount(account);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("JMail | " + account);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void reply(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reply-view.fxml"));
        root = loader.load();

        ReplyController replyController = loader.getController();
        replyController.setAccount(account);
        replyController.setEmail(email, false);
        replyController.setNotificationExecutor(notificationExecutor);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Jmail | Rispondi");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void replyAll(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reply-view.fxml"));
        root = loader.load();

        ReplyController replyController = loader.getController();
        replyController.setAccount(account);
        replyController.setEmail(email, true);
        replyController.setNotificationExecutor(notificationExecutor);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Jmail | Rispondi a tutti");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void forward(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("forward-view.fxml"));
        root = loader.load();

        ForwardController forwardController = loader.getController();
        forwardController.setAccount(account);
        forwardController.setEmail(this.email);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Jmail | Inoltra");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
