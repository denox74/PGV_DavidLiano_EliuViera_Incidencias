package com.Controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.ibm.j9ddr.vm29.structure.INIT_STAGE;
import com.model.ClientConnected;
import com.model.Incidence;

public class CommandController {

    private final AtomicInteger idIncidencia;
    private final Map<Integer, ClientConnected> client;
    private final List<Incidence> incidencesList;

    public CommandController(Map<Integer, ClientConnected> client, AtomicInteger idIncidencia, List<Incidence> incidencesList) {
        this.client = client;
        this.idIncidencia = idIncidencia;
        this.incidencesList = incidencesList;
    }

    /**
     * --------------------------------------------------------------------------------------
     * INICIACIÓN DEL PROCESO PARA LA SELECCIÓN DEL COMANDO
     * --------------------------------------------------------------------------------------
     */
    public String processCommand(String command, String user, String role) {

        // la expresion \\s+, 2 es para separar uno o mas espacios en blanco con un limite de division de 2 partes
        String[] part = command.toUpperCase().split("\\s+", 2);
        String cmd = part[0];
        /**
         * --------------------------------------------------------------------------------
         * SWITCH CON TODOS LOS COMANDOS QUE VAMOS A REALIZAR LLAMANDO A CADA
         * UNO DE LAS FUNCIONES QUE ESTAN EN LA PARTE INFERIOR
         * --------------------------------------------------------------------------------
         */

        switch (cmd) {
            case "LOGIN":

            case "ALTA":
                return cmdAlta(part, user);
            case "LISTAR":

            case "EDITAR":

            case "CLIENTES":

            case "CERRAR":

            case "SALIR":

            default:
                return "Comando no correcto" + cmd;

        }

    }


    public String cmdAlta(String [] parts, String user){

        synchronized (incidencesList) {
            int newId = idIncidencia.incrementAndGet();
            Incidence newIncidence = new Incidence(newId, parts[1].trim());
            newIncidence.setUserIncidence(user);
            incidencesList.add(newIncidence);
            return " <- Incidencia con número : " + newId + " Creada correctamente -> ";
        }
    }

    public String cmdLista(){

        synchronized (incidencesList) {
            if(incidencesList.isEmpty()) return "<- No tiene ninguna incidencia en la lista ->\n";
            
            StringBuilder sb = new StringBuilder("<- Listado de incidencias ->");
            for(Incidence inc : incidencesList){
                sb.append(String.format("[ID:%d] %s - Estado:%s - Usuario:%s - Fecha:%s\n",
                    inc.getId(),
                    inc.getDescription(),
                    inc.getUserIncidence(),
                    inc.getDateTime()
                ));
            }
            return sb.toString();
        }
    }

    public String cmdEditar(String[] part){

        String [] detailsPart = part[1].split("\\s+",2);

        



    }

}
