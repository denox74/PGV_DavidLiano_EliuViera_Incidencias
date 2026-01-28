package com.Controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
    private static final String DATA_JSON_PATH = "src/main/java/com/json/data.json";
    private final ObjectMapper objectMapper;

    public CommandController(ConcurrentHashMap<Integer, ClientConnected> client, AtomicInteger idIncidencia,
            List<Incidence> incidencesList) {
        this.client = client;
        this.idIncidencia = idIncidencia;
        this.incidencesList = incidencesList;
        this.normalizer = new Normalizer();

        // Configurar Jackson
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Cargar incidencias al iniciar
        loadIncidences();
    }

    /**
     * Carga las incidencias desde el archivo JSON
     */
    private void loadIncidences() {
        File file = new File(DATA_JSON_PATH);
        if (file.exists() && file.length() > 0) {
            try {
                List<Incidence> loaded = objectMapper.readValue(file, new TypeReference<List<Incidence>>() {
                });
                synchronized (incidencesList) {
                    incidencesList.clear();
                    incidencesList.addAll(loaded);

                    // Ajustar el contador de IDs al máximo encontrado
                    int maxId = 0;
                    for (Incidence inc : loaded) {
                        if (inc.getId() > maxId) {
                            maxId = inc.getId();
                        }
                    }
                    idIncidencia.set(maxId);
                }
                System.out.println("Incidencias cargadas desde JSON correctamente.");
            } catch (IOException e) {
                System.err.println("Error al cargar incidencias: " + e.getMessage());
            }
        }
    }

    /**
     * Guarda las incidencias en el archivo JSON
     */
    private void saveIncidences() {
        try {
            synchronized (incidencesList) {
                objectMapper.writeValue(new File(DATA_JSON_PATH), incidencesList);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar incidencias: " + e.getMessage());
        }
    }

    /**
     * --------------------------------------------------------------------------------------
     * INICIACIÓN DEL PROCESO PARA LA SELECCIÓN DEL COMANDO
     * --------------------------------------------------------------------------------------
     */
    public String processCommand(String command, String user, Role role) {

        // la expresion \\s+, 2 es para separar uno o mas espacios en blanco con un
        // limite de division de 2 partes
        String[] part = command.trim().toUpperCase().split("\\s+", 2);
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
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            return "Error: Debe proporcionar una descripción para el alta. Uso: ALTA <descripción>";
        }

        String description = normalizer.normalizerDescription(parts[1].trim());

        synchronized (incidencesList) {
            int newId = idIncidencia.incrementAndGet();

            Incidence newIncidence = new Incidence(newId, description);
            newIncidence.setUserIncidence(user);
            incidencesList.add(newIncidence);

            saveIncidences(); // Guardar cambios

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

            StringBuilder sb = new StringBuilder("<- Listado de incidencias ->\n");
            for (Incidence inc : incidencesList) {
                sb.append(String.format("[ID:%d] %s - Usuario:%s - Fecha:%s - Estado:%s\n",
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
        if (part.length < 2) {
            return "Error: Uso: EDITAR <id> <nueva_descripción>";
        }

        String[] detailsPart = part[1].split("\\s+", 2);
        if (detailsPart.length < 2) {
            return "Error: Debe proporcionar tanto el ID como la nueva descripción. Uso: EDITAR <id> <nueva_descripción>";
        }

        try {
            int id = Integer.valueOf(detailsPart[0]);
            String newDetails = detailsPart[1].trim();

            synchronized (incidencesList) {
                for (Incidence inc : incidencesList) {
                    if (inc.getId() == id) {
                        // NORMALIZAMOS ANTES DE GUARDAR LA NUEVA DESCRIPCION
                        inc.setDescription(normalizer.normalizerDescription(newDetails));
                        saveIncidences(); // Guardar cambios
                        return "<- Incidencia :" + id + " modificada correctamente - >";
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
        if (parts.length < 2) {
            return "Error: Debe proporcionar el ID de la incidencia. Uso: CERRAR <id>";
        }

        try {
            int id = Integer.valueOf(parts[1].trim());

            synchronized (incidencesList) {
                for (Incidence inc : incidencesList) {
                    if (inc.getId() == id) {
                        inc.setState(State.CLOSED);
                        saveIncidences(); // Guardar cambios
                        return "< - Incidencia : " + id + " se ha cerrado correctamente - >";
                    }
                }
            }
            return "No se ha podido encontrar la incidencia : " + id;

        } catch (NumberFormatException e) {
            return "El ID tiene que ser númerico";
        }
    }

}
