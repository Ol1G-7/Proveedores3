/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.erpmotores.beans;

import com.mongodb.client.model.Updates;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.conversions.Bson;

@Named("usuarioBean")
@SessionScoped // <-- CAMBIO DE @ViewScoped A @SessionScoped
public class UsuarioBean implements Serializable {

    private List<Usuario> listaUsuarios;
    private String filtro = "";
    private Usuario usuarioSeleccionado;
    private MongoClass mongoDao;

    @PostConstruct
    public void init() {
        mongoDao = new MongoClass();
        cargarUsuarios();
    }

    public void cargarUsuarios() {
        listaUsuarios = new ArrayList<>();
        List<Document> docs = mongoDao.buscarDocumentos("usuarios", new Document());
        for (Document doc : docs) {
            Usuario u = new Usuario(
                    doc.getObjectId("_id"),
                    doc.getString("nombre"),
                    doc.getString("email"),
                    doc.getString("rol")
            );
            listaUsuarios.add(u);
        }
    }

    public List<Usuario> getUsuariosFiltrados() {
        if (filtro == null || filtro.isBlank()) {
            return listaUsuarios;
        }
        String f = filtro.toLowerCase().trim();
        return listaUsuarios.stream()
                .filter(u -> u.getNombre().toLowerCase().contains(f)
                || u.getCorreo().toLowerCase().contains(f)
                || u.getRol().toLowerCase().contains(f))
                .collect(Collectors.toList());
    }

// --- MÉTODOS DE NAVEGACIÓN ---
    public String navegarAFormularioParaCrear() {
        this.usuarioSeleccionado = new Usuario();
        return "usuarioForm?faces-redirect=true";
    }

    public String navegarAFormularioParaEditar(Usuario usuario) {
        this.usuarioSeleccionado = usuario;
        return "usuarioForm?faces-redirect=true";
    }

    public String guardarYRedirigir() {
        if (usuarioSeleccionado == null) {
            return null; // O redirigir a una página de error
        }

        if (usuarioSeleccionado.getId() == null) {
            // --- LÓGICA PARA CREAR UN NUEVO USUARIO ---
            Document nuevoUsuarioDoc = new Document()
                    .append("nombre", usuarioSeleccionado.getNombre())
                    .append("email", usuarioSeleccionado.getCorreo())
                    .append("rol", usuarioSeleccionado.getRol());

            if (usuarioSeleccionado.getPassword() != null && !usuarioSeleccionado.getPassword().isEmpty()) {
                nuevoUsuarioDoc.append("password", usuarioSeleccionado.getPassword());
            }
            mongoDao.insertarDocumento("usuarios", nuevoUsuarioDoc);
        } else {
            // --- LÓGICA PARA ACTUALIZAR UN USUARIO EXISTENTE ---
            Document filtroDoc = new Document("_id", usuarioSeleccionado.getId());
            Document datosAActualizar = new Document()
                    .append("nombre", usuarioSeleccionado.getNombre())
                    .append("email", usuarioSeleccionado.getCorreo())
                    .append("rol", usuarioSeleccionado.getRol());

            if (usuarioSeleccionado.getPassword() != null && !usuarioSeleccionado.getPassword().trim().isEmpty()) {
                datosAActualizar.append("password", usuarioSeleccionado.getPassword());
            }
            Document actualizacionFinal = new Document("$set", datosAActualizar);
            mongoDao.actualizarUnDocumento("usuarios", filtroDoc, actualizacionFinal);
        }

        cargarUsuarios();
        return "usuarios?faces-redirect=true";
    }

    public void eliminarUsuario(Usuario usuario) {
        // ... tu código para eliminar (sin cambios)
    }

    // Getters y Setters
    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Usuario getUsuarioSeleccionado() {
        return usuarioSeleccionado;
    }

    public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) {
        this.usuarioSeleccionado = usuarioSeleccionado;
    }

}
