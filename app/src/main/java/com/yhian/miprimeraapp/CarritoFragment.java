package com.yhian.miprimeraapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yhian.miprimeraapp.adapter.CartAdapter;
import com.yhian.miprimeraapp.modelo.CartItem;
import com.yhian.miprimeraapp.modelo.CartManager;
import com.yhian.miprimeraapp.modelo.Pedido;
import com.yhian.miprimeraapp.modelo.UbicacionData;
import com.yhian.miprimeraapp.util.AppData;

import java.util.List;

public class CarritoFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtTotal;
    private CartAdapter adapter;
    private List<CartItem> listaCarrito;
    private Button btnRealizarPedido;
    private View layoutVacio;
    private double total;


    public CarritoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);

        recyclerView = view.findViewById(R.id.recyclerCarrito);
        txtTotal = view.findViewById(R.id.txtTotalCarrito);
        btnRealizarPedido = view.findViewById(R.id.btnRealizarPedido);
        layoutVacio = view.findViewById(R.id.layoutVacio);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaCarrito = CartManager.getInstance().getItems();

        adapter = new CartAdapter(getContext(), listaCarrito, this::actualizarTotal);
        recyclerView.setAdapter(adapter);

        actualizarTotal();
        btnRealizarPedido.setOnClickListener(v -> confirmarPedido());
        return view;


    }

    private void actualizarTotal() {
        double total = 0;
        for (CartItem item : listaCarrito) {
            total += item.getPrecio() * item.getCantidad();
        }

        if (listaCarrito.isEmpty()) {
            layoutVacio.setVisibility(View.VISIBLE);
            txtTotal.setVisibility(View.GONE);
            btnRealizarPedido.setVisibility(View.GONE);
        } else {
            layoutVacio.setVisibility(View.GONE);
            txtTotal.setVisibility(View.VISIBLE);
            btnRealizarPedido.setVisibility(View.VISIBLE);
            txtTotal.setText("Total: S/ " + String.format("%.2f", total));
        }
    }

    private void confirmarPedido() {
        if (listaCarrito.isEmpty()) {
            Toast.makeText(getContext(), "No hay productos en el carrito", Toast.LENGTH_SHORT).show();
            return;
        }


        // Inflar el layout personalizado
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_confirmar_pedido, null);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setView(view)
                .setPositiveButton("Sí", (dialog, which) -> {
                    //AQUI HAY QUE MODIFICAR//

            //*        Intent intent = new Intent(getContext(), VerificarZonaActivity.class);
            //*        intent.putExtra("listaCarrito", (java.io.Serializable) listaCarrito);
             //*       intent.putExtra("total", total);
          //*          intent.putExtra("usuario", AppData.usuarioActual);

            //*        startActivity(intent);
          registrarPedido();

                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Verifica si el carrito fue limpiado
        listaCarrito = CartManager.getInstance().getItems();
        adapter.notifyDataSetChanged();
        actualizarTotal();
    }

    private void registrarPedido() {
        List<CartItem> listaCarrito = CartManager.getInstance().getItems();

        String idPedido = FirebaseDatabase.getInstance().getReference().child("Pedidos").push().getKey();


        for (CartItem item : listaCarrito) {
            total += item.getPrecio() * item.getCantidad();
        }


        Pedido pedido = new Pedido(idPedido, listaCarrito, total, System.currentTimeMillis(), AppData.usuarioActual,null , "desconocido");

        FirebaseDatabase.getInstance()
                .getReference("Pedidos")
                .child(idPedido)
                .setValue(pedido)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Se registró el pedido", Toast.LENGTH_SHORT).show();
                    CartManager.getInstance().limpiar(); // Limpia carrito
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al registrar pedido", Toast.LENGTH_SHORT).show();
                });
    }




}
