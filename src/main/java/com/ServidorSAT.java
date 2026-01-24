package com;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Abre el puerto, espera clientes y les asigna un hilo 
public class ServidorSAT {

    private static final int PUERTO = 5000;

    // Compartidos entre hilos (concurrencia)
    private static final AtomicInteger contadorClientes = new AtomicInteger(1);
    private static final AtomicInteger contadorIncidencias = new AtomicInteger(1);

    private static final Map<Integer, ClienteInfo> clientesConectados = new ConcurrentHashMap<>();
    private static final List<Incidencia> incidencias = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println(">>> Servidor SAT arrancado <<<");
        System.out.println("> Escuchando en el puerto " + PUERTO);

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socketCliente = servidor.accept();

                int idCliente = contadorClientes.getAndIncrement();

                String ipRemota = socketCliente.getInetAddress().getHostAddress();
                int puertoCliente = socketCliente.getPort();
                int puertoServidor = socketCliente.getLocalPort();

                clientesConectados.put(idCliente, new ClienteInfo(ipRemota, puertoCliente, puertoServidor));

                System.out.println("> Cliente conectado");
                System.out.println("> Cliente con ID " + idCliente + " en sesion");

                Thread hilo = new Thread(
                        new AtencionClienteSAT(
                                socketCliente,
                                idCliente,
                                clientesConectados,
                                incidencias,
                                contadorIncidencias),
                        "Cliente-" + idCliente);
                hilo.start();
            }
        } catch (Exception e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }
}
