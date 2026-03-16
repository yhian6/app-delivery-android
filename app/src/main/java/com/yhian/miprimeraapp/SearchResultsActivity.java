package com.yhian.miprimeraapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yhian.miprimeraapp.adapter.ProductAdapter;
import com.yhian.miprimeraapp.modelo.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerResultados;
    private ProductAdapter adapter;
    private List<Product> listaProductos = new ArrayList<>();
    private List<Product> listaFiltrada = new ArrayList<>();
    private EditText edtSearchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerResultados = findViewById(R.id.recyclerResultados);
        recyclerResultados.setLayoutManager(new LinearLayoutManager(this));

        edtSearchResults = findViewById(R.id.edtSearchResults);

        adapter = new ProductAdapter(this, listaFiltrada);
        recyclerResultados.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Recuperar el query inicial desde HomeFragment (si lo mandaste con putExtra)
        String query = getIntent().getStringExtra("query");

        cargarProductos(query);

        // Buscar en tiempo real dentro de esta activity
        edtSearchResults.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarProductos(String queryInicial) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Productos");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaProductos.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product p = snap.getValue(Product.class);
                    if (p != null && "ACTIVO".equals(p.getEstado())) {
                        listaProductos.add(p);
                    }
                }

                // Si recibimos un query inicial, aplicamos filtro directamente
                if (queryInicial != null && !queryInicial.isEmpty()) {
                    edtSearchResults.setText(queryInicial);
                    filtrar(queryInicial);
                } else {
                    // si no hay query inicial, mostramos todo
                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaProductos);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchResultsActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrar(String query) {
        listaFiltrada.clear();
        for (Product p : listaProductos) {
            if (p.getNombre() != null && p.getNombre().toLowerCase().contains(query.toLowerCase())) {
                listaFiltrada.add(p);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
