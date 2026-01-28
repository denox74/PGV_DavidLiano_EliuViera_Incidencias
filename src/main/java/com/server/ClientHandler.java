package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLSocket;
import com.Controllers.CommandController;
import com.model.ClientConnected;
import com.model.Incidence;
import com.model.Role;
import com.security.AuthService;

//Clase para los hilos de los clientes
public class ClientHandler implements Runnable {

    private final SSLSocket socket;
    private final Semaphore semaphore;
    private int idClient;

    private ClientConnected clientInfo;
    private final ConcurrentHashMap<Integer, ClientConnected> clients;
    private final List<Incidence> incidences;
    // AtomicInteger para generar IDs únicos, recomendado para entornos multi-hilo.
    private final AtomicInteger idIncidence;
    // INSTANCIAMOS LA CLASE COMMANDCONTROLLER
    private final CommandController controller;

    private String user = null;
    private Role role = null;

    public ClientHandler(SSLSocket socket, Semaphore semaforo, int idClient,
            ConcurrentHashMap<Integer, ClientConnected> clients,
            List<Incidence> incidences, AtomicInteger idIncidence) {
        this.socket = socket;
        this.semaphore = semaforo;
        this.idClient = idClient;
        this.clients = clients;
        this.incidences = incidences;
        this.idIncidence = idIncidence;
        this.controller = new CommandController(clients, idIncidence, incidences);
    }

    @Override
    /**
     * -----------------------------------------------------------------------
     * METODO QUE SE EJECUTA CUANDO SE CREA EL HILO
     * -----------------------------------------------------------------------
     */
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);) {
            /**
             * --------------------------------------------------------------------------------
             * AUTENTIFICAMOS AL CLIENTE
             * --------------------------------------------------------------------------------
             */
            while (true) {
                if (authentication(in, out)) {
                    break;
                }
                out.println("Error de credenciales. Inténtelo de nuevo.");
            }

            /**
             * --------------------------------------------------------------------------------
             * REGISTRAMOS AL CLIENTE CONECTADO
             * --------------------------------------------------------------------------------
             */
            synchronized (clients) {
                clientInfo = new ClientConnected(user, idClient);
                clients.put(idClient, clientInfo);

            }
            out.println("< - Bienvenido " + user + " - > [" + role + "]");
            out.println("<-- Comandos Disponibles -->");
            out.println("ALTA <descripción> - Crear nueva incidencia");
            out.println("LISTAR - Ver todas las incidencias");
            out.println("EDITAR <id> <nueva_descripción> - Modificar una incidencia");
            out.println("CERRAR <id> - Cerrar una incidencia");
            if (role == Role.ADMIN) {
                out.println("CLIENTES - Ver lista de clientes conectados");
            }
            out.println("SALIR - Desconectar del servidor");

            /**
             * --------------------------------------------------------------------------------------------------
             * INICIAMOS EL BUCLE PARA REALIZAR LOS COMANDOS REALIZAMOS EN
             * COMMANDCONTROLLER
             * --------------------------------------------------------------------------------------------------
             */
            String line;

            while ((line = in.readLine()) != null) {
                // INSTANCIAMOS TODAS LAS FUNCIONES DEL CLIENTE DESDE EL CONTROLLADOR
                String cmd = controller.processCommand(line.trim(), user, role);

                // Si el comando requiere una descripción interactiva
                if ("PROMPT_DESCRIPTION".equals(cmd)) {
                    out.println("Ingrese la descripción de la incidencia:");
                    out.println("___FIN___"); // Marcador de fin de respuesta
                    String description = in.readLine();

                    if (description != null && !description.trim().isEmpty()) {
                        // Procesar ALTA con la descripción proporcionada
                        cmd = controller.processCommand("ALTA " + description.trim(), user, role);
                        out.println(cmd);
                        out.println("___FIN___"); // Marcador de fin de respuesta
                    } else {
                        out.println("Error: Descripción vacía. Incidencia no creada.");
                        out.println("___FIN___"); // Marcador de fin de respuesta
                    }
                } else {
                    out.println(cmd);
                    out.println("___FIN___"); // Marcador de fin de respuesta
                }

                if (line.trim().equalsIgnoreCase("SALIR")) {
                    System.out.println("Cliente desconectado");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error en el hilo del cliente: " + e.getMessage());
        } finally {
            cleanSemaphore();
        }
    }

    /**
     * --------------------------------------------------------------------------------------------------
     * REALIZAMOS LA AUTENTIFICACIÓN DEL LOGIN PARA PODER LOGEARNOS
     * --------------------------------------------------------------------------------------------------
     */
    public Boolean authentication(BufferedReader in, PrintWriter out) throws IOException {
        // COMPROBAMOS EL USUARIO
        out.println("<LOGIN> : Usuario");
        String usuario = in.readLine();
        if (usuario == null) {
            throw new IOException("Cliente cerró conexión durante login");
        }

        // COMPROBAMOS LA CONTRASEÑA
        out.println("<LOGIN> : Password");
        String pass = in.readLine();
        if (pass == null) {
            throw new IOException("Cliente cerró conexión durante login");
        }

        AuthService.authResult result = AuthService.autentication(usuario, pass);

        if (result.isLogin()) {
            this.user = result.getUser();
            this.role = result.getRol();
            return true;
        }
        return false;

    }

    /**
     * --------------------------------------------------------------------------------------------------
     * REALIZAMOS LA LIMPIEZA DEL SEMAFORO AL SALIR UN CLIENTE Y LO ELIMINAMOS
     * --------------------------------------------------------------------------------------------------
     */

    public void cleanSemaphore() {
        if (clientInfo != null) {
            synchronized (clients) {
                clients.remove(idClient);
                System.out.println("Cliente ID: " + idClient + " desconectado");
            }
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Cliente desconectado");

        } catch (Exception e) {
            System.out.println("Error al cerrar el socket: " + e.getMessage());
        }
        semaphore.release();
        System.out.println("Quedan " + semaphore.availablePermits() + " clientes");
    }

}
