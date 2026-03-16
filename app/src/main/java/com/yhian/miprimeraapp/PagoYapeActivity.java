package com.yhian.miprimeraapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.yhian.miprimeraapp.modelo.CartManager;
import com.yhian.miprimeraapp.modelo.Pedido;

public class PagoYapeActivity extends AppCompatActivity {

    private Button btnRealicePago, btnEnviarWhatsapp;
    private String metodoPago;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_yape);

        btnRealicePago = findViewById(R.id.btnRealicePago);
        btnEnviarWhatsapp = findViewById(R.id.btnEnviarWhatsapp);
        toolbar = findViewById(R.id.toolbarYape);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pago con Yape");
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(PagoYapeActivity.this, MetodoPagoActivity.class);
            startActivity(intent);
            finish();
        });


        btnEnviarWhatsapp.setOnClickListener(v -> abrirWhatsapp());

        btnRealicePago.setOnClickListener(v -> {
            solicitarGPS();
        });

        metodoPago = getIntent().getStringExtra("metodoPago");
    }

    private void solicitarGPS() {
        Intent intent = new Intent(this, UbicacionActivity.class);
        intent.putExtra("metodoPago", metodoPago); // 👈 aquí lo pasas
        startActivity(intent);
        finish();
    }

    private void abrirWhatsapp() {
        String numeroTelefono = "51931159141"; // tu número con código de país (sin +)
        String mensaje = "Hola, ya realicé el pago por Yape. Aquí está la captura:";
        Uri uri = Uri.parse("https://wa.me/" + numeroTelefono + "?text=" + Uri.encode(mensaje));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
