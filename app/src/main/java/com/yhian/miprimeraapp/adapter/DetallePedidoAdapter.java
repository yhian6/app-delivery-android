package com.yhian.miprimeraapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yhian.miprimeraapp.R;
import com.yhian.miprimeraapp.modelo.CartItem;

import java.util.List;

public class DetallePedidoAdapter extends RecyclerView.Adapter<DetallePedidoAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> lista;

    public DetallePedidoAdapter(Context context, List<CartItem> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public DetallePedidoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detalle_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetallePedidoAdapter.ViewHolder holder, int position) {
        CartItem item = lista.get(position);
        holder.txtNombre.setText(item.getProducto().getNombre());
        holder.txtCantidad.setText("Cantidad: " + item.getCantidad());
        holder.txtSubtotal.setText("Subtotal: S/ " + String.format("%.2f", item.getCantidad() * item.getPrecio()));

        // Cargar imagen desde la URL con Glide
        Glide.with(context)
                .load(item.getProducto().getImagenUrl())
                .placeholder(R.drawable.ic_producto) // imagen por defecto si aún no carga
                .error(R.drawable.ic_producto) // imagen si falla
                .into(holder.imgProducto);

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad, txtSubtotal;
        ImageView imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreDetalle);
            txtCantidad = itemView.findViewById(R.id.txtCantidadDetalle);
            txtSubtotal = itemView.findViewById(R.id.txtSubtotalDetalle);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}

