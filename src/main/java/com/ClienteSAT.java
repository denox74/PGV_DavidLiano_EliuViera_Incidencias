package com;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// El cliente que usamos para conectarnos al SAT. 
// Es básicamente la interfaz por consola para que el usuario mande comandos.
public class ClienteSAT {

    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        String host = (args.length >= 1) ? args[0] : HOST;
        int puerto = (args.length >= 2) ? Integer.parseInt(args[1]) : PUERTO;

        try (
                Socket socket = new Socket(host, puerto);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter salida = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                        true);
                Scanner sc = new Scanner(System.in)) {
            System.out.println("Conectado al Servicio de Incidencias");

            // Leer bienvenida del servidor
            String bienvenida = entrada.readLine();
            if (bienvenida != null)
                System.out.println(bienvenida);

            while (true) {
                System.out.print("Comando (ALTA, LISTAR, CLIENTES, SALIR): ");
                String cmd = sc.nextLine().trim();

                if (cmd.equalsIgnoreCase("ALTA")) {
                    System.out.print("Descripcion: ");
                    String desc = sc.nextLine().trim();
                    salida.println("ALTA " + desc);

                    System.out.println(entrada.readLine());
                    continue;
                }

                // Enviar comando tal cual
                salida.println(cmd);

                if (cmd.equalsIgnoreCase("LISTAR") || cmd.equalsIgnoreCase("CLIENTES")) {
                    String linea;
                    while ((linea = entrada.readLine()) != null) {
                        if (linea.equals("FIN"))
                            break;
                        System.out.println(linea);
                    }
                    continue;
                }

                // Respuesta de una línea (incluye SALIR)
                String resp = entrada.readLine();
                if (resp == null) {
                    System.out.println("El servidor cerro la conexion.");
                    break;
                }
                System.out.println(resp);

                if (cmd.equalsIgnoreCase("SALIR")) {
                    System.out.println("Cliente finalizado");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error cliente: " + e.getMessage());
        }
    }
}
