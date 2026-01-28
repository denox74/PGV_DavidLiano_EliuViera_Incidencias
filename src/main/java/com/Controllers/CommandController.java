package com.Controllers;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.model.ClientConnected;
import com.model.Incidence;
import com.model.Role;
import com.model.State;
import com.util.Normalizer;

public class CommandController {

    private final AtomicInteger idIncidencia;
    private final ConcurrentHashMap<Integer, ClientConnected> client;
    private final List<Incidence> incidencesList;
    private final Normalizer normalizer;

    public CommandController(ConcurrentHashMap<Integer, ClientConnected> client, AtomicInteger idIncidencia,
            List<Incidence> incidencesList) {
        this.client = client;
        this.idIncidencia = idIncidencia;
        this.incidencesList = incidencesList;
        this.normalizer = new Normalizer();
    }

    /**
     * --------------------------------------------------------------------------------------
     * INICIACIÓN DEL PROCESO PARA LA SELECCIÓN DEL COMANDO
     * --------------------------------------------------------------------------------------
     */
    public String processCommand(String command, String user, Role role) {

        // la expresion \\s+, 2 es para separar uno o mas espacios en blanco con un
        // limite de division de 2 partes
        String[] part = command.toUpperCase().split("\\s+", 2);
        String cmd = part[0];
        /**
         * --------------------------------------------------------------------------------
         * SWITCH CON TODOS LOS COMANDOS QUE VAMOS A REALIZAR LLAMANDO A CADA
         * UNO DE LAS FUNCIONES QUE ESTAN EN LA PARTE INFERIOR
         * --------------------------------------------------------------------------------
         */

        switch (cmd) {

            case "ALTA":
                return cmdAlta(part, user);
            case "LISTAR":
                return cmdLista();

            case "EDITAR":
                return cmdEditar(part);

            case "CLIENTES":
                return cmdClients(role);

            case "CERRAR":
                return cmdCerrar(part);
            case "AYUDA":
                return cmdAyuda(role);

            case "SALIR":
                return "Desconectando .....";

            default:
                return "Comando no correcto" + cmd;

        }

    }

    /**
     * --------------------------------------------------------------------------------------
     * REALIZAMOS LA FUNCIÓN ALTA DE UNA INCIDENCIA Y LA
     * INCREMENTAMOS
     * --------------------------------------------------------------------------------------
     */
    public String cmdAlta(String[] parts, String user) {

        synchronized (incidencesList) {
            int newId = idIncidencia.incrementAndGet();

            Incidence newIncidence = new Incidence(newId, parts[1].trim());
            newIncidence.setUserIncidence(user);
            incidencesList.add(newIncidence);
            return " <- Incidencia con número : " + newId + " Creada correctamente -> ";
        }
    }

    /**
     * --------------------------------------------------------------------------------------
     * REALIZAMOS LA FUNCIÓN DE LA LISTA DE INCIDENCIAS PARA VER TODAS
     * --------------------------------------------------------------------------------------
     */
    public String cmdLista() {

        synchronized (incidencesList) {
            if (incidencesList.isEmpty()) {
                return "<- No tiene ninguna incidencia en la lista ->\n";
            }

            StringBuilder sb = new StringBuilder("<- Listado de incidencias ->");
            for (Incidence inc : incidencesList) {
                sb.append(String.format("[ID:%d] %s - Estado:%s - Usuario:%s - Fecha:%s\n",
                        inc.getId(),
                        inc.getDescription(),
                        inc.getUserIncidence(),
                        inc.getDateTime(),
                        inc.getState()));
            }
            return sb.toString();
        }
    }

    /**
     * --------------------------------------------------------------------------------------
     * REALIZAMOS LA FUNCIÓN PARA EDITAR LA DESCRIPCIÓN DE UNA INCIDENCIA POR ID
     * DE LA MISMA
     * --------------------------------------------------------------------------------------
     */
    public String cmdEditar(String[] part) {

        String[] detailsPart = part[1].split("\\s+", 2);

        try {

            int id = Integer.valueOf(detailsPart[0]);
            String newDetails = detailsPart[1].trim();

            synchronized (incidencesList) {

                for (Incidence inc : incidencesList) {
                    if (inc.getId() == id) {
                        // NORMALIZAMOS ANTES DE GUARDAR LA NUEVA DESCRIPCION
                        inc.setDescription(normalizer.normalizerDescription(newDetails));
                        return "<- Incidencia :" + id + "modificada correctamente - >";
                    }
                }

            }
            return "<- Incidencia : " + id + " no encontrada";
        } catch (NumberFormatException e) {
            return "La id tiene que ser númerica";
        }

    }

    /**
     * --------------------------------------------------------------------------------------
     * REALIZAMOS LA FUNCIÓN QUE SOLO AL ADMIN PUEDE VER LOS CLIENTES CONECTADOS
     * --------------------------------------------------------------------------------------
     */

    public String cmdClients(Role role) {

        synchronized (client) {
            if (role != Role.ADMIN) {
                return "< - Permiso denegado - > \n  < - Los administradores estan autorizados para ver los clientes - > ";
            }
            if (client.isEmpty())
                return "< - No hay clientes conectados - >";

            StringBuilder sb = new StringBuilder("<--Clientes Conectados-->");
            for (ClientConnected cc : client.values()) {
                sb.append(String.format("[ID:%d] Usuario: %s\n",
                        cc.getId(),
                        cc.getName()));

            }
            return sb.toString();

        }

    }

    /**
     * --------------------------------------------------------------------------------------
     * REALIZAMOS LA FUNCIÓN PARA CERRAR LA INCIDENCIA CREADA
     * --------------------------------------------------------------------------------------
     */

    public String cmdCerrar(String[] parts) {

        try {

            int id = Integer.valueOf(parts[1].trim());

            synchronized (incidencesList) {
                for (Incidence inc : incidencesList) {
                    if (inc.getId() == id) {
                        inc.setState(State.CLOSED);
                        return "< - Incidencia : " + id + " se ha cerrado correctamente - >";
                    }

                }

            }
            return "No se a podido encontrar la incidencia : " + id;

        } catch (NumberFormatException e) {
            return "El ID tiene que ser númerico";
        }

    }

    /**
     * --------------------------------------------------------------------------------------
     * REALIZAMOS LA FUNCIÓN AYUDA
     * --------------------------------------------------------------------------------------
     */
    public String cmdAyuda(Role role) {
        StringBuilder sb = new StringBuilder("<-- Comandos Disponibles -->\n");
        sb.append("ALTA <descripción> - Crear nueva incidencia\n");
        sb.append("LISTAR - Ver todas las incidencias\n");
        sb.append("EDITAR <id> <nueva_descripción> - Modificar una incidencia\n");
        sb.append("CERRAR <id> - Cerrar una incidencia\n");

        if (role == Role.ADMIN) {
            sb.append("CLIENTES - Ver clientes conectados (solo admin)\n");
        }

        sb.append("AYUDA - Mostrar este menú\n");
        sb.append("SALIR - Desconectar del servidor\n");

        return sb.toString();
    }
}
