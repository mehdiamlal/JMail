package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.model.Inbox;
import com.example.progetto_prog_3.model.MsgProtocol;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HomeController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private String account;
    private ScheduledExecutorService notificationExecutor;

    @FXML
    private ListView<Email> inboxList;
    private Email[] inbox; //non è un ArrayList per evitare errore di conversione di Gson
    private Email selectedEmail;
    @FXML
    private Label mittente;

    @FXML
    private Label data;

    @FXML
    private Button readBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Label errorMsg;

    public void getInbox() {
        Gson gson = new Gson();
        String json = "";
        selectedEmail = null;
        Socket s = null;
        BufferedWriter writer = null;
        try {
            s = new Socket(InetAddress.getLocalHost(), 8082);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            MsgProtocol<String> req = new MsgProtocol<>(account, MsgProtocol.MsgAction.GET_INBOX_FOR_USER_IN_REQUEST);
            out.writeObject(req);
            out.flush();
            MsgProtocol<Inbox> res = (MsgProtocol<Inbox>) in.readObject();
            List<Email> inbox = res.getMsg().getInMessages();
            if(inbox != null) {
                Collections.reverse(inbox);  //facciamo la reverse della inbox per avere i nuovi messaggi sempre al top della lista
            }


            //aggiorniamo la inbox locale...
            json = gson.toJson(inbox);
            writer = new BufferedWriter(new FileWriter("./local_data/mailboxes/" + account + "/in.txt"));
            writer.write(json);
        } catch(IOException e) {
            //do nothing, we'll fetch data from local inbox
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());;
        } finally {
            try {
                if(s != null) s.close();
                if(writer != null) writer.close();
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }

        //fetch from local inbox...
        json = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("./local_data/mailboxes/" + account + "/in.txt"));
            String line;
            while((line = reader.readLine()) != null) {
                json += line;
            }
            inbox = gson.fromJson(json, Email[].class);
            if(inbox == null) {  //se non ci sono email ricevute...
                inbox = new Email[0];
            }

            inboxList.getItems().clear();
            inboxList.getItems().addAll(inbox);
            inboxList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Email>() {
                @Override
                public void changed(ObservableValue<? extends Email> observableValue, Email email, Email t1) {
                    selectedEmail = inboxList.getSelectionModel().getSelectedItem();
                    readBtn.setDisable(false);
                    deleteBtn.setDisable(false);
                    if(inboxList.getSelectionModel().getSelectedItem() != null) {
                        mittente.setText(inboxList.getSelectionModel().getSelectedItem().getMittente());
                        data.setText(inboxList.getSelectionModel().getSelectedItem().getData());
                    }
                }
            });
            readBtn.setDisable(true);
            deleteBtn.setDisable(true);
        } catch(IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void setAccount(String account) {
        this.account = account.trim().toLowerCase();
        getInbox();
        notificationExecutor = new ScheduledThreadPoolExecutor(1);
        AskForNewEmails task = new AskForNewEmails(account, this);     //task che va a reperire se ci sono nuove email
        notificationExecutor.scheduleAtFixedRate(task , 2, 1, TimeUnit.SECONDS);//2 secondi di delay per evitare che appena si apre il client si ottenga una notifica
    }

    public void delete(ActionEvent event) {
        Gson gson = new Gson();
        String json = "";
        Socket s = null;
        BufferedWriter writer = null;
        try {
            s = new Socket(InetAddress.getLocalHost(), 8082);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            MsgProtocol<Pair<String, Email>> req = new MsgProtocol<>(new Pair<>(account, selectedEmail), MsgProtocol.MsgAction.REMOVE_EMAIL_REQUEST);
            out.writeObject(req);
            out.flush();
            MsgProtocol<Inbox> res = (MsgProtocol<Inbox>) in.readObject();
            if(res.getError() == MsgProtocol.MsgError.NO_ERROR) {
                List<Email> inbox = res.getMsg().getInMessages();

                json = gson.toJson(inbox);
                writer = new BufferedWriter(new FileWriter("./local_data/mailboxes/" + account + "/in.txt"));
                writer.write(json);

                //aggiorniamo graficamente
                Collections.reverse(inbox);
                inboxList.getItems().clear();
                inboxList.getItems().addAll(inbox);
                readBtn.setDisable(true);
                deleteBtn.setDisable(true);
            } else {
                //mostra un errore di eliminazione
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());;
        } catch (IOException e) {
            errorMsg.setText("Unable to send the email,\ntry later.");
        } finally {
            try {
                if(writer != null) writer.close();
                if(s != null) s.close();
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void read(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reader-view.fxml"));
        root = loader.load();

        ReaderController readerController = loader.getController();
        readerController.setAccount(account);
        readerController.setEmail(selectedEmail);
        readerController.setNotificationExecutor(notificationExecutor);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(selectedEmail.getArgomento());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void compose(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("compose-view.fxml"));
        root = loader.load();

        ComposeController composeController = loader.getController();
        composeController.setAccount(account);
        composeController.setNotificationExecutor(notificationExecutor);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("JMail | Compose");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
