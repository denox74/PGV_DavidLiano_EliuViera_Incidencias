package com.model;

public class ClientConnected {
    private int id;

    private String name;


    public ClientConnected(String nombre, int id) {
        this.name = nombre;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }
}
