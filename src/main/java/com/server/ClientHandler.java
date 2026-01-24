package com.server;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

//Clase para los hilos de los clientes
public class ClientHandler implements Runnable {
    private final SSLSocket socket;
    private final Semaphore semaphore;

    public ClientHandler(SSLSocket socket, Semaphore semaforo) {
        this.socket = socket;
        this.semaphore = semaforo;
    }

    @Override
    // Metodo que se ejecuta cuando se crea el hilo
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

        ) {
            out.println("CONECTADO");
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
