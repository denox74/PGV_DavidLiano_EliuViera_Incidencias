package com.util;

import java.util.Set;

public class Normalizer {

    private static final Set<String> COMMAND_SET = Set.of("SALIR", "ALTA", "LISTAR", "CLIENTES", "LOGIN");

    /**
     * -------------------------------------------------------------------
     * NORMALIZAMOS EL TEXTO DE LA DESCRIPCIÓN QUE VAYAMOS A REALIZAR
     * -------------------------------------------------------------------
     */
    private String normalizerDescription(String description) {

        // SINO TENEMOS DESCRIPCIÓN
        if (description == null) {
            return null;
        }

        String des = description.trim();
        //SI ESTA VACIA
        if (des.isEmpty()) {
            return null;
        }
        //SI ES MAYOR DE 200 CARACTERES
        if (des.length() > 200) {
            return null;
        }
        // SI NO CONTIENE ALGUNO DE ESOS CARACTERES
        if (!des.matches("[A-Za-z0-9 áéíóúÁÉÍÓÚñÑ.,;:()/_\\\\-]+")) {
            return null;
        }

        return des;
    }

    /**
     * -------------------------------------------------------------------
     * NORMALIZAMOS LOS COMANDOS DE LISTAR,CLIENTES,ALTA,SALIR,LOGIN
     * -------------------------------------------------------------------
     */

    private String normalizerCommand(String command) {
        
        String cmd = command.trim().toUpperCase();

        if (cmd == null) {
            return "Tienes que insertar un comando de ejecución";
        }

        if (cmd.isEmpty()) {
            return null;

        }
        if (cmd.length() > 10) {
            return null;

        }

        if (cmd.matches("[A-Z]+")) {
            return null;
        }

        if (!COMMAND_SET.contains(cmd)) {
            return null;
        }

        return cmd;
    }
}
