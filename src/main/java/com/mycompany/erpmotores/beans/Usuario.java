package com.mycompany.erpmotores.beans;

import org.bson.types.ObjectId;
import java.io.Serializable;

public class Usuario implements Serializable {
    private ObjectId id;
    private String nombre;
    private String correo;
    private String rol;
    private String password; // <-- CAMPO AÑADIDO

    public Usuario() {}

    public Usuario(ObjectId id, String nombre, String correo, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
    }

    // Getters y Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    // --- GETTER Y SETTER AÑADIDOS ---
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}