# Sistema de Gestión de Incidencias (SAT)



# Integrantes

*   David Liaño Macías
*   Eliu Manuel Viera Lorenzo

## Descripción General
* Pasos a tener en cuenta:
Servidor debe: escuchar (5000 parametrizable), multihilo, datos compartidos seguros, LOGIN + roles (usuario/admin), limitación de clientes (10) con semáforo, normalizar/validar comandos, y sockets seguros SSL/TLS.
Cliente debe: conexión parametrizable, manejar rechazo, hacer LOGIN, mostrar menú según rol, enviar comandos y mostrar respuestas.

## Características Principales
* Clases
\client\ClientSAT.java  (Clase para la conexión del cliente)
\server\ServerSAT.java  (Clase para la conexión del servidor)
\model\ClientConnected.java (Clase para la gestión de los clientes conectados)
\model\Incidence.java (Clase para la gestión de las incidencias)
\util\Normalizer.java (Clase para la normalización de los comandos)
\security\Auth.java (Clase para la autenticación)
\server\ClientHandler.java (Clase para el manejo de los hilos de los clientes)



## Protocolo de Comandos





## Estructura del Proyecto




## Requisitos

*   Java Development Kit (JDK) 8 o superior.
*   Maven para la gestión de dependencias y construcción del proyecto.

## Configuración del pom.xml

Tener encuenta añadir las dependencias de spring boot y jackson para poder gestionar las incidencias en formato JSON.