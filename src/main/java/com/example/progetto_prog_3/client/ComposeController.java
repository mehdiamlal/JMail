package com.example.progetto_prog_3.client;

import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.model.MsgProtocol;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;

public class ComposeController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String account;
    private ScheduledExecutorService notificationExecutor;
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

    public void setAccount(String account) {
        this.account = account.trim().toLowerCase();
    }

    public void setNotificationExecutor(ScheduledExecutorService notificationExecutor) {
        this.notificationExecutor = notificationExecutor;
    }

    private boolean controllaMail(String indirizzoMail) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(indirizzoMail).matches();
    }
    @FXML
    protected void addDestinatario() {
        invalidEmail.setText("");
        somethingMissing.setText("");
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
            invalidEmail.setText("Type in a valid email.");
        }
    }

    @FXML
    protected void sendEmail() {
        somethingMissing.setText("");
        somethingMissing.setTextFill(Color.rgb(208, 29, 29));
        if(listaDestinatari.size() == 0) {
            somethingMissing.setText("WARNING: Please add at least one receiver address.");
        } else if(oggetto.getText().trim().equals("")) {
            somethingMissing.setText("WARNING: The field 'Subject' cannot be empty.");
        } else if(messaggio.getText().trim().equals("")) {
            somethingMissing.setText("WARNING: The body of the email cannot be empty.");
        } else {
            Email em = new Email(account, listaDestinatari, oggetto.getText().trim(), messaggio.getText().trim(), new Date().toString());
            Socket s = null;
            try {
                MsgProtocol<Email> msg = new MsgProtocol<>(em, MsgProtocol.MsgAction.SEND_EMAIL_REQUEST);
                s = new Socket(InetAddress.getLocalHost(), 8082);
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                out.writeObject(msg);
                out.flush();
                MsgProtocol<List<String>> resp = (MsgProtocol<List<String>>) in.readObject();
                if(resp.getMsg() != null && resp.getError() == MsgProtocol.MsgError.WRONG_EMAIL) {
                    somethingMissing.setText("The following email addresses are wrong: " + resp.getMsg());
                } else if(resp.getMsg() == null && resp.getError() == MsgProtocol.MsgError.NO_ERROR){
                    somethingMissing.setTextFill(Color.color(0, 0, 0));
                    somethingMissing.setText("Email sent successfully.");
                }
                destinatario.setText("");
                oggetto.setText("");
                messaggio.setText("");
                listaDestinatari = new ArrayList<>();
                destinatari.setText("");
                stringaDestinatari = "";
            } catch(IOException e) {
                somethingMissing.setText("Unable to send the email at the moment, please try later.");
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
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
}