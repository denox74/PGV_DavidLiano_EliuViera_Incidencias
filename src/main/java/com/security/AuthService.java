package com.security;

public class AuthService {

    /**
     * ------------------------------------------------------------------------
     * GENERAMOS EL TOKEN DEL CLIENTE SIMULADO
     * -----------------------------------------------------------------------
     */
    private static String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * ------------------------------------------------------------------------
     * CREAMOS LA CLASE DEL CLIENTE QUE SE VAYA A CONECTAR
     * -----------------------------------------------------------------------
     */
    public static class authResult {

        private boolean login;
        private String user;
        private String rol;
        private String token;
        private String error;
        private String rawBody;

    }

    /**
     * ------------------------------------------------------------------------
     * CREAMOS LA AUTENTIFICACIÓN (ROL) DEL USUARIO
     * -----------------------------------------------------------------------
     */
    public static authResult autentication(String user, String password) {

        authResult result = new authResult();

        //NORMALIZACION DEL RESULTADO SIMPLE PARA EVITAR LOS SALTOS DE LINEA Y TAB
        if (user != null) {
            user = user.trim();
        }
        if (password != null) {
            password = password.trim();
        }

        //SEGURIDAD SIMPLE 
        if (user == null || password == null) {
            result.login = false;
            result.error = "Usuario o contraseña nulos";
            return result;
        }

        //CREAMOS EL USUARIO ADMIN
        if ("admin".equals(user) && "admin123".equals(password)) {
            result.login = true;
            result.user = "admin";
            result.rol = "ADMIN";
            result.token = generateToken();
            return result;
        }

        //CREAMOS AL TECNICO 
        if ("tecnico".equals(user) && "tecnico123".equals(password)) {
            result.login = true;
            result.user = "tecnico";
            result.rol = "TECHNICIAN";
            result.token = generateToken();
            return result;
        }
         //CREAMOS AL CLIENTE 
        if ("cliente".equals(user) && "cliente123".equals(password)) {
            result.login = true;
            result.user = "tecnico";
            result.rol = "CLIENT";
            result.token = generateToken();
            return result;
        }

        result.login = false;
        return result;
    }

}
