package com.example.progetto_prog_3.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Email implements Serializable {
    private int id;
    private final String mittente;
    private final List<String> destinatari;
    private final String argomento;
    private final String testo;
    private final String data;

    public Email(int id, String mittente, List<String> destinatari, String argomento, String testo, String data) {
        this.id = id;
        this.mittente = mittente;
        this.destinatari = destinatari;
        this.argomento = argomento;
        this.testo = testo;
        this.data = data;
    }

    public Email(String mittente, List<String> destinatari, String argomento, String testo, String data) {
        this.id = -1;
        this.mittente = mittente;
        this.destinatari = destinatari;
        this.argomento = argomento;
        this.testo = testo;
        this.data = data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getMittente() {
        return mittente;
    }

    public List<String> getDestinatari() {
        return destinatari;
    }

    public String getArgomento() {
        return argomento;
    }

    public String getTesto() {
        return testo;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return argomento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return id == email.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
