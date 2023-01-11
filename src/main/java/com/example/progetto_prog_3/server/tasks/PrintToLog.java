package com.example.progetto_prog_3.server.tasks;

import com.example.progetto_prog_3.model.MsgProtocol;
import com.example.progetto_prog_3.server.modules.Log;
import javafx.application.Platform;

import java.text.SimpleDateFormat;

public class PrintToLog {
    static void printToLog(Log log, String line) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MsgProtocol<Object> msg;
                log.writeLog("["+timeStamp+"] "+line);
            }
        });
    }
}
