package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
    private final Map<Integer, ClientConnected> clients;
    private final List<Incidence> incidences;
    // AtomicInteger para generar IDs únicos, recomendado para entornos multi-hilo.
    private final AtomicInteger idIncidence;
    // INSTANCIAMOS LA CLASE COMMANDCONTROLLER
    private final CommandController controller;

    private String user = null;
    private Role role = null;

    public ClientHandler(SSLSocket socket, Semaphore semaforo, int idClient, Map<Integer, ClientConnected> clients,
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
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)); PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);) {
            out.println("CONECTADO, Cliente ID: " + idClient);
            /**
             * --------------------------------------------------------------------------------
             * AUTENTIFICAMOS AL CLIENTE
             * --------------------------------------------------------------------------------
             */
            if(!authentication(in, out)){
                out.println("Error al autentificarse");
                return;
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

                out.println(cmd);

                if (line.trim().equalsIgnoreCase("SALIR")) {
                    System.out.println("Cliente desconectado");
                    break;
                } 
            }

        } catch (Exception e) {
            System.out.println("Error en el hilo del cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            semaphore.release(); // Liberamos la plaza del semaforo
        }
    }

    /**
     * --------------------------------------------------------------------------------------------------
     * REALIZAMOS LA AUTENTIFICACIÓN DEL LOGIN PARA PODER LOGEARNOS
     * --------------------------------------------------------------------------------------------------
     */
    public Boolean authentication(BufferedReader in, PrintWriter out) throws IOException {
        //COMPROBAMOS EL USUARIO 
        out.println("<LOGIN> : Usuario");
        String usuario = in.readLine();
        if (usuario == null) {
            return false;
        }

        //COMPROBAMOS LA CONTRASEÑA
        out.println("<LOGIN> : Password");
        String pass = in.readLine();
        if (pass == null) {
            return false;
        }

        AuthService.authResult result = AuthService.autentication(usuario, pass);

        if (result.isLogin()) {
            this.user = result.getUser();
            this.role = result.getRol();
            return true;
        }
        return false;

    }

}
