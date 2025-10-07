package com.mycompany.erpmotores.beans;

import jakarta.json.bind.annotation.JsonbTypeAdapter;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;

public class Venta {
    @JsonbTypeAdapter(ObjectIdAdapter.class) // Usa el adaptador para este campo
    private ObjectId id;
    private String folioVenta;
    private Date fechaVenta;
    @JsonbTypeAdapter(ObjectIdAdapter.class) // Usa el adaptador para este campo
    private ObjectId usuarioId;
    private double total;
    private String status;
    private List<DetalleVenta> detalle;

    // --- Getters y Setters ---
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getFolioVenta() { return folioVenta; }
    public void setFolioVenta(String folioVenta) { this.folioVenta = folioVenta; }
    public Date getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(Date fechaVenta) { this.fechaVenta = fechaVenta; }
    public ObjectId getUsuarioId() { return usuarioId; }
    public void setUsuarioId(ObjectId usuarioId) { this.usuarioId = usuarioId; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<DetalleVenta> getDetalle() { return detalle; }
    public void setDetalle(List<DetalleVenta> detalle) { this.detalle = detalle; }
}