package com.client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;



// Una clase para guardar los datos básicos de la conexión de un cliente. 
// Pasa archivos de información.
public class ClientSAT {
    private final String ipRemote;
    private final int portClient;
    private final int portServer;

    public ClientSAT(String ipRemote, int portClient, int portServer) {
        this.ipRemote = ipRemote;
        this.portClient = portClient;
        this.portServer = portServer;
    }

    public String getIpRemote() {
        return ipRemote;
    }

    public int getPortClient() {
        return portClient;
    }

    public int getPortServer() {
        return portServer;
    }
}
