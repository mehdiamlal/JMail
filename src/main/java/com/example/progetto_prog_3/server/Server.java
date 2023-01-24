package com.example.progetto_prog_3.server;

import com.example.progetto_prog_3.server.modules.IDClass;
import com.example.progetto_prog_3.server.modules.UserDirectory;
import com.example.progetto_prog_3.server.modules.Log;
import com.example.progetto_prog_3.server.tasks.RequestHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server extends Application {

    private IDClass idClass;
    private Map<String, UserDirectory> userDirectoryMap;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean serverStatus;
    Log log;

    @Override
    public void init() throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> closeServer()));
        this.log = new Log();
        File id_txt = new File("./server_data/id.txt");
        try {
            if (!id_txt.exists()) {
                id_txt.createNewFile();
                PrintWriter w = new PrintWriter(id_txt);
                w.println(0);
                w.flush();
                w.close();
                idClass = new IDClass(0);
            } else {
                int tmp = 0;
                Scanner sc = new Scanner(id_txt);
                while (sc.hasNextInt()) {
                    tmp = sc.nextInt();
                    System.out.println("ID: " + tmp);
                }
                idClass = new IDClass(tmp);
                sc.close();
            }
            userDirectoryMap = createMapOfUserDirectory("./server_data/mailboxes","in.txt","out.txt","cnew_email.txt");
            if(userDirectoryMap == null){
                closeServer();
            }
            threadPool = Executors.newFixedThreadPool(5);
            serverSocket = new ServerSocket(8082);
            serverStatus = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        super.init();
    }

    /* for each directory inside the folder specified by directoryPath create a new userDirectory obj containing the reference to 3 files (inMessage,outMessage,newEmailCounter)*/
    /* and put it inside a hashmap where the keys are the user email and value userDirectory ,if there are no users return null else return the hashmap*/
    private static Map<String, UserDirectory> createMapOfUserDirectory(String directoryPath,String inMessageFileName,String outMessageFileName,String newEmailCounterFileName) {
        Map<String, UserDirectory> map = new HashMap<>();
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String userEmail = file.getName();
                    File inFile = new File(directoryPath+"/"+userEmail+"/"+inMessageFileName);
                    File outFile = new File(directoryPath+"/"+userEmail + "/"+outMessageFileName);
                    File cnew_emailFile = new File(directoryPath+"/"+userEmail+"/"+newEmailCounterFileName);
                    UserDirectory userDirectory = new UserDirectory(userEmail, inFile, outFile,cnew_emailFile);
                    map.put(userEmail, userDirectory);
                }
            }
        }
        if (map.isEmpty()) {
            return null;
        }
        return map;
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("log-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Server");
        stage.setScene(scene);
        LogController controller = fxmlLoader.getController();
        Thread socketThread = new Thread(() -> {
            while (serverStatus) {
                try {
                    Socket so = serverSocket.accept();
                    RequestHandler requestHandler = new RequestHandler(userDirectoryMap, so, idClass, log);
                    threadPool.execute(requestHandler);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        controller.initialize(log);
        stage.show();
        socketThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        closeServer();
    }

    void closeServer(){
        System.out.println("chiudo");
        serverStatus = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        File id_txt = new File("./server_data/id.txt");
        PrintWriter w = null;
        try {
            w = new PrintWriter(id_txt);
            w.println(idClass.getId());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (w != null) {
                w.flush();
                w.close();
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}


