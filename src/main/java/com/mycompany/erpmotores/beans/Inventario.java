package com.mycompany.erpmotores.beans;

import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

public class Inventario implements Serializable {

    private ObjectId id;
    private ObjectId productoId; // Para la referencia al producto
    private int stock;
    private String ubicacion;
    private int minimo;
    private int maximo;
    private Date fechaActualizacion;

    // --- Atributo extra para mostrar el nombre del producto en la tabla ---
    private String nombreProducto;

    public Inventario() {}

    // --- Getters y Setters ---

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public ObjectId getProductoId() { return productoId; }
    public void setProductoId(ObjectId productoId) { this.productoId = productoId; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public int getMinimo() { return minimo; }
    public void setMinimo(int minimo) { this.minimo = minimo; }

    public int getMaximo() { return maximo; }
    public void setMaximo(int maximo) { this.maximo = maximo; }

    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
}