package com.yhian.miprimeraapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PagoPlinActivity extends AppCompatActivity {

    private Button btnRealicePago, btnEnviarWhatsapp;
    private String metodoPago;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_plin);

        btnRealicePago = findViewById(R.id.btnRealicePago);
        btnEnviarWhatsapp = findViewById(R.id.btnEnviarWhatsapp);
        metodoPago = getIntent().getStringExtra("metodoPago");
        toolbar = findViewById(R.id.toolbarPlin);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pago con Plin");
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(PagoPlinActivity.this, MetodoPagoActivity.class);
            startActivity(intent);
            finish();
        });

        btnEnviarWhatsapp.setOnClickListener(v -> abrirWhatsapp());

        btnRealicePago.setOnClickListener(v -> {
            solicitarGPS();
        });
    }

    private void mostrarMensajeAgradecimiento() {
        new AlertDialog.Builder(this)
                .setTitle("Gracias por tu pago")
                .setMessage("En unos minutos confirmaremos que se realizó correctamente. Mantente atento.")
                .setPositiveButton("Continuar", (dialog, which) -> {
                    // Aquí pedimos la ubicación
                    solicitarGPS();
                })
                .setCancelable(false)
                .show();
    }

    private void solicitarGPS() {
        // Aquí iría la lógica para obtener ubicación (puedo ayudarte con eso)
        Intent intent = new Intent(this, UbicacionActivity.class); // actividad donde capturas la ubicación
        intent.putExtra("metodoPago", metodoPago); //  aquí lo pasas
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