package com;

// Una clase para guardar los datos básicos de la conexión de un cliente. 
// Pasa archivos de información.
public class ClienteInfo {
    private final String ipRemota;
    private final int puertoCliente;
    private final int puertoServidor;

    public ClienteInfo(String ipRemota, int puertoCliente, int puertoServidor) {
        this.ipRemota = ipRemota;
        this.puertoCliente = puertoCliente;
        this.puertoServidor = puertoServidor;
    }

    public String getIpRemota() {
        return ipRemota;
    }

    public int getPuertoCliente() {
        return puertoCliente;
    }

    public int getPuertoServidor() {
        return puertoServidor;
    }
}
