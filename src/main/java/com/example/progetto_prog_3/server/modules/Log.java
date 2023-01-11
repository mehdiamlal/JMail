package com.example.progetto_prog_3.server.modules;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Log {
    private final ObservableList<String> mylog;

    public Log() {
        this.mylog = FXCollections.observableArrayList();
    }

    public ObservableList<String> getMylog() {
        return mylog;
    }

    public void writeLog(String line){
        synchronized (mylog){
            mylog.add(line);
        }
    }
}
