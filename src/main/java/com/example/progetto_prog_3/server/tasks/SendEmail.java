package com.example.progetto_prog_3.server.tasks;


import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.server.modules.IDClass;
import com.example.progetto_prog_3.server.modules.UserDirectory;
import com.example.progetto_prog_3.model.MsgProtocol;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SendEmail {
    private static List<String> sendMessages(Email email, IDClass idClass, Map<String, UserDirectory> userDirectoryMap) {
        List<String> listOfWrongEmail = new ArrayList<>();
        email.setId(idClass.getIdAndIncrease());
        for (String receiver : email.getDestinatari()) {        //scorri tutti i destinatari della mail
            UserDirectory receiverDirectory = userDirectoryMap.get(receiver);  //trova la casella del receiver in cui mettere il messagio
            if (receiverDirectory != null) {
                receiverDirectory.writeOnInMessageFile(email);
                receiverDirectory.increaseNewEmail();                      //aggiungi la mail nella inbox
            } else {                                                    //se non esiste la casella vuol dire che il receiver non esiste oppure è sbagliato
                listOfWrongEmail.add(receiver);                  //aggiungilo ai destinatari sbagliati
            }
        }
        UserDirectory senderDirectory = userDirectoryMap.get(email.getMittente());    //ottieni la inbox del mittente per inserire il messaggio in uscita
        senderDirectory.writeOnOutMessageFile(email);                     //scrivi nei messaggi un uscita del mittente
        if (listOfWrongEmail.isEmpty()) {                             //se la lista è vuota tutti i destinatari erano corretti,ritorna null
            return null;
        }
        return listOfWrongEmail;                                    //ci sono dei destinatari scorretti,ritornali
    }



    private static void sendBackResponse(List<String> list,ObjectOutputStream out) throws IOException {
        MsgProtocol<List<String>> respMsg;
        if (list == null) {
            respMsg = new MsgProtocol<>(null, MsgProtocol.MsgAction.SEND_EMAIL_RESPONSE, MsgProtocol.MsgError.NO_ERROR);
        } else {
            respMsg = new MsgProtocol<>(list, MsgProtocol.MsgAction.SEND_EMAIL_RESPONSE, MsgProtocol.MsgError.WRONG_EMAIL);
        }
        out.writeObject(respMsg);
        out.flush();
    }

    public static List<String> sendEmail(Email email, IDClass idClass, Map<String, UserDirectory> userDirectoryMap, ObjectOutputStream out) throws IOException {
        if(userDirectoryMap.containsKey(email.getMittente())){
            List<String> listOfWrongEmail;
            listOfWrongEmail = sendMessages(email,idClass,userDirectoryMap);
            sendBackResponse(listOfWrongEmail,out);
            return listOfWrongEmail;
        }
        else{
            MsgProtocol<List<String>> respMsg = new MsgProtocol<>(null, MsgProtocol.MsgAction.SEND_EMAIL_RESPONSE, MsgProtocol.MsgError.WRONG_SENDER_EMAIL);
            out.writeObject(respMsg);
            out.flush();
            throw new IOException("Wrong sender Email");
        }
    }

}