module com.example.progetto_prog_3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;


    opens com.example.progetto_prog_3.model to com.google.gson;
    opens com.example.progetto_prog_3 to javafx.fxml;
    exports com.example.progetto_prog_3;
    exports com.example.progetto_prog_3.server;
    exports com.example.progetto_prog_3.client;
    opens com.example.progetto_prog_3.server to javafx.fxml;
    opens com.example.progetto_prog_3.server.modules to com.google.gson;
    opens com.example.progetto_prog_3.client to javafx.fxml;

}