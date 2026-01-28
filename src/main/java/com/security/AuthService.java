package com.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Role;
import com.model.User;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AuthService {

    private static final String USERS_JSON_PATH = "src/main/java/com/json/users.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
        private Role rol;
        private String token;
        private String error;

        public boolean isLogin() {
            return login;
        }

        public void setLogin(boolean login) {
            this.login = login;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public Role getRol() {
            return rol;
        }

        public void setRol(Role rol) {
            this.rol = rol;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

    }

    /**
     * ------------------------------------------------------------------------
     * CREAMOS LA AUTENTIFICACIÓN (ROL) DEL USUARIO
     * -----------------------------------------------------------------------
     */
    public static authResult autentication(String user, String password) {

        authResult result = new authResult();

        // NORMALIZACION DEL RESULTADO SIMPLE PARA EVITAR LOS SALTOS DE LINEA Y TAB
        if (user != null) {
            user = user.trim();
        }
        if (password != null) {
            password = password.trim();
        }

        // SEGURIDAD SIMPLE
        if (user == null || password == null) {
            result.login = false;
            result.error = "Usuario o contraseña nulos";
            return result;
        }

        try {
            // CARGAMOS LA LISTA DE USUARIOS DESDE EL JSON
            File file = new File(USERS_JSON_PATH);
            if (!file.exists()) {
                result.login = false;
                result.error = "Archivo de usuarios no encontrado";
                return result;
            }

            List<User> users = objectMapper.readValue(file, new TypeReference<List<User>>() {
            });

            // BUSCAMOS SI EXISTE UN USUARIO QUE COINCIDA
            for (User u : users) {
                if (u.getUsername().equals(user) && u.getPassword().equals(password)) {
                    result.login = true;
                    result.user = u.getUsername();
                    result.rol = u.getRole();
                    result.token = generateToken();
                    return result;
                }
            }

        } catch (IOException e) {
            result.login = false;
            result.error = "Error al leer el archivo de usuarios: " + e.getMessage();
            return result;
        }

        result.login = false;
        result.error = "Credenciales incorrectas";
        return result;
    }

}
