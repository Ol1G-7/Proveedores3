package com.mycompany.erpmotores.beans;

import jakarta.json.bind.annotation.JsonbTypeAdapter;
import org.bson.types.ObjectId;

public class DetalleVenta {
    @JsonbTypeAdapter(ObjectIdAdapter.class) // Usa el adaptador para este campo
    private ObjectId productoId;
    private String nombre;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    // --- Getters y Setters ---
    public ObjectId getProductoId() { return productoId; }
    public void setProductoId(ObjectId productoId) { this.productoId = productoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}