package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.model.MsgProtocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ReplyController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String account = "giacomo@jmail.com";

    @FXML
    private ChoiceBox<String> destinatari;
    private Email replyingTo;
    private ArrayList<String> listaDestinatari = new ArrayList<>();

    @FXML
    private TextField oggetto;

    @FXML
    private TextArea messaggio;

    @FXML
    private Label somethingMissing;

    public void setEmail(Email email, boolean replyAll) {
        //inizializzo con email dei destinatari
        replyingTo = email;
        if(replyAll) {
            listaDestinatari.addAll(replyingTo.getDestinatari());
            listaDestinatari.remove(this.account);
            listaDestinatari.add(replyingTo.getMittente());
            destinatari.getItems().addAll(listaDestinatari);
        } else {
            destinatari.getItems().addAll(replyingTo.getMittente());
            listaDestinatari.add(replyingTo.getMittente());
        }
        oggetto.setText("RE: " + replyingTo.getArgomento());
    }

    @FXML
    protected void sendEmail() {
        somethingMissing.setText("");
        somethingMissing.setTextFill(Color.rgb(208, 29, 29));
        if(oggetto.getText().trim().equals("")) {
            somethingMissing.setText("ATTENZIONE: Il campo 'Oggetto' non può essere vuoto.");
        } else if(messaggio.getText().trim().equals("")) {
            somethingMissing.setText("ATTENZIONE: Il corpo dell'email non può essere vuoto");
        } else {
            Email em = new Email(this.account, listaDestinatari, oggetto.getText().trim(), messaggio.getText().trim(), new Date().toString());
            Socket s = null;
            try {
                MsgProtocol<Email> msg = new MsgProtocol<>(em, MsgProtocol.MsgAction.SEND_EMAIL_REQUEST);
                s = new Socket(InetAddress.getLocalHost(), 8082);
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                out.writeObject(msg);
                out.flush();
                MsgProtocol<List<String>> resp = (MsgProtocol<List<String>>) in.readObject();
                if(resp.getMsg() != null) {
                    somethingMissing.setText("Le seguenti email sono errate: " + resp.getMsg());
                } else {
                    somethingMissing.setTextFill(Color.color(0, 0, 0));
                    somethingMissing.setText("Email inviata con successo.");
                }
                messaggio.setText("");
            } catch(IOException e) {
                somethingMissing.setText("Impossibile inviare la mail al momento, riprovare più tardi.");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if(s != null) {
                        s.close();
                    }
                } catch(IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void home(ActionEvent event) throws IOException{
        root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("JMail | " + this.account);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
