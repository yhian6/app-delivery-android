package com.yhian.miprimeraapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.FirebaseDatabase;
import com.yhian.miprimeraapp.modelo.CartItem;
import com.yhian.miprimeraapp.modelo.CartManager;
import com.yhian.miprimeraapp.modelo.Pedido;
import com.yhian.miprimeraapp.modelo.UbicacionData;
import com.yhian.miprimeraapp.util.AppData;

import java.util.List;

public class PagoContraentregaActivity extends AppCompatActivity {

    private ImageView imgCash;
    private TextView txtTitulo, txtDescripcion;
    private Button btnConfirmar;
    private String metodoPago;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_contraentrega);

        imgCash = findViewById(R.id.imgContraentrega);
        txtDescripcion = findViewById(R.id.txtDescripcionContraentrega);
        btnConfirmar = findViewById(R.id.btnConfirmarContraentrega);
        metodoPago = getIntent().getStringExtra("metodoPago");
        toolbar = findViewById(R.id.toolbarContraentrega);
        setSupportActionBar(toolbar);

        btnConfirmar.setOnClickListener(v -> solicitarGPS());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pago contraentrega");
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(PagoContraentregaActivity.this, MetodoPagoActivity.class);
            startActivity(intent);
            finish();
        });
    }


/*


    private void confirmarPedidoContraentrega() {
        List<CartItem> listaCarrito = CartManager.getInstance().getItems();

        if (listaCarrito.isEmpty()) {
            Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        String idPedido = FirebaseDatabase.getInstance().getReference().child("Pedidos").push().getKey();
        if (idPedido == null) {
            Toast.makeText(this, "Error al generar el ID de pedido", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = 0;
        for (CartItem item : listaCarrito) {
            total += item.getPrecio() * item.getCantidad();
        }

        String userId = AppData.usuarioActual != null ? AppData.usuarioActual.getUid() : "anonimo";
        UbicacionData ubicacionDummy = new UbicacionData(userId, 0, 0, System.currentTimeMillis());

        Pedido pedido = new Pedido(idPedido, listaCarrito, total, System.currentTimeMillis(), AppData.usuarioActual, ubicacionDummy, );
       // pedido.setMetodoPago("Contraentrega");

        FirebaseDatabase.getInstance().getReference("Pedidos")
                .child(idPedido)
                .setValue(pedido)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Pedido confirmado. Recuerda tener el monto exacto.", Toast.LENGTH_LONG).show();
                    CartManager.getInstance().limpiar();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar pedido", Toast.LENGTH_SHORT).show();
                });


    }*/

    private void solicitarGPS() {
        Intent intent = new Intent(this, UbicacionActivity.class);
        intent.putExtra("metodoPago", metodoPago); //  aquí lo pasas
        startActivity(intent);
        finish();
    }


}
