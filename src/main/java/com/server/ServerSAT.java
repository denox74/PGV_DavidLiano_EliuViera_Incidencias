package com.server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

public class ServerSAT {

    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 10; // Capacidad del semaforo (clientes simultaneos)

    public static void main(String[] args) {

        // Ponemos las Kestores del servidor
        System.setProperty("javax.net.ssl.keyStore", "src/main/resources/servidor-keystore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "servidorpgvsat");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        Semaphore semaphore = new Semaphore(MAX_CLIENTS, true);
        System.out.println("ServidAT (SSL) arrancado en puerto" + PORT);

        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(PORT);

            while (true) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();

                if (!semaphore.tryAcquire()) {
                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                            true
                    );
                    out.println("Servidor lleno, vuelva a intentarlo mas tarde");
                    socket.close();
                    continue;
                }

                System.out.println("Cliente SSL conectado: " + socket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(socket, semaphore, MAX_CLIENTS, clients, incidences, idIncidence));
            }

        } catch (Exception e) {
            System.out.println("Error Servidor SSL: " + e);
        }
    }
}