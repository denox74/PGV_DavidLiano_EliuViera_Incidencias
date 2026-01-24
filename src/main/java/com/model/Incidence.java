package com.model;

// Clase para representar una incidencia. ID y descripción. 

public class Incidence {
    private final int id;
    private final String descripcion;

    public Incidence(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
