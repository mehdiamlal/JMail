package com.example.progetto_prog_3.server.tasks;

import com.example.progetto_prog_3.server.modules.UserDirectory;
import com.example.progetto_prog_3.model.MsgProtocol;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class HandleNotification {
    public static void handleNotification(String email, Map<String, UserDirectory> mapOfInbox, ObjectOutputStream out) throws IOException {         //estrai l'indirizzo email dal messaggio
        UserDirectory userDirectory = mapOfInbox.get(email);                                                                                       //ottieni la casella relativa alla email
        if (userDirectory != null) {
            Integer count = userDirectory.readNewEmailCount();                                                                                   //ottieni il numero di nuove email in entrata per quella inbox
            MsgProtocol<Integer> response = new MsgProtocol<>(count, MsgProtocol.MsgAction.GET_NOTIFICATION_FOR_USER_RESPONSE);         //attacca il numero al messaggio di risposta
            out.writeObject(response);          //manda indietro la risposta
            out.flush();
            userDirectory.resetNewEmailCount();
        } else {
            MsgProtocol<Integer> response = new MsgProtocol<>(-1, MsgProtocol.MsgAction.GET_NOTIFICATION_FOR_USER_RESPONSE, MsgProtocol.MsgError.WRONG_EMAIL);
            out.writeObject(response);
            out.flush();
        }
    }
}
