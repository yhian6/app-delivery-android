package com.yhian.miprimeraapp.modelo;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String uid;
    private String nombre;
    private String apellidos;
    private String correo;
    private String contrasena;
    private String estado;
    private String foto; // Opcional, puede estar vacío al inicio

    public Usuario() {
        // Requerido por Firebase
    }

    public Usuario(String uid, String nombre, String apellidos, String correo, String contrasena, String estado, String foto) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contrasena = contrasena;
        this.estado = estado;
        this.foto = foto;
    }

    // Getters y setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
