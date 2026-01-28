package com.server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import com.model.ClientConnected;
import com.model.Incidence;

public class ServerSAT {

    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 10; // Capacidad del semaforo (clientes simultaneos)

    public static void main(String[] args) {

        /**
         * ---------------------------------------------------------------------------
         * PONEMOS LAS KEYSTORES DEL SERVIDOR
         * ---------------------------------------------------------------------------
         */

        System.setProperty("javax.net.ssl.keyStore", "src/main/resources/servidor-keystore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "servidorpgvsat");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        Semaphore semaphore = new Semaphore(MAX_CLIENTS, true);

        /**
         * ---------------------------------------------------------------------------
         * ESTRUCTURAS COMPARTIDAS ENTRE TODOS LOS HILOS DE CLIENTES
         * ---------------------------------------------------------------------------
         */

        ConcurrentHashMap<Integer, ClientConnected> clients = new ConcurrentHashMap<>();
        List<Incidence> incidences = new ArrayList<>();
        AtomicInteger idIncidence = new AtomicInteger(0); // CONTADOR PARA ID DE INCIDENCIAS EMPEZANDO POR 1
        AtomicInteger idClientCounter = new AtomicInteger(1); // CONTADOR PARA ID DE CLIENTES EMPEZANDO POR 1

        System.out.println("ServidAT (SSL) arrancado en puerto " + PORT);

        SSLServerSocket serverSocket = null;

        try {

            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) factory.createServerSocket(PORT);

            while (true) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();

                if (!semaphore.tryAcquire()) {
                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                            true);
                    out.println("Servidor lleno, vuelva a intentarlo mas tarde");
                    socket.close();
                    continue;
                }

                /**
                 * ---------------------------------------------------------------------------
                 * ASIGNAMOS UN ID ÚNICO A CADA CLIENTE
                 * ---------------------------------------------------------------------------
                 */

                int idClient = idClientCounter.getAndIncrement();

                System.out.println("Cliente SSL conectado: " + socket.getInetAddress().getHostAddress());
                /**
                 * ---------------------------------------------------------------------------
                 * CREAMOS Y INICIAMOS EL HILO DEL CLIENTE
                 * ---------------------------------------------------------------------------
                 */
                Thread clientThread = new Thread(
                        new ClientHandler(socket, semaphore, idClient, clients, incidences, idIncidence));
                clientThread.start();
            }

        } catch (Exception e) {
            System.out.println("Error Servidor SSL: " + e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    System.out.println("Servidor cerrado correctamente");
                } catch (Exception e) {
                    System.out.println("Error al cerrar el servidor: " + e);
                }
            }
        }
    }
}