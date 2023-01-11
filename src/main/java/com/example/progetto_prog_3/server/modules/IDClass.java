package com.example.progetto_prog_3.server.modules;

public class IDClass{
    private int id;

    public IDClass(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getIdAndIncrease() {
        synchronized (this){
            this.id++;
            return this.id - 1;
        }
    }
}