package com.yhian.miprimeraapp.modelo;

import java.io.Serializable;

public class Product implements Serializable {

    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagenUrl;
    private String categoriaId;
    private String estado; // ACTIVO, INACTIVO, AGOTADO, etc.
    private boolean recomendado; // true = recomendado, false = no recomendado

    public Product() {
        // Constructor vacío requerido por Firebase
        this.estado = "ACTIVO"; // valor por defecto
        this.recomendado = false;
    }

    public Product(String id, String nombre, String descripcion, double precio, String imagenUrl, String categoriaId, String estado, boolean recomendado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.categoriaId = categoriaId;
        this.estado = estado != null ? estado : "ACTIVO"; // si viene null, poner ACTIVO
        this.recomendado = recomendado;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isRecomendado() {
        return recomendado;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setRecomendado(boolean recomendado) {
        this.recomendado = recomendado;
    }
}
