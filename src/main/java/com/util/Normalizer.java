package com.util;

public class Normalizer {

    private String normalizer(String description) {

        if (description == null) {
            return null;
        }

        String des = description.trim();

        if (des.isEmpty()) {
            return null;
        }

        if (des.length() > 200) {
            return null;
        }

        if (!des.matches("[A-Za-z0-9 áéíóúÁÉÍÓÚñÑ.,;:()/_\\\\-]+")) {
            return null;
        }

        return des;
    }
}
