/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.erpmotores.api;


import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Clase de configuración que activa JAX-RS (la API REST) en la aplicación.
 * La anotación @ApplicationPath define la URL base para todos los endpoints.
 */
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {
    // Esta clase no necesita contener ningún método. Su sola presencia y
    // la anotación @ApplicationPath son suficientes para la configuración.
}