package com.yhian.miprimeraapp.modelo;

import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {
    private String id;
    private List<CartItem> productos;
    private double total;
    private long timestamp;
    private Usuario usuario;
    private UbicacionData ubicacion;
    private String estado = "pendiente";
    private String metodoPago; //  Nuevo campo

    public Pedido() {} // Requerido por Firebase

    public Pedido(String id, List<CartItem> productos, double total,
                  long timestamp, Usuario usuario, UbicacionData ubicacion,
                  String metodoPago) {
        this.id = id;
        this.productos = productos;
        this.total = total;
        this.timestamp = timestamp;
        this.usuario = usuario;
        this.ubicacion = ubicacion;
        this.metodoPago = metodoPago; // ✅ Asignar método de pago
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<CartItem> getProductos() { return productos; }
    public void setProductos(List<CartItem> productos) { this.productos = productos; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public UbicacionData getUbicacion() { return ubicacion; }
    public void setUbicacion(UbicacionData ubicacion) { this.ubicacion = ubicacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}
