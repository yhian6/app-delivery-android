package com.yhian.miprimeraapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.yhian.miprimeraapp.DetalleProductoActivity;
import com.yhian.miprimeraapp.R;
import com.yhian.miprimeraapp.modelo.CartManager;
import com.yhian.miprimeraapp.modelo.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> listaProductos;
    private Map<String, Integer> cantidades = new HashMap<>();

    public ProductAdapter(Context context, List<Product> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = listaProductos.get(position);

        holder.txtNombre.setText(p.getNombre());
        holder.txtPrecio.setText("S/ " + String.format("%.2f", p.getPrecio()));
        Glide.with(context).load(p.getImagenUrl()).into(holder.imgProducto);

        // Mostrar la cantidad actual
        int cantidadInicial = cantidades.getOrDefault(p.getId(), 1);
        holder.txtCantidad.setText(String.valueOf(cantidadInicial));

// Botón +
        holder.btnAumentar.setOnClickListener(v -> {
            int cantidadActual = cantidades.getOrDefault(p.getId(), 1);
            int nuevaCantidad = cantidadActual + 1;
            cantidades.put(p.getId(), nuevaCantidad);
            holder.txtCantidad.setText(String.valueOf(nuevaCantidad));
        });

// Botón -
        holder.btnDisminuir.setOnClickListener(v -> {
            int cantidadActual = cantidades.getOrDefault(p.getId(), 1);
            if (cantidadActual > 1) {
                int nuevaCantidad = cantidadActual - 1;
                cantidades.put(p.getId(), nuevaCantidad);
                holder.txtCantidad.setText(String.valueOf(nuevaCantidad));
            }
        });

// Botón agregar al carrito
        holder.btnAgregarCarrito.setOnClickListener(v -> {
            int cantSeleccionada = cantidades.getOrDefault(p.getId(), 1);

            // Aquí usamos el manager
            CartManager.getInstance().agregarAlCarrito(p, cantSeleccionada);

            showLottieDialog(p.getNombre(), cantSeleccionada);

        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleProductoActivity.class);
            intent.putExtra("producto", p);
            context.startActivity(intent);
        });

        holder.btnVerDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleProductoActivity.class);
            intent.putExtra("producto", p);
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDescripcion, txtPrecio, txtCantidad;
        ImageView imgProducto;
        Button btnAumentar, btnDisminuir, btnAgregarCarrito, btnVerDetalle;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProducto);
            txtPrecio = itemView.findViewById(R.id.txtPrecioProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            btnAumentar = itemView.findViewById(R.id.btnAumentar);
            btnDisminuir = itemView.findViewById(R.id.btnDisminuir);
            btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);
            btnVerDetalle = itemView.findViewById(R.id.btnVerDetalle);

        }
    }

    private void showLottieDialog(String nombreProducto, int cantSeleccionada) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_agregado);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtMensaje = dialog.findViewById(R.id.txtMensaje);
        txtMensaje.setText(cantSeleccionada + " " + nombreProducto + " agregado");


        dialog.show();

        // Cerrar automáticamente después de 2 segundos
        new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 1500);
    }

}
