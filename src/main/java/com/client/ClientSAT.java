package com.client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// Una clase para guardar los datos básicos de la conexión de un cliente. 
// Pasa archivos de información.
public class ClientSAT {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        // Cargamos el certifiicado de Truststore
        System.setProperty("javax.net.ssl.trustStore", "src/main/resources/cliente-truststore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "servidorpgvsat");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

        try {
            SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(HOST, PORT);

            // Entrada de datos, salida de datos, auto-flush, UTF-8 (para caracteres
            // especiales en español)
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true);
            Scanner sc = new Scanner(System.in);

            String msg = in.readLine();
            System.out.println("Servidor: " + msg);
            if (msg != null && msg.toLowerCase().equals("OCUPADO")) {
                socket.close();
                return;
            }

            while (true) {
                System.out.print("> ");
                String userInput = sc.nextLine();
                out.println(userInput);

                String resp = in.readLine();
                System.out.println("Servidor: " + resp);

                if ("SALIR".equalsIgnoreCase(userInput)) break;
            }

            socket.close();

        } catch (Exception e) {
            System.out.println("Error Cliente SSL: " + e.getMessage());
        }
    }

}
