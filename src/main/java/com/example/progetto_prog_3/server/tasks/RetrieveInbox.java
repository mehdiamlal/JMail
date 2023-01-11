package com.example.progetto_prog_3.server.tasks;

import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.model.Inbox;
import com.example.progetto_prog_3.server.modules.UserDirectory;
import com.example.progetto_prog_3.model.MsgProtocol;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;


public class RetrieveInbox {
    public static boolean sendBackInbox(String email,MsgProtocol.MsgAction action,Map<String, UserDirectory> userDirectoryMap, ObjectOutputStream out) throws IOException {
        Inbox payload;
        MsgProtocol<Inbox> response;
        UserDirectory userDirectory = userDirectoryMap.get(email);
        if(userDirectory != null){
            List<Email> inMessages = userDirectory.getInMessages();
            List<Email> outMessages = userDirectory.getOutMessages();
            payload = new Inbox(email,inMessages,outMessages);
            if(action == MsgProtocol.MsgAction.GET_INBOX_FOR_USER_IN_REQUEST){
                userDirectory.resetNewEmailCount();
                response = new MsgProtocol<>(payload, MsgProtocol.MsgAction.GET_INBOX_FOR_USER_IN_RESPONSE);
            }
            else{
                response = new MsgProtocol<>(payload, MsgProtocol.MsgAction.GET_INBOX_FOR_USER_OUT_RESPONSE);
            }
            out.writeObject(response);
        }
        else{
            if(action == MsgProtocol.MsgAction.GET_INBOX_FOR_USER_IN_REQUEST) {
                response = new MsgProtocol<>(null, MsgProtocol.MsgAction.GET_INBOX_FOR_USER_IN_RESPONSE, MsgProtocol.MsgError.WRONG_EMAIL);
            }
            else{
                response = new MsgProtocol<>(null, MsgProtocol.MsgAction.GET_INBOX_FOR_USER_OUT_RESPONSE, MsgProtocol.MsgError.WRONG_EMAIL);
            }
            out.writeObject(response);
        }
        out.flush();
        return userDirectory != null;
    }
}
