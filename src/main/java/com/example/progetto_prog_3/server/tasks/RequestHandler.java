package com.example.progetto_prog_3.server.tasks;

import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.server.modules.IDClass;
import com.example.progetto_prog_3.server.modules.UserDirectory;
import com.example.progetto_prog_3.model.MsgProtocol;
import com.example.progetto_prog_3.server.modules.Log;
import javafx.util.Pair;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static com.example.progetto_prog_3.server.tasks.HandleNotification.handleNotification;
import static com.example.progetto_prog_3.server.tasks.PrintToLog.printToLog;
import static com.example.progetto_prog_3.server.tasks.RemoveEmail.removeEmail;
import static com.example.progetto_prog_3.server.tasks.RetrieveInbox.*;
import static com.example.progetto_prog_3.server.tasks.SendEmail.sendEmail;

public class RequestHandler implements Runnable {
    private final Map<String, UserDirectory> mapOfInbox;
    private final Socket so;
    private final IDClass idClass;
    private final Log log;

    public RequestHandler(Map<String, UserDirectory> mapOfInbox, Socket so, IDClass idClass, Log log) {
        this.mapOfInbox = mapOfInbox;
        this.so = so;
        this.idClass = idClass;
        this.log = log;
    }


    @Override
    public void run() {
        MsgProtocol msg = null;
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            in = new ObjectInputStream(so.getInputStream());
            out = new ObjectOutputStream(so.getOutputStream());
            msg = (MsgProtocol) in.readObject();
        } catch (IOException e) {
            printToLog(log, "Qualcosa è andato storto "+e.getMessage());
        } catch (ClassNotFoundException e) {
            printToLog(log, "Non riesco a deserializzare il messaggio"+e.getMessage());
        }
        if(msg != null && in != null && out != null){
            switch (msg.getAction()) {
                case SEND_EMAIL_REQUEST:
                    Email e = (Email) msg.getMsg();
                    printToLog(log, e.getMittente() + " vuole inviare una mail");
                    List<String> wrongEmail = null;
                    try {
                        wrongEmail = sendEmail(e, idClass, mapOfInbox, out);
                    } catch (IOException ex) {
                        printToLog(log,"Non riesco a inviare la mail di ("+e.getMittente()+") : "+ex.getMessage());
                    }
                    if (wrongEmail == null) {
                        printToLog(log, "(" + e.getMittente() + ") email inviata con successo a tutti i destinatari");
                    } else {
                        printToLog(log, "(" + e.getMittente() + ") alcuni destinari erano errati");
                    }
                    break;
                case GET_INBOX_FOR_USER_IN_REQUEST:
                    String sender = (String) msg.getMsg();
                    printToLog(log,sender+" richiede la inbox");
                    try {
                        if(sendBackInbox(sender, MsgProtocol.MsgAction.GET_INBOX_FOR_USER_IN_REQUEST,mapOfInbox, out)){
                            printToLog(log,"inbox inviata con successo a "+sender);
                        }else{
                            printToLog(log,"email errata per la richiesta di inbox ("+sender+")");
                        }
                    } catch (IOException ex) {
                        printToLog(log,"Non riesco a mandare indietro la inbox di "+sender+" "+ex.getMessage());
                    }
                    break;
                case GET_INBOX_FOR_USER_OUT_REQUEST:
                    sender = (String) msg.getMsg();
                    printToLog(log,sender+" richiede la inbox");
                    try {
                        if(sendBackInbox(sender, MsgProtocol.MsgAction.GET_INBOX_FOR_USER_OUT_REQUEST, mapOfInbox, out)){
                            printToLog(log,"inbox inviata con successo a "+sender);
                        }
                        else{
                            printToLog(log,"email errata per la richiesta di inbox ("+sender+")");
                        }
                    } catch (IOException ex) {
                        printToLog(log,"Non riesco a mandare indietro la inbox di "+sender+" "+ex.getMessage());
                    }
                    break;
                case GET_NOTIFICATION_FOR_USER_REQUEST:
                    String email = (String) msg.getMsg();
                    try {
                        handleNotification(email, mapOfInbox, out);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case REMOVE_EMAIL_REQUEST:
                    Pair<String,Email> pair = (Pair<String, Email>) msg.getMsg();
                    String emailAddress = pair.getKey();
                    Email emailToRemove = pair.getValue();
                    printToLog(log,emailAddress+" chiede di rimuovere la mail con ID = "+emailToRemove.getId());
                    try {
                        if(removeEmail(emailAddress,emailToRemove,mapOfInbox,out)){
                            printToLog(log,"Utente "+emailAddress+" Email ID = "+emailToRemove.getId()+" eliminata con successo");
                        }
                        else{
                            printToLog(log,"Utente "+emailAddress+" Email ID = "+emailToRemove.getId()+" inesistente oppure qualcosa è andato storto");
                        }
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
            }
        }

    }


}


