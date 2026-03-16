package com.yhian.miprimeraapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yhian.miprimeraapp.adapter.ProductAdapter;
import com.yhian.miprimeraapp.modelo.Product;

import java.util.ArrayList;
import java.util.List;


public class ProductosFragment extends Fragment {

    private static final String ARG_CATEGORIA = "categoria";
    private String categoriaSeleccionada;

    private RecyclerView recyclerProductos;
    private ProductAdapter adapter;
    private List<Product> productoList;
    private Toolbar toolbar;

    public ProductosFragment() {}

    public static ProductosFragment newInstance(String categoria) {
        ProductosFragment fragment = new ProductosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORIA, categoria);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoriaSeleccionada = getArguments().getString(ARG_CATEGORIA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        toolbar = view.findViewById(R.id.toolbarProductos);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("Productos");
        }

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        recyclerProductos = view.findViewById(R.id.recyclerProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        productoList = new ArrayList<>();
        adapter = new ProductAdapter(getContext(), productoList);
        recyclerProductos.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Productos");
        ref.orderByChild("categoriaId").equalTo(categoriaSeleccionada)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productoList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Product producto = snap.getValue(Product.class);
                            productoList.add(producto);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show();
                    }
                });

        return view;
    }
}