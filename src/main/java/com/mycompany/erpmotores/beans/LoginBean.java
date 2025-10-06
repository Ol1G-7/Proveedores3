/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.erpmotores.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Propiedades del formulario
    private String usuario;
    private String password;
    private boolean recordar;
    
    // Usuario autenticado
    private String usuarioActual;
    private boolean autenticado;
    
    public LoginBean() {
        this.autenticado = false;
    }
    
    /**
     * Método para realizar el login
     * @return Página de destino
     */
    public String login() {
        // Validar campos
        if (usuario == null || usuario.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debes ingresar un usuario");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debes ingresar una contraseña");
            return null;
        }
        
        // Aquí iría la lógica real de autenticación con MongoDB
        // Por ejemplo: usuarioService.autenticar(usuario, password);
        
        // Simulación de autenticación
        if (autenticarUsuario(usuario, password)) {
            this.usuarioActual = usuario;
            this.autenticado = true;
            
            // Mensaje de éxito
            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Bienvenido " + usuario);
            
            // Redirigir al dashboard
            return "dashboard?faces-redirect=true";
        } else {
            // Credenciales incorrectas
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario o contraseña incorrectos");
            return null;
        }
    }
    
    /**
     * Método simulado de autenticación
     * En producción, aquí validarías contra MongoDB
     */
    private boolean autenticarUsuario(String usuario, String password) {
        // Simulación simple - CAMBIAR EN PRODUCCIÓN
        // Aquí deberías consultar tu base de datos MongoDB
        
        // Ejemplo de usuarios hardcodeados para pruebas
        if ("admin".equals(usuario) && "admin123".equals(password)) {
            return true;
        }
        if ("usuario".equals(usuario) && "12345".equals(password)) {
            return true;
        }
        
        return false;
        
        /* Ejemplo real con MongoDB:
        try {
            Usuario user = usuarioService.buscarPorUsuario(usuario);
            if (user != null) {
                // Verificar contraseña (usar bcrypt en producción)
                return passwordEncoder.matches(password, user.getPasswordHash());
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        */
    }
    
    /**
     * Método para cerrar sesión
     * @return Página de login
     */
    public String logout() {
        // Limpiar datos de sesión
        this.usuario = null;
        this.password = null;
        this.usuarioActual = null;
        this.autenticado = false;
        
        // Invalidar sesión de JSF
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        // Redirigir al login
        return "login?faces-redirect=true";
    }
    
    /**
     * Verificar si el usuario está autenticado
     * Útil para filtros de seguridad
     */
    public boolean isAutenticado() {
        return autenticado;
    }
    
    /**
     * Obtener el nombre del usuario actual
     */
    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual : "Invitado";
    }
    
    /**
     * Método auxiliar para agregar mensajes
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
    
    // ===== GETTERS Y SETTERS =====
    
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRecordar() {
        return recordar;
    }

    public void setRecordar(boolean recordar) {
        this.recordar = recordar;
    }

    public String getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(String usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
}