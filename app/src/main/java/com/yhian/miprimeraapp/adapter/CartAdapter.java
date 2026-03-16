package com.yhian.miprimeraapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yhian.miprimeraapp.R;
import com.yhian.miprimeraapp.modelo.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> listaCarrito;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartUpdated(); // Para actualizar el total
    }

    public CartAdapter(Context context, List<CartItem> listaCarrito, OnCartChangeListener listener) {
        this.context = context;
        this.listaCarrito = listaCarrito;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
        return new CartViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = listaCarrito.get(position);

        holder.txtNombre.setText(item.getProducto().getNombre());
        holder.txtPrecio.setText("S/ " + String.format("%.2f", item.getPrecio()));
        holder.txtCantidad.setText(String.valueOf(item.getCantidad()));
        double subtotal = item.getPrecio() * item.getCantidad();
        holder.txtSubtotal.setText("Subtotal: S/ " + String.format("%.2f", subtotal));

        Glide.with(context).load(item.getProducto().getImagenUrl()).into(holder.imgProducto);

        holder.btnAumentar.setOnClickListener(v -> {
            item.setCantidad(item.getCantidad() + 1);
            notifyItemChanged(position);
            listener.onCartUpdated();
        });

        holder.btnDisminuir.setOnClickListener(v -> {
            if (item.getCantidad() > 1) {
                item.setCantidad(item.getCantidad() - 1);
                notifyItemChanged(position);
                listener.onCartUpdated();
            } else {
                Toast.makeText(context, "Mínimo 1 unidad", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_eliminar_producto, null);

            new com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
                    .setView(dialogView)
                    .setPositiveButton("Sí", (dialog, which) -> {
                        listaCarrito.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listaCarrito.size());
                        listener.onCartUpdated();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        });




    }

    @Override
    public int getItemCount() {
        return listaCarrito.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPrecio, txtCantidad, txtSubtotal;
        ImageView imgProducto;
        ImageButton btnAumentar, btnDisminuir;
        ImageButton btnEliminar;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCarrito);
            txtPrecio = itemView.findViewById(R.id.txtPrecioCarrito);
            txtCantidad = itemView.findViewById(R.id.txtCantidadCarrito);
            txtSubtotal = itemView.findViewById(R.id.txtSubtotalCarrito);
            imgProducto = itemView.findViewById(R.id.imgCarritoProducto);
            btnAumentar = itemView.findViewById(R.id.btnMasCarrito);
            btnDisminuir = itemView.findViewById(R.id.btnMenosCarrito);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProducto);

        }
    }
}
