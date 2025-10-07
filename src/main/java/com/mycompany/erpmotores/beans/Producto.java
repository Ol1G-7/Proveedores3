/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.erpmotores.beans;


import java.io.Serializable;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Producto implements Serializable {

    private ObjectId id;
    private String codigoProducto;
    private String nombre;
    private String marca;
    private String categoria;
    private Double precio;
    private Document caracteristicas; // Usamos Document para manejar el JSON de forma nativa
    private Date fechaCreacion;
    private Date ultimaActualizacion;

    public Producto() {
        // Constructor vacío es necesario para JSF
    }

    // Constructor completo para facilitar la creación de objetos
    public Producto(ObjectId id, String codigoProducto, String nombre, String marca, String categoria, Double precio, Document caracteristicas, Date fechaCreacion, Date ultimaActualizacion) {
        this.id = id;
        this.codigoProducto = codigoProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.caracteristicas = caracteristicas;
        this.fechaCreacion = fechaCreacion;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    // --- Getters y Setters para todos los campos ---

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Document getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(Document caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Date ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
}