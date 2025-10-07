package com.mycompany.erpmotores.beans;

/**
 * Clase DTO (Data Transfer Object) para manejar la creación de un nuevo producto
 * y su registro inicial de inventario en una sola petición.
 */
public class CrearProductoRequest {
    private Producto producto;
    private Inventario inventarioInicial;

    // --- Getters y Setters ---
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Inventario getInventarioInicial() { return inventarioInicial; }
    public void setInventarioInicial(Inventario inventarioInicial) { this.inventarioInicial = inventarioInicial; }
}
