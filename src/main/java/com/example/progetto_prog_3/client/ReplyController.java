package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ReplyController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ChoiceBox<String> destinatari;
    private Email replyingTo;

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
            destinatari.getItems().addAll(replyingTo.getDestinatari());
            destinatari.getItems().add(replyingTo.getMittente());
        } else {
            destinatari.getItems().addAll(replyingTo.getMittente());
        }
    }

    @FXML
    protected void sendEmail() {
        if(oggetto.getText().trim().equals("")) {
            somethingMissing.setText("ATTENZIONE: Il campo 'Oggetto' non può essere vuoto.");
        } else if(messaggio.getText().trim().equals("")) {
            somethingMissing.setText("ATTENZIONE: Il corpo dell'email non può essere vuoto");
        } else {
            somethingMissing.setText("");
            //qui metti tutte le robe da fare per inviare l'email
            oggetto.setText("");
            messaggio.setText("");
        }
    }

    public void home(ActionEvent event) throws IOException{
        root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("JMail | Home");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
