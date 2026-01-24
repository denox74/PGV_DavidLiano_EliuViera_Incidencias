package com;

// Clase para representar una incidencia. ID y descripción. 

public class Incidencia {
    private final int id;
    private final String descripcion;

    public Incidencia(int id, String descripcion) {
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
