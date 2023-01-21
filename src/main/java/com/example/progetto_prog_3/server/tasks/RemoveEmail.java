package com.example.progetto_prog_3.server.tasks;

import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.model.Inbox;
import com.example.progetto_prog_3.model.MsgProtocol;
import com.example.progetto_prog_3.server.modules.UserDirectory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class RemoveEmail {
    public static boolean removeEmail(String email, Email emailToRemove,Map<String, UserDirectory> userDirectoryMap, ObjectOutputStream out) throws IOException {
        MsgProtocol<Inbox> response = null;
        UserDirectory userDirectory = userDirectoryMap.get(email);
        if(userDirectory != null && userDirectory.removeMessageFromInMessageFile(emailToRemove)){
            List<Email> inMessages = userDirectory.getInMessages();
            List<Email> outMessages = userDirectory.getOutMessages();
            Inbox payload = new Inbox(email,inMessages,outMessages);
            response = new MsgProtocol<>(payload, MsgProtocol.MsgAction.REMOVE_EMAIL_RESPONSE, MsgProtocol.MsgError.NO_ERROR);
            out.writeObject(response);
            out.flush();
            return true;
        }
        else{
            response = new MsgProtocol<>(null, MsgProtocol.MsgAction.REMOVE_EMAIL_RESPONSE, MsgProtocol.MsgError.EMAIL_DOESNT_EXIST);
            out.writeObject(response);
            out.flush();
            return false;
        }
    }
}
