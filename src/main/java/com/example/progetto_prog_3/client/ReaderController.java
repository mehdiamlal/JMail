package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.Email;
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

import java.io.IOException;

public class ReaderController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String account = "giacomo@jmail.com";

    @FXML
    private TextField mittente;
    private Email email; //oggetto email che si sta leggendo
    @FXML
    private TextField oggetto;

    @FXML
    private ChoiceBox<String> destinatari;

    @FXML
    private TextArea messaggio;

    public void setEmail(Email e) {
        email = e;
        mittente.setText(email.getMittente());
        oggetto.setText(email.getArgomento());
        destinatari.getItems().addAll(email.getDestinatari());
        messaggio.setText(email.getTesto());
    }

    public void home(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("JMail | " + this.account);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void reply(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reply-view.fxml"));
        root = loader.load();

        ReplyController replyController = loader.getController();
        replyController.setEmail(email, false);

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
        replyController.setEmail(email, true);

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
        forwardController.setEmail(this.email);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Jmail | Inoltra");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void delete(ActionEvent event) throws IOException {
        //apre popup che chiede se si è sicuri di voler eliminare l'email
        //poi riporta alla home, in caso si abbia eliminato la mail
    }
}
