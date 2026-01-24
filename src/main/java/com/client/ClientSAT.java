package com.client;

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
