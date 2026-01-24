package com.server;


import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ServerSAT {

    private static final int PORT = 5000;

    public static void main(String[] args) {    

        //Ponemos las Kestores del servidor
        System.setProperty("javax.net.ssl.keyStore", "src/main/resources/servidor-keystrore.p12");
        System.setProperty("java.net.ssl.ketStorePassword", "servidorpgvsat");
        System.setProperty("java.net.ssl.ketStoreType", "PKCS12");

        System.out.println("ServidAT (SSL) arrancado en puerto"+ PORT);

        try{
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(PORT);

            while(true){
                
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    }
