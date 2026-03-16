package com.yhian.miprimeraapp.modelo;

import java.io.Serializable;

public class CartItem implements Serializable {

    private Product producto;
    private int cantidad;

    public CartItem() {
    }

    public CartItem(Product producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Product getProducto() {
        return producto;
    }

    public void setProducto(Product producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // ✅ Método adicional para acceder al precio directamente
    public double getPrecio() {
        return producto != null ? producto.getPrecio() : 0;
    }
}
