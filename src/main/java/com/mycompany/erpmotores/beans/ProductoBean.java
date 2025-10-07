/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.erpmotores.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

@Named("productoBean")
@SessionScoped
public class ProductoBean implements Serializable {

    private List<Producto> listaProductos;
    private Producto productoSeleccionado;
    private String caracteristicasJson = "{}"; // String para enlazar con el textarea del formulario
    private MongoClass mongoDao;

    @PostConstruct
    public void init() {
        mongoDao = new MongoClass();
        cargarProductos();
    }

    public void cargarProductos() {
        listaProductos = new ArrayList<>();
        // Por ahora cargamos todos los productos, ordenados por nombre
        List<Document> docs = mongoDao.buscarDocumentos("productos", new Document());

        for (Document doc : docs) {
            Producto p = new Producto(
                    doc.getObjectId("_id"),
                    doc.getString("codigo_producto"),
                    doc.getString("nombre"),
                    doc.getString("marca"),
                    doc.getString("categoria"),
                    doc.getDouble("precio"),
                    doc.get("caracteristicas", Document.class), // Obtenemos el sub-documento
                    doc.getDate("fecha_creacion"),
                    doc.getDate("ultima_actualizacion")
            );
            listaProductos.add(p);
        }
    }

    // --- Métodos de Navegación ---
    public String navegarAFormularioParaCrear() {
        this.productoSeleccionado = new Producto();
        this.caracteristicasJson = "{\n  \"clave\": \"valor\"\n}"; // Ejemplo para el usuario
        return "productoForm?faces-redirect=true";
    }

    public String navegarAFormularioParaEditar(Producto producto) {
        this.productoSeleccionado = producto;

        // Convertimos el Document de características a un String JSON formateado
        if (producto.getCaracteristicas() != null) {
            // ✅ LÍNEA CORREGIDA: Usamos el builder para crear la configuración
            this.caracteristicasJson = producto.getCaracteristicas().toJson(JsonWriterSettings.builder().indent(true).build());
        } else {
            this.caracteristicasJson = "{}";
        }

        return "productoForm?faces-redirect=true";
    }

    public void validarCodigoUnico(FacesContext fc, UIComponent component, Object value) throws ValidatorException {
        String codigo = (String) value;
        if (codigo == null || codigo.isEmpty()) {
            return; // No validar si el campo está vacío
        }

        Document filtro = new Document("codigo_producto", codigo.trim());
        Document productoExistente = mongoDao.buscarUnDocumento("productos", filtro);

        boolean esEdicion = (productoSeleccionado != null && productoSeleccionado.getId() != null);

        if (esEdicion) {
            // En edición, el código es válido si le pertenece al producto que estamos editando
            if (productoExistente != null && !productoExistente.getObjectId("_id").equals(productoSeleccionado.getId())) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El código del producto ya existe.", null));
            }
        } else {
            // En creación, el código no debe existir
            if (productoExistente != null) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El código del producto ya existe.", null));
            }
        }
    }

    // --- Métodos de Acción ---
    public String guardarYRedirigir() {
        if (productoSeleccionado == null) {
            return null; // O a página de error
        }

        // Convertir el String JSON del formulario de vuelta a un Document BSON
        try {
            productoSeleccionado.setCaracteristicas(Document.parse(caracteristicasJson));
        } catch (Exception e) {
            // Manejar error de JSON inválido, por ahora lo dejamos como objeto vacío
            productoSeleccionado.setCaracteristicas(new Document());
        }

        Document productoDoc = new Document()
                .append("codigo_producto", productoSeleccionado.getCodigoProducto())
                .append("nombre", productoSeleccionado.getNombre())
                .append("marca", productoSeleccionado.getMarca())
                .append("categoria", productoSeleccionado.getCategoria())
                .append("precio", productoSeleccionado.getPrecio())
                .append("caracteristicas", productoSeleccionado.getCaracteristicas())
                .append("ultima_actualizacion", new Date()); // Siempre actualizamos esta fecha

        if (productoSeleccionado.getId() == null) {
            // --- CREAR NUEVO PRODUCTO ---
            productoDoc.append("fecha_creacion", new Date()); // La fecha de creación solo se añade al crear
            mongoDao.insertarDocumento("productos", productoDoc);
        } else {
            // --- ACTUALIZAR PRODUCTO EXISTENTE ---
            Document filtro = new Document("_id", productoSeleccionado.getId());
            Document actualizacion = new Document("$set", productoDoc);
            mongoDao.actualizarUnDocumento("productos", filtro, actualizacion);
        }

        cargarProductos();
        return "productos?faces-redirect=true";
    }

    public void eliminarProducto(Producto producto) {
        Document filtro = new Document("_id", producto.getId());
        mongoDao.eliminarUnDocumento("productos", filtro);
        cargarProductos();
    }

    // --- Getters y Setters ---
    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(Producto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public String getCaracteristicasJson() {
        return caracteristicasJson;
    }

    public void setCaracteristicasJson(String caracteristicasJson) {
        this.caracteristicasJson = caracteristicasJson;
    }
}
