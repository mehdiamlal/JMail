package com.example.progetto_prog_3.model;

import java.io.Serializable;
import java.util.List;

public class Inbox implements Serializable {
    private final String email;
    private final List<Email> inMessages;
    private final List<Email> outMessages;

    public Inbox(String email, List<Email> inMessages, List<Email> outMessages) {
        this.email = email;
        this.inMessages = inMessages;
        this.outMessages = outMessages;
    }

    public String getEmail() {
        return email;
    }

    public List<Email> getInMessages() {
        return inMessages;
    }

    public List<Email> getOutMessages() {
        return outMessages;
    }

    @Override
    public String toString() {
        return "Inbox{" +
                "email='" + email + '\'' +
                ", inMessages=" + inMessages +
                ", outMessages=" + outMessages +
                '}';
    }
}
