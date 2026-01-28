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

        /**
         * ---------------------------------------------------------------------------
         * CARGAMOS EL CERTIFICADO DE TRUSTSTORE
         * ---------------------------------------------------------------------------
         */

        System.setProperty("javax.net.ssl.trustStore", "src/main/resources/cliente-truststore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "servidorpgvsat");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

        /**
         * ---------------------------------------------------------------------------
         * INICIAMOS EL SSL SOCKET Y EL SCANNER PARA ENTRADA DE DATOS Y LOS DEJAMOS
         * NULL PARA PODER USARLOS EN EL TRY
         * ---------------------------------------------------------------------------
         */

        SSLSocket socket = null;
        Scanner sc = null;

        try {

            System.out.println("<-- GESTOR DE INCIDENCIAS SAT -->");
            /**
             * ---------------------------------------------------------------------------
             * CREAMOS EL SOCKET SSL
             * ---------------------------------------------------------------------------
             */
            socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(HOST, PORT);

            /**
             * ---------------------------------------------------------------------------
             * ENTRADA DE DATOS, SALIDA DE DATOS, AUTO-FLUSH, UTF-8 (para caracteres
             * especiales en español)
             * ---------------------------------------------------------------------------
             */
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true);
            sc = new Scanner(System.in);

            /**
             * ---------------------------------------------------------------------------
             * LEEMOS EL MENSAJE INICIAL DEL SERVIDOR
             * ---------------------------------------------------------------------------
             */
            String msg = in.readLine();

            // Si el servidor está lleno, cerrar conexión
            if (msg != null && msg.contains("lleno")) {
                System.out.println("Servidor: " + msg);
                socket.close();
                return;
            }

            String serverResponse = msg;

            while (serverResponse != null) {
                System.out.println("Servidor: " + serverResponse);

                // Si es un prompt de Usuario
                if (serverResponse.contains("Usuario")) {
                    System.out.print("Introduzca su usuario > ");
                    String username = sc.nextLine();
                    out.println(username);
                }
                // Si es un prompt de Password
                else if (serverResponse.contains("Password")) {
                    System.out.print("Introduzca su contraseña > ");
                    String password = sc.nextLine();
                    out.println(password);
                }
                // Si la autenticación fue exitosa
                else if (serverResponse.contains("Bienvenido")) {
                    break;
                }

                // Leemos el siguiente mensaje del servidor
                serverResponse = in.readLine();
            }

            if (serverResponse == null) {
                System.out.println("Servidor cerró la conexión.");
                socket.close();
                return;
            }

            // Leer el menú de comandos (puede ser múltiples líneas)
            String menuLine;
            while ((menuLine = in.readLine()) != null) {
                System.out.println(menuLine);
                // Si la línea contiene "SALIR", es la última del menú
                if (menuLine.contains("SALIR")) {
                    break;
                }
            }

            /**
             * ---------------------------------------------------------------------------
             * BUCLE PRINCIPAL DE COMANDOS
             * ---------------------------------------------------------------------------
             */
            while (true) {
                System.out.print("\n> ");
                String userInput = sc.nextLine();
                System.out.println("\n");
                if (userInput == null || userInput.trim().isEmpty()) {
                    System.out.println("Comando vacio - Escriba AYUDA para ver los comandos");
                    continue;
                }
                out.println(userInput);

                // Leer respuestas del servidor hasta el marcador de fin
                String resp;
                boolean isDescription = false;

                while ((resp = in.readLine()) != null) {
                    if (resp.equals("FIN")) {
                        break;
                    }

                    // Si el servidor está pidiendo la descripción
                    if (resp.contains("Ingrese la descripción")) {
                        System.out.println("Servidor: " + resp);
                        System.out.print("Descripción > ");
                        String description = sc.nextLine();
                        out.println(description);
                        isDescription = true;

                        // Leer la respuesta final después de enviar la descripción
                        while ((resp = in.readLine()) != null) {
                            if (resp.equals("FIN")) {
                                break;
                            }
                            System.out.println("Servidor: " + resp);
                        }
                        break;
                    } else {
                        System.out.println("Servidor: " + resp);
                    }
                }

                if (resp == null) {
                    System.out.println("El servidor ha cerrado la conexión.");
                    break;
                }

                if ("SALIR".equalsIgnoreCase(userInput)) {
                    socket.close();
                    break;
                }

            }

        } catch (Exception e) {
            System.out.println("Error Cliente SSL: " + e.getMessage());
        }
    }

}
