package com;
// Clase para guardar los datos básicos de conexión (No sé si es mejor práctica dejarla definida aquí o en el cliente, me gusta definir cada clase por separado *David)
public class AuthResult {
    private boolean login;
    private String usuario;
    private String rol;
    private String error;
    private String rawBody;
}