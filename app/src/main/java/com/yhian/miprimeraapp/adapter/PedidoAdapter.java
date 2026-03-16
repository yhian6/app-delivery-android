package com.yhian.miprimeraapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yhian.miprimeraapp.DetallePedidoActivity;
import com.yhian.miprimeraapp.R;
import com.yhian.miprimeraapp.modelo.Pedido;
import com.yhian.miprimeraapp.util.UtilsFecha;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> listaPedidos;
    private Context context;

    public PedidoAdapter(Context context, List<Pedido> listaPedidos) {
        this.context = context;
        this.listaPedidos = listaPedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);
        holder.txtTotal.setText(
                "Total: S/ " + String.format(Locale.US, "%.2f", pedido.getTotal())
        );

        holder.txtFecha.setText(UtilsFecha.formatearFecha(pedido.getTimestamp()));

        // Mostrar el estado
        String estado = pedido.getEstado();
        holder.txtEstado.setText("Estado: " + mapEstado(estado));
        holder.txtEstado.setTextColor(getColorSegunEstado(estado));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePedidoActivity.class);
            intent.putExtra("pedido", pedido);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (pedido.getEstado().equals("pendiente")) {
                new AlertDialog.Builder(context)
                        .setTitle("Anular pedido")
                        .setMessage("¿Seguro que deseas anular este pedido?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Pedidos")
                                    .child(pedido.getId());
                            ref.child("estado").setValue("anulado")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Pedido anulado", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(context, "Ya no puedes anular este pedido", Toast.LENGTH_SHORT).show();
            }
            return true;
        });


    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView txtTotal, txtFecha, txtEstado;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTotal = itemView.findViewById(R.id.txtTotalPedido);
            txtFecha = itemView.findViewById(R.id.txtFechaPedido);
            txtEstado = itemView.findViewById(R.id.txtEstadoPedido);
        }
    }

    // Opcional: Traducir estado si deseas mostrarlo bonito
    private String mapEstado(String estado) {
        switch (estado) {
            case "pendiente": return "Pendiente";
            case "preparando": return "Preparando";
            case "en_camino": return "En camino";
            case "entregado": return "Entregado";
            case "anulado": return "Anulado";
            default: return "Desconocido";
        }
    }


    // Color distinto para cada estado
    private int getColorSegunEstado(String estado) {
        switch (estado) {
            case "pendiente":
                return context.getResources().getColor(android.R.color.holo_orange_dark);
            case "preparando":
                return context.getResources().getColor(android.R.color.holo_blue_dark);
            case "en_camino":
                return context.getResources().getColor(android.R.color.holo_green_dark);
            case "entregado":
                return context.getResources().getColor(android.R.color.holo_green_light);
            case "anulado":
                return context.getResources().getColor(android.R.color.holo_red_dark);

            default:
                return context.getResources().getColor(android.R.color.darker_gray);
        }
    }
}

