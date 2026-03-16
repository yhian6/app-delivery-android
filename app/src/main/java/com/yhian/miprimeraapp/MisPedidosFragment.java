package com.yhian.miprimeraapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yhian.miprimeraapp.adapter.PedidoAdapter;
import com.yhian.miprimeraapp.modelo.Pedido;

import java.util.ArrayList;
import java.util.List;


public class MisPedidosFragment extends Fragment {

    private RecyclerView recyclerView;
    private PedidoAdapter adapter;
    private List<Pedido> listaPedidos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_pedidos, container, false);

        recyclerView = view.findViewById(R.id.recyclerPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PedidoAdapter(getContext(),listaPedidos);
        recyclerView.setAdapter(adapter);

        cargarPedidosDesdeFirebase();

        return view;
    }

    private void cargarPedidosDesdeFirebase() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Pedidos");
        Query query = dbRef.orderByChild("usuario/uid").equalTo(uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPedidos.clear();
                for (DataSnapshot pedidoSnap : snapshot.getChildren()) {
                    Pedido pedido = pedidoSnap.getValue(Pedido.class);
                    listaPedidos.add(pedido);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
