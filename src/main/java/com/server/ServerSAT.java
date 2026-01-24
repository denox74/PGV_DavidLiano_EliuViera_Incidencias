package com.server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ServerSAT {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        // Ponemos las Kestores del servidor
        System.setProperty("javax.net.ssl.keyStore", "src/main/resources/servidor-keystore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "servidorpgvsat");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        System.out.println("ServidAT (SSL) arrancado en puerto" + PORT);

        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(PORT);

            while (true) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                System.out.println("Cliente SSL conectado: " + socket.getInetAddress().getHostAddress());
                // Salida de datos, para enviar mensajes al cliente, auto-flush, UTF-8 (para
                // caracteres especiales en español)
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                out.println("CONECTADO");
                socket.close();

            }

        } catch (Exception e) {
            System.out.println("Error Servirdor SSL: " + e.getMessage());
        }

    }
}
