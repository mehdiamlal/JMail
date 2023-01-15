package com.example.progetto_prog_3;


import com.example.progetto_prog_3.model.Email;
import com.example.progetto_prog_3.model.Inbox;
import com.example.progetto_prog_3.model.MsgProtocol;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        Socket so = null;
        try {
            so = new Socket("localhost",8082);
            ObjectOutputStream out = new ObjectOutputStream(so.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(so.getInputStream());
//            MsgProtocol<String> msg = new MsgProtocol<>("mirko@jmail.com", MsgProtocol.MsgAction.GET_INBOX_FOR_USER_IN_REQUEST);
//            out.writeObject(msg);
//            out.flush();
//            MsgProtocol<Inbox> resp = (MsgProtocol<Inbox>) in.readObject();
//            System.out.println(resp.getMsg().getInMessages());
            Email email = new Email(2,null,null,null,null,null);
            Pair<String,Email> pair = new Pair<>("mirko@jmail.com",email);
            MsgProtocol<Pair> request = new MsgProtocol<>(pair, MsgProtocol.MsgAction.REMOVE_EMAIL_REQUEST);
            out.writeObject(request);
            out.flush();
            MsgProtocol<Inbox> resp = (MsgProtocol<Inbox>) in.readObject();
            System.out.println(resp.getError());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(so != null) {
                    so.close();
                }
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

class MyRun implements Runnable {

    long time;

    MyRun(long time) {
        this.time = time;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket so = new Socket("localhost", 8082);
                ObjectOutputStream out = new ObjectOutputStream(so.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(so.getInputStream());
                List<String> l = new ArrayList<>();
                l.add("y@gmail.com");
                Email e = new Email("x@gmail.com", l, "cic", "ac", "unadata");
                MsgProtocol<Email> msg = new MsgProtocol<>(e, MsgProtocol.MsgAction.SEND_EMAIL_REQUEST);
                out.writeObject(msg);
                MsgProtocol<List<String>> response = (MsgProtocol<List<String>>) in.readObject();
                if (response.getError() == MsgProtocol.MsgError.NO_ERROR) {
                    System.out.println("Nessun errore");
                } else if (response.getError() == MsgProtocol.MsgError.WRONG_EMAIL) {
                    System.out.println("Errore");
                    List<String> list = response.getMsg();
                    System.out.println(list);
                }
                in.close();
                out.close();
                so.close();
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}