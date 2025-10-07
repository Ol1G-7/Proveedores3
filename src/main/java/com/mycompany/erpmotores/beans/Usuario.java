package com.mycompany.erpmotores.beans;

import org.bson.types.ObjectId;
import java.io.Serializable;

public class Usuario implements Serializable {

    private ObjectId id;
    private String nombre;
    private String correo;
    private String rol;
    private String password; // <-- CAMPO AÑADIDO
    private boolean activo; // <-- CAMBIO A boolean

    public Usuario() {
    }

    // Constructor actualizado para incluir el nuevo campo
    public Usuario(ObjectId id, String nombre, String correo, String rol, boolean status) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.activo = status; // <-- CAMPO AÑADIDO
    }

    // Getters y Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    // --- GETTERS Y SETTERS ---
    // (Getters y setters para id, nombre, correo, rol, password...)

    public boolean isActivo() { // <-- GETTER para boolean
        return activo;
    }

    public void setActivo(boolean activo) { // <-- SETTER para boolean
        this.activo = activo;
    }

    // --- GETTER Y SETTER AÑADIDOS ---
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
