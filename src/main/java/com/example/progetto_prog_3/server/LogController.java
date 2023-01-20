package com.example.progetto_prog_3.server;

import com.example.progetto_prog_3.server.modules.Log;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.concurrent.ExecutorService;

public class LogController {

    @FXML
    ListView<String> loglistview;
    Log log;


    @FXML
    public void initialize(Log log){
        this.log = log;
        loglistview.setItems(log.getMylog());
    }


    @FXML
    public void stopServer(ActionEvent event){
        Platform.exit();
    }
}
