package com.model;

import java.time.LocalDateTime;

// Clase para representar una incidencia. ID y descripción. 

public class Incidence {
    private int id;
    private String description;
    private State state;
    private LocalDateTime dateTime;
    private String userIncidence;

    public Incidence() {
    }

    public Incidence(int id, String description) {
        this.id = id;
        this.description = description;
        this.state = State.OPEN;
        this.dateTime = LocalDateTime.now();
        this.userIncidence = "";
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public State getState() {
        return state;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getUserIncidence() {
        return userIncidence;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setUserIncidence(String userIncidence) {
        this.userIncidence = userIncidence;
    }

}
