package com.yhian.miprimeraapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MetodoPagoActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_pago);

        findViewById(R.id.cardYape).setOnClickListener(v -> seleccionarMetodo("Yape"));
        findViewById(R.id.cardPlin).setOnClickListener(v -> seleccionarMetodo("Plin"));
        findViewById(R.id.cardContraEntrega).setOnClickListener(v -> seleccionarMetodo("Contraentrega"));
        toolbar = findViewById(R.id.toolbarmePa);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }

    private void seleccionarMetodo(String metodo) {

        Intent intent;

        switch (metodo) {
            case "Yape":
                intent = new Intent(this, PagoYapeActivity.class);
                intent.putExtra("metodoPago", "Yape"); //
                startActivity(intent);
                finish();
                break;
            case "Plin":
                intent = new Intent(this, PagoPlinActivity.class);
                intent.putExtra("metodoPago", "Plin");
                startActivity(intent);
                finish();
                break;
            case "Contraentrega":
                intent = new Intent(this, PagoContraentregaActivity.class);
                intent.putExtra("metodoPago", "Contraentrega");
                startActivity(intent);
                finish();
               break;
            default:
                return;
        }
    }
}
