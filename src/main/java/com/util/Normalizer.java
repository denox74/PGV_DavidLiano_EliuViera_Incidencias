package com.util;

public class Normalizer {

    /**
     * -------------------------------------------------------------------
     * NORMALIZAMOS EL TEXTO DE LA DESCRIPCIÓN QUE VAYAMOS A REALIZAR
     * -------------------------------------------------------------------
     */
    public String normalizerDescription(String description) {

        // SINO TENEMOS DESCRIPCIÓN
        if (description == null) {
            return null;
        }

        String des = description.trim();
        // SI ESTA VACIA
        if (des.isEmpty()) {
            return null;
        }
        // SI ES MAYOR DE 200 CARACTERES
        if (des.length() > 200) {
            return null;
        }
        // SI NO CONTIENE ALGUNO DE ESOS CARACTERES
        if (!des.matches("[A-Za-z0-9 áéíóúÁÉÍÓÚñÑ.,;:()/_\\\\-]+")) {
            return null;
        }

        return des;
    }

}
