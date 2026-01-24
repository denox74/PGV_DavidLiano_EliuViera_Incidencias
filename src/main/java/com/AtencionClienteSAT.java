package com;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// Esta clase es la que se encarga de "atender" a cada cliente que se conecta. 
// Funciona en su propio hilo para no bloquear al servidor principal.
public class AtencionClienteSAT implements Runnable {

    private final Socket socket;
    private final int idCliente;
    private final Map<Integer, ClienteInfo> clientesConectados;
    private final List<Incidencia> incidencias; // es synchronizedList
    private final AtomicInteger contadorIncidencias;

    public AtencionClienteSAT(Socket socket,
            int idCliente,
            Map<Integer, ClienteInfo> clientesConectados,
            List<Incidencia> incidencias,
            AtomicInteger contadorIncidencias) {
        this.socket = socket;
        this.idCliente = idCliente;
        this.clientesConectados = clientesConectados;
        this.incidencias = incidencias;
        this.contadorIncidencias = contadorIncidencias;
    }

    @Override
    public void run() {
        try (
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter salida = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                        true)) {
            // Bienvenida
            salida.println("OK Bienvenido. Tu ID de cliente es " + idCliente);

            String linea;
            while ((linea = entrada.readLine()) != null) {
                linea = linea.trim();

                if (linea.equalsIgnoreCase("SALIR")) {
                    salida.println("Conexion cerrada");
                    break;
                }

                if (linea.equalsIgnoreCase("LISTAR")) {
                    //para iterar una synchronizedList, sincroniza al iterar
                    synchronized (incidencias) {
                        for (Incidencia inc : incidencias) {
                            salida.println(inc.getId() + " - " + inc.getDescripcion());
                        }
                    }
                    salida.println("FIN");
                    continue;
                }

                if (linea.equalsIgnoreCase("CLIENTES")) {
                    for (Map.Entry<Integer, ClienteInfo> entry : clientesConectados.entrySet()) {
                        ClienteInfo info = entry.getValue();
                        salida.println(
                                "IP: " + info.getIpRemota() +
                                        " | Puerto cliente: " + info.getPuertoCliente() +
                                        " | Puerto servidor: " + info.getPuertoServidor());
                    }
                    salida.println("FIN");
                    continue;
                }

                if (linea.toUpperCase().startsWith("ALTA ")) {
                    String desc = linea.substring(5).trim();
                    if (desc.isEmpty()) {
                        salida.println("ERROR Descripcion vacia");
                        continue;
                    }

                    int idInc = contadorIncidencias.getAndIncrement();
                    incidencias.add(new Incidencia(idInc, desc));
                    salida.println("OK Incidencia registrada con ID " + idInc);
                    continue;
                }

                salida.println("ERROR Comando no valido");
            }

        } catch (IOException e) {
            // cliente se desconectó, por error o por salir
            System.out.println("> Cliente con ID " + idCliente + " desconectado inesperadamente");
        } finally {
            // limpiar conectados y cerrar socket
            clientesConectados.remove(idCliente);
            System.out.println("> Cliente con ID " + idCliente + " eliminado de conectados");
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            System.out.println("~> Socket cliente cerrado");
        }
    }
}
