package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.model.Inbox;
import com.example.progetto_prog_3.model.MsgProtocol;
import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
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

    public HomeController() {
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readBtn.setDisable(true);
        Gson gson = new Gson();
        String json = "";
        selectedEmail = null;
        Socket s = null;
        try {
            s = new Socket(InetAddress.getLocalHost(), 8082);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            MsgProtocol<String> req = new MsgProtocol<>("giacomo@jmail.com", MsgProtocol.MsgAction.GET_INBOX_FOR_USER_IN_REQUEST);
            out.writeObject(req);
            out.flush();
            MsgProtocol<Inbox> res = (MsgProtocol<Inbox>) in.readObject();
            List<Email> inbox = res.getMsg().getInMessages();


            //aggiorniamo la inbox locale...
            json = gson.toJson(inbox);
            BufferedWriter writer = new BufferedWriter(new FileWriter("./local_data/inbox.txt"));
            writer.write(json);
            System.out.println("Inbox locale aggiornata.");
            writer.close();

        } catch(IOException e) {
            //do nothing, we'll fetch data from local inbox
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if(s != null) {
                try {
                    s.close();
                } catch(IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        json = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./local_data/inbox.txt"));
            String line;
            while((line = reader.readLine()) != null) {
                json += line;
            }
            inbox = gson.fromJson(json, Email[].class);
            if(inbox == null) {  //se non ci sono email ricevute...
                inbox = new Email[0];
            }
            reader.close();

            inboxList.getItems().addAll(inbox);
            inboxList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Email>() {
                @Override
                public void changed(ObservableValue<? extends Email> observableValue, Email email, Email t1) {
                    selectedEmail = inboxList.getSelectionModel().getSelectedItem();
                    readBtn.setDisable(false);
                    mittente.setText(inboxList.getSelectionModel().getSelectedItem().getMittente());
                    data.setText(inboxList.getSelectionModel().getSelectedItem().getData());
                }
            });
        } catch(IOException e) {
            System.out.println(e.getMessage());
        } finally {

        }
    }

    public void read(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reader-view.fxml"));
        root = loader.load();

        ReaderController readerController = loader.getController();
        readerController.setEmail(selectedEmail);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(selectedEmail.getArgomento());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void compose(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("compose-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("JMail | Componi");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
