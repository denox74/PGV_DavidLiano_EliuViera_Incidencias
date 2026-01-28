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
            System.out.println("Servidor: " + msg);

            // Si el servidor está lleno, cerrar conexión
            if (msg != null && msg.contains("lleno")) {
                socket.close();
                return;
            }

            /**
             * ---------------------------------------------------------------------------
             * PROCESO DE AUTENTICACIÓN - USUARIO
             * ---------------------------------------------------------------------------
             */
            String userPrompt = in.readLine();
            System.out.println("Servidor: " + userPrompt);
            String username = sc.nextLine();
            out.println(username);

            /**
             * ---------------------------------------------------------------------------
             * PROCESO DE AUTENTICACIÓN - PASSWORD
             * ---------------------------------------------------------------------------
             */
            String passPrompt = in.readLine();
            System.out.println("Servidor: " + passPrompt);
            String password = sc.nextLine();
            out.println(password);

            /**
             * ---------------------------------------------------------------------------
             * LEER MENSAJE DE BIENVENIDA Y MENÚ DE COMANDOS
             * ---------------------------------------------------------------------------
             */
            String welcomeMsg = in.readLine();
            System.out.println("Servidor: " + welcomeMsg);

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

                String resp = in.readLine();
                System.out.println("Servidor: " + resp);

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
