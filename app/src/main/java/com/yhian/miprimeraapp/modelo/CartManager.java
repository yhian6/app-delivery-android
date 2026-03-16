package com.yhian.miprimeraapp.modelo;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> carrito;
    private double total;
    public CartManager() {
        carrito = new ArrayList<>();
    }



    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void agregarAlCarrito(Product producto, int cantidad) {
        // Verificar si ya existe y solo sumar
        for (CartItem item : carrito) {
            if (item.getProducto().getId().equals(producto.getId())) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        carrito.add(new CartItem(producto, cantidad));
    }

    public List<CartItem> getItems() {
        return carrito;
    }

    public void limpiar() {
        carrito.clear();
    }

    public double calcularTotal() {

        for (CartItem item : carrito) {
            total += item.getProducto().getPrecio() * item.getCantidad();
        }
        return total;
    }

    public int getCantidadProductosDiferentes() {
        return carrito.size();

    }

    public double getTotal() {
        return total;
    }
}
