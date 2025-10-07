/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.erpmotores.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("usuarioBean")
@ViewScoped
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
                .filter(u -> u.getNombre().toLowerCase().contains(f) ||
                             u.getCorreo().toLowerCase().contains(f) ||
                             u.getRol().toLowerCase().contains(f))
                .collect(Collectors.toList());
    }

    public void agregarUsuario() {
        // Ejemplo: agrega un usuario demo. Reemplaza por lógica de formulario/modal
        Document nuevo = new Document("nombre", "Nuevo Usuario")
                .append("email", "nuevo@correo.com")
                .append("rol", "Usuario");
        mongoDao.insertarDocumento("usuarios", nuevo);
        cargarUsuarios();
    }

    public void editarUsuario(Usuario usuario) {
        // Lógica para editar (normalmente abres modal, aquí ejemplo simple)
        // Por ejemplo, cambiar el rol:
        Document filtro = new Document("_id", usuario.getId());
        Document actualizacion = new Document("$set", new Document("rol", "Admin"));
        mongoDao.actualizarUnDocumento("usuarios", filtro, actualizacion);
        cargarUsuarios();
    }

    public void eliminarUsuario(Usuario usuario) {
        Document filtro = new Document("_id", usuario.getId());
        mongoDao.eliminarUnDocumento("usuarios", filtro);
        cargarUsuarios();
    }

    // Getters y Setters
    public List<Usuario> getListaUsuarios() { return listaUsuarios; }
    public void setListaUsuarios(List<Usuario> listaUsuarios) { this.listaUsuarios = listaUsuarios; }
    public String getFiltro() { return filtro; }
    public void setFiltro(String filtro) { this.filtro = filtro; }
    public Usuario getUsuarioSeleccionado() { return usuarioSeleccionado; }
    public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }
}