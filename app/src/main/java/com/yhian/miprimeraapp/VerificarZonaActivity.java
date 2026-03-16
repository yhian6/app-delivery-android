package com.yhian.miprimeraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class VerificarZonaActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final double CURA_MORI_LAT = -5.326526;
    private static final double CURA_MORI_LNG = -80.664189;
    private static final float RADIO_PERMITIDO_METROS = 1200; // Aumentamos a 4km

    private FusedLocationProviderClient fusedLocationClient;
    private TextView txtEstadoZona;
    private Button btnVerificar, btnContinuar;

    private boolean estaEnZonaValida = false;
    private Location ubicacionCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_zona);

        txtEstadoZona = findViewById(R.id.txtEstadoZona);
        btnVerificar = findViewById(R.id.btnVerificarUbicacion);
        btnContinuar = findViewById(R.id.btnContinuarPago);
        btnContinuar.setEnabled(false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnVerificar.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                verificarUbicacion();
            }
        });

        btnContinuar.setOnClickListener(v -> {
            if (estaEnZonaValida && ubicacionCliente != null) {
                // Ir a MetodoPagoActivity y pasar la ubicación si deseas
                Intent intent = new Intent(this, MetodoPagoActivity.class);
                intent.putExtra("lat", ubicacionCliente.getLatitude());
                intent.putExtra("lng", ubicacionCliente.getLongitude());
                startActivity(intent);
                finish();
            }
        });
    }

    private void verificarUbicacion() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        ubicacionCliente = location;
                        float[] distancia = new float[1];
                        Location.distanceBetween(
                                location.getLatitude(), location.getLongitude(),
                                CURA_MORI_LAT, CURA_MORI_LNG,
                                distancia
                        );

                        txtEstadoZona.setText("Distancia: " + distancia[0] + " metros");

                        if (distancia[0] <= RADIO_PERMITIDO_METROS) {
                            txtEstadoZona.append(". Estás dentro de la zona de reparto (Cura Mori)");
                            estaEnZonaValida = true;
                            btnContinuar.setEnabled(true);
                        } else {
                            txtEstadoZona.append("Lo sentimos, solo repartimos en Cura Mori");
                            estaEnZonaValida = false;
                            btnContinuar.setEnabled(false);
                        }
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación, debe prender su GPS", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Manejo de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            verificarUbicacion();
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }
}
