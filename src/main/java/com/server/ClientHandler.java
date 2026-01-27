package com.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLSocket;

import com.model.ClientConnected;
import com.model.Incidence;
import com.model.Role;

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
            // Sesión minima, esperar hasta que el cliente se desconecte (SALIR)
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("SALIR")) {
                    System.out.println("Cliente desconectado");
                    break;
                } else {
                    System.out.println("Cliente: " + line);
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

}
