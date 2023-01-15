package com.example.progetto_prog_3.server.modules;


import com.example.progetto_prog_3.model.Email;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserDirectory {
    private final String email;
    private final File inFile;
    private final File outFile;
    private final File cnew_emailFile;

    public UserDirectory(String email, File inFile, File outFile, File cnew_emailFile) {
        this.email = email;
        this.inFile = inFile;
        this.outFile = outFile;
        this.cnew_emailFile = cnew_emailFile;
    }

    public String getEmail() {
        return email;
    }

    public File getInFile() {
        return inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    /*legge la stringa json dal file , la trasforma in un oggetto lista di email,aggiunge la mail alla lista,ritrasforma la lista in json e riscreve sul file */
    public void writeOnInMessageFile(Email emailWithId) {
        synchronized (inFile) {
            PrintWriter writer = null;
            BufferedReader reader = null;

            Gson g = new GsonBuilder().setPrettyPrinting().create();
            try {

                StringBuilder sb = new StringBuilder(); //stringa per costruire la collezione json
                reader = new BufferedReader(new FileReader(inFile));
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }
                String data = sb.toString();
                Type type = new TypeToken<List<Email>>() {
                }.getType();
                List<Email> list = g.fromJson(data, type); //trasforma la stringa in una lista di modules.Email
                if (list == null) {
                    list = new ArrayList<>(); //è la prima volta che viene ricevuta una mail
                }
                list.add(emailWithId);
                String dataToWrite = g.toJson(list); //ritrasforma la lista in stringa con la nuova mail al suo interno
                JsonElement je = JsonParser.parseString(dataToWrite); //fai un indent per facilitare la lettura del json
                String parsedString = g.toJson(je);
                writer = new PrintWriter(inFile);
                writer.println(parsedString); //scrivi su file il json modificato
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                    if (writer != null) {
                        writer.flush();
                        writer.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void writeOnOutMessageFile(Email emailWithId) {
        synchronized (outFile) {
            PrintWriter writer = null;
            BufferedReader reader = null;
            Gson g = new GsonBuilder().setPrettyPrinting().create();
            try {
                StringBuilder sb = new StringBuilder(); //stringa per costruire la collezione json
                reader = new BufferedReader(new FileReader(outFile));
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }
                String data = sb.toString();
                Type type = new TypeToken<List<Email>>() {
                }.getType();
                List<Email> list = g.fromJson(data, type); //trasforma la stringa in una lista di modules.Email
                if (list == null) {
                    list = new ArrayList<>(); //è la prima volta che viene mandata una mail
                }
                list.add(emailWithId);
                String dataToWrite = g.toJson(list); //ritrasforma la lista in stringa con la nuova mail al suo interno
                JsonElement je = JsonParser.parseString(dataToWrite); //fai un indent per facilitare la lettura del json
                String parsedString = g.toJson(je);
                writer = new PrintWriter(outFile);
                writer.println(parsedString); //scrivi su file il json modificato
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                    if (writer != null) {
                        writer.flush();
                        writer.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public boolean removeMessageFromInMessageFile(Email emailToRemove) throws IOException {
        PrintWriter writer = null;
        BufferedReader reader = null;
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder sb = new StringBuilder();
        synchronized (inFile){
            reader = new BufferedReader(new FileReader(inFile));
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            String data = sb.toString();
            Type type = new TypeToken<List<Email>>() {
            }.getType();
            List<Email> list = g.fromJson(data, type);
            if(list == null){   //se la lista è null , non esistono email,ritorna false
                reader.close();
                return false;
            }
            if(list.remove(emailToRemove)){
                String dataToWrite = g.toJson(list); //ritrasforma la lista in stringa con la nuova mail al suo interno
                JsonElement je = JsonParser.parseString(dataToWrite); //fai un indent per facilitare la lettura del json
                String parsedString = g.toJson(je);
                writer = new PrintWriter(inFile);
                writer.println(parsedString);
                reader.close();
                writer.flush();
                writer.close();
                return true;
            }
            reader.close();
            return false;
        }
    }

    public List<Email> getInMessages(){
        List<Email> list = null;
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        BufferedReader reader = null;
        try{
            StringBuilder sb = new StringBuilder();
            synchronized (inFile){
                reader = new BufferedReader(new FileReader(inFile));
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }
            }
            String data = sb.toString();
            Type type = new TypeToken<List<Email>>() {
            }.getType();
            list = g.fromJson(data, type);

//            Type type = new TypeToken<List<Email>>() {
//            }.getType();
//            list = g.fromJson(data, type);
        }catch (IOException e){
            throw new RuntimeException();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }

    public List<Email> getOutMessages(){
        List<Email> list = null;
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        BufferedReader reader = null;
        try{
            StringBuilder sb = new StringBuilder();
            synchronized (outFile){
                reader = new BufferedReader(new FileReader(outFile));
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }
            }
            String data = sb.toString();
            Type type = new TypeToken<List<Email>>() {
            }.getType();
            list = g.fromJson(data, type);
        }catch (IOException e){
            throw new RuntimeException();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }

    public void increaseNewEmail(){
        int count = readNewEmailCount();
        count++;
        synchronized (cnew_emailFile){
            try {
                FileWriter fw = new FileWriter(cnew_emailFile);
                fw.write(Integer.toString(count));
                fw.flush();
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int readNewEmailCount(){
        synchronized (cnew_emailFile){
            try {
                FileReader fr = new FileReader(cnew_emailFile);
                BufferedReader br = new BufferedReader(fr);
                int counter = Integer.parseInt(br.readLine());
                br.close();
                return counter;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void resetNewEmailCount(){
        synchronized (cnew_emailFile){
            try {
                FileWriter fw = new FileWriter(cnew_emailFile);
                fw.write("0");
                fw.flush();
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
