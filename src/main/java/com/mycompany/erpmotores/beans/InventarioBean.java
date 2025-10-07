package com.mycompany.erpmotores.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.types.ObjectId;

@Named("inventarioBean")
@SessionScoped
public class InventarioBean implements Serializable {

    private List<Inventario> listaInventarios;
    private Inventario inventarioSeleccionado;
    private List<Producto> listaProductosDisponibles; // Para el dropdown del formulario
    private MongoClass mongoDao;

    @PostConstruct
    public void init() {
        mongoDao = new MongoClass();
        cargarProductosDisponibles();
        cargarInventarios();
    }

    // EN: InventarioBean.java
    public void cargarProductosDisponibles() {
        // Inicializamos la lista para asegurarnos de que esté limpia
        this.listaProductosDisponibles = new ArrayList<>();

        try {
            List<Document> docs = mongoDao.buscarDocumentos("productos", new Document());

            if (docs == null) {
                System.err.println("Error: La búsqueda de productos devolvió null.");
                return;
            }

            for (Document doc : docs) {
                // Verificamos que los campos esenciales existan antes de crear el objeto
                if (doc.containsKey("_id") && doc.containsKey("nombre")) {
                    Producto p = new Producto();
                    p.setId(doc.getObjectId("_id"));
                    p.setNombre(doc.getString("nombre"));
                    this.listaProductosDisponibles.add(p);
                }
            }
        } catch (Exception e) {
            // Imprimimos cualquier error que ocurra durante la carga
            System.err.println("Ocurrió una excepción al cargar los productos disponibles: " + e.getMessage());
            e.printStackTrace(); // Esto mostrará el error completo en los logs del servidor
        }
    }

    public void cargarInventarios() {
        listaInventarios = new ArrayList<>();
        List<Document> docsInventario = mongoDao.buscarDocumentos("inventarios", new Document());

        // Para ser eficientes, creamos un mapa de ProductoID -> NombreProducto
        Map<ObjectId, String> mapaProductos = listaProductosDisponibles.stream()
                .collect(Collectors.toMap(Producto::getId, Producto::getNombre));

        for (Document doc : docsInventario) {
            Inventario inv = new Inventario();
            inv.setId(doc.getObjectId("_id"));
            inv.setProductoId(doc.getObjectId("producto_id"));
            inv.setStock(doc.getInteger("stock", 0));
            inv.setUbicacion(doc.getString("ubicacion"));
            inv.setMinimo(doc.getInteger("minimo", 0));
            inv.setMaximo(doc.getInteger("maximo", 0));
            inv.setFechaActualizacion(doc.getDate("fecha_actualizacion"));

            // Usamos el mapa para asignar el nombre del producto de forma eficiente
            inv.setNombreProducto(mapaProductos.getOrDefault(inv.getProductoId(), "Producto no encontrado"));

            listaInventarios.add(inv);
        }
    }

    // --- Métodos de Navegación y Acción ---
    public String navegarAFormularioParaCrear() {
        this.inventarioSeleccionado = new Inventario();
        return "inventarioForm?faces-redirect=true";
    }

    public String navegarAFormularioParaEditar(Inventario inventario) {
        this.inventarioSeleccionado = inventario;
        return "inventarioForm?faces-redirect=true";
    }

    public String guardarYRedirigir() {
        // Validación básica inicial
        if (inventarioSeleccionado == null || inventarioSeleccionado.getProductoId() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un producto."));
            return null; // No continuar si no hay producto seleccionado
        }

        // --- INICIO DE VALIDACIONES DE NEGOCIO ---
        boolean validacionFallida = false;

        // Regla 1: No se puede crear un registro de inventario para un producto que ya lo tiene.
        // (Esta validación solo se aplica al crear, no al editar)
        if (inventarioSeleccionado.getId() == null) {
            Document filtroExistencia = new Document("producto_id", inventarioSeleccionado.getProductoId());
            Document registroExistente = mongoDao.buscarUnDocumento("inventarios", filtroExistencia);
            if (registroExistente != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registro Duplicado", "Ya existe un registro de inventario para este producto."));
                validacionFallida = true;
            }
        }

        // Regla 2: El stock máximo no puede ser menor que el stock mínimo.
        if (inventarioSeleccionado.getMaximo() > 0 && inventarioSeleccionado.getMaximo() < inventarioSeleccionado.getMinimo()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "El stock máximo no puede ser menor que el stock mínimo.", ""));
            validacionFallida = true;
        }

        // Regla 3: El stock actual debe estar dentro del rango mínimo y máximo (si aplica).
        if (inventarioSeleccionado.getStock() < inventarioSeleccionado.getMinimo()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "El stock actual no puede ser menor que el stock mínimo.", ""));
            validacionFallida = true;
        }

        if (inventarioSeleccionado.getMaximo() > 0 && inventarioSeleccionado.getStock() > inventarioSeleccionado.getMaximo()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "El stock actual no puede ser mayor que el stock máximo.", ""));
            validacionFallida = true;
        }

        // Si alguna de las validaciones anteriores falló, detenemos la ejecución.
        if (validacionFallida) {
            return null; // JSF se quedará en la misma página y mostrará los mensajes de error.
        }
        // --- FIN DE VALIDACIONES DE NEGOCIO ---

        // Si todas las validaciones pasan, procedemos a guardar en la base de datos.
        Document inventarioDoc = new Document()
                .append("producto_id", inventarioSeleccionado.getProductoId())
                .append("stock", inventarioSeleccionado.getStock())
                .append("ubicacion", inventarioSeleccionado.getUbicacion())
                .append("minimo", inventarioSeleccionado.getMinimo())
                .append("maximo", inventarioSeleccionado.getMaximo())
                .append("fecha_actualizacion", new Date());

        if (inventarioSeleccionado.getId() == null) {
            // Crear
            mongoDao.insertarDocumento("inventarios", inventarioDoc);
        } else {
            // Actualizar
            Document filtro = new Document("_id", inventarioSeleccionado.getId());
            Document actualizacion = new Document("$set", inventarioDoc);
            mongoDao.actualizarUnDocumento("inventarios", filtro, actualizacion);
        }

        cargarInventarios();
        return "inventarios?faces-redirect=true";
    }

    public void eliminarInventario(Inventario inventario) {
        Document filtro = new Document("_id", inventario.getId());
        mongoDao.eliminarUnDocumento("inventarios", filtro);
        cargarInventarios();
    }

    // --- Getters y Setters ---
    public List<Inventario> getListaInventarios() {
        return listaInventarios;
    }

    public Inventario getInventarioSeleccionado() {
        return inventarioSeleccionado;
    }

    public void setInventarioSeleccionado(Inventario inventarioSeleccionado) {
        this.inventarioSeleccionado = inventarioSeleccionado;
    }

    public List<Producto> getListaProductosDisponibles() {
        return listaProductosDisponibles;
    }
}
