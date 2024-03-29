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
import java.util.concurrent.ScheduledExecutorService;

public class ReplyController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String account;
    private ScheduledExecutorService notificationExecutor;

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

    public void setAccount(String account) {
        this.account = account.trim().toLowerCase();
    }

    public void setNotificationExecutor(ScheduledExecutorService notificationExecutor) {
        this.notificationExecutor = notificationExecutor;
    }

    public void setEmail(Email email, boolean replyAll) {
        replyingTo = email;
        //inizializzo con email dei destinatari
        if(replyAll) {
            listaDestinatari.addAll(replyingTo.getDestinatari());
            listaDestinatari.remove(account);
            listaDestinatari.add(replyingTo.getMittente());
            destinatari.getItems().addAll(listaDestinatari);
        } else {
            destinatari.getItems().addAll(replyingTo.getMittente());
            listaDestinatari.add(replyingTo.getMittente());
        }
        if(!replyingTo.getArgomento().startsWith("RE: ")) {  //per non creare tanti RE: RE: RE: in una discussione
            oggetto.setText("RE: " + replyingTo.getArgomento());
        } else {
            oggetto.setText(replyingTo.getArgomento());
        }
    }

    @FXML
    protected void sendEmail() {
        somethingMissing.setText("");
        somethingMissing.setTextFill(Color.rgb(208, 29, 29));
        if(oggetto.getText().trim().equals("")) {
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
                } else if(resp.getMsg() == null && resp.getError() == MsgProtocol.MsgError.NO_ERROR) {
                    somethingMissing.setTextFill(Color.color(0, 0, 0));
                    somethingMissing.setText("Email sent successfully.");
                }
                messaggio.setText("");
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

    public void home(ActionEvent event) throws IOException{
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
