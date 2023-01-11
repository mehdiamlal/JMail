package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.Email;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ComposeController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private ArrayList<Email> sentEmails;
    @FXML
    private TextField destinatario;

    @FXML
    private Label somethingMissing;

    @FXML
    private Label destinatari;
    private ArrayList<String> listaDestinatari = new ArrayList<>();
    private String stringaDestinatari = "";

    @FXML
    private Label invalidEmail;

    @FXML
    private TextField oggetto;

    @FXML
    private TextArea messaggio;

    private boolean controllaMail(String indirizzoMail) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(indirizzoMail).matches();
    }
    @FXML
    protected void addDestinatario() {
        invalidEmail.setText("");
        if(controllaMail(destinatario.getText().trim())) {
            if(stringaDestinatari.equals("")) {
                stringaDestinatari += destinatario.getText().trim().toLowerCase();
            } else {
                stringaDestinatari += ",   " + destinatario.getText().trim().toLowerCase();
            }
            listaDestinatari.add(destinatario.getText().trim().toLowerCase());
            destinatari.setText(stringaDestinatari);
            destinatario.setText("");
        } else {
            invalidEmail.setText("Inserire un'email valida.");
        }
    }

    @FXML
    protected void sendEmail() {
        if(listaDestinatari.size() == 0) {
            somethingMissing.setText("ATTEZIONE: Aggiungere almeno un destinatario.");
        } else if(oggetto.getText().trim().equals("")) {
            somethingMissing.setText("ATTENZIONE: Il campo 'Oggetto' non può essere vuoto.");
        } else if(messaggio.getText().trim().equals("")) {
            somethingMissing.setText("ATTENZIONE: Il corpo dell'email non può essere vuoto");
        } else {
            somethingMissing.setText("");
            Gson gson = new Gson();
            Email em = new Email("mehdi@jmail.com", listaDestinatari, oggetto.getText().trim(), messaggio.getText().trim(), new Date().toString());
            sentEmails.add(em);
            String json = gson.toJson(sentEmails);

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("./local_data/sent.txt"));
                writer.write(json);
                writer.close();
                System.out.println("\nEmail inviata con successo!");
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
            destinatario.setText("");
            oggetto.setText("");
            messaggio.setText("");
            listaDestinatari = new ArrayList<>();
            destinatari.setText("");
            stringaDestinatari = "";
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Gson gson = new Gson();
        String json = "";
        try {
            Reader reader = new FileReader("./local_data/sent.txt");
            sentEmails = gson.fromJson(json, ArrayList.class);
            if(sentEmails == null) {  //se non ci sono email inviate...
                sentEmails = new ArrayList<>();
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
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