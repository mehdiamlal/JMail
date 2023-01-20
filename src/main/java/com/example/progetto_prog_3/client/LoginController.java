package com.example.progetto_prog_3.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField emailTxt;

    @FXML
    private Label errorMsg;

    private ArrayList<String> accounts = new ArrayList<>();


    @FXML
    protected void login(ActionEvent event) throws IOException {
        if(accounts.contains(emailTxt.getText().trim().toLowerCase())) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home-view.fxml"));
            root = loader.load();

            HomeController homeController = loader.getController();
            homeController.setAccount(emailTxt.getText());

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("JMail | " + emailTxt.getText());
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } else {
            errorMsg.setText("Attenzione! L'utente selezionato non esiste.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        accounts.add("mehdi@jmail.com");
        accounts.add("giacomo@jmail.com");
        accounts.add("mirko@jmail.com");
    }
}
