package com.client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;



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

        try{
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(HOST, PORT);

            //Entrada de datos, salida de datos, auto-flush, UTF-8 (para caracteres especiales en español)
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            
            String msg = in.readLine();
            System.out.println("Servidor: "+ msg);
            socket.close();

            
        }catch(Exception e){
            System.out.println("Error Cliente SSL: "+ e.getMessage());
        }
    }


}
