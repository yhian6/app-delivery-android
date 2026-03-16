package com.yhian.miprimeraapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.FirebaseDatabase;
import com.yhian.miprimeraapp.modelo.CartItem;
import com.yhian.miprimeraapp.modelo.CartManager;
import com.yhian.miprimeraapp.modelo.Pedido;
import com.yhian.miprimeraapp.modelo.UbicacionData;
import com.yhian.miprimeraapp.util.AppData;
import com.yhian.miprimeraapp.util.UtilsFecha;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView txtMensajeFinal;
    private Location ultimaUbicacion;
    private double total;
    private Toolbar toolbar;

    // Coordenadas de referencia para Cura Mori (puedes ajustar si lo necesitas)
    private static final double CURA_MORI_LAT = -5.326526;
    private static final double CURA_MORI_LNG = -80.664189;
    private static final float RADIO_PERMITIDO_METROS = 1200; // 8 km
    // 3 km de radio
    private String metodoPagoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        metodoPagoSeleccionado = getIntent().getStringExtra("metodoPago");
        if (metodoPagoSeleccionado == null) {
            metodoPagoSeleccionado = "Desconocido"; // Por seguridad
        }

        Button btnUbicacion = findViewById(R.id.btnCompartirUbicacion);
        txtMensajeFinal = findViewById(R.id.txtMensajeFinal);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        toolbar = findViewById(R.id.toolbarUbicacion);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Entrega de pedido");
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(UbicacionActivity.this, MetodoPagoActivity.class);
            startActivity(intent);
            finish();
        });

        btnUbicacion.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                obtenerUbicacion();
            }
        });
    }

    private void obtenerUbicacion() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        if (estaDentroDeCuraMori(location)) {
                            ultimaUbicacion = location;
                            guardarUbicacionEnFirebase(location);
                            txtMensajeFinal.setVisibility(TextView.VISIBLE);
                        } else {
                            Toast.makeText(this, "Lo sentimos, el delivery solo está disponible en Cura Mori.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean estaDentroDeCuraMori(Location ubicacionCliente) {
        float[] distancia = new float[1];
        Location.distanceBetween(
                ubicacionCliente.getLatitude(), ubicacionCliente.getLongitude(),
                CURA_MORI_LAT, CURA_MORI_LNG,
                distancia
        );
        android.util.Log.d("DISTANCIA", "Distancia a Cura Mori: " + distancia[0] + " metros");
        return distancia[0] <= RADIO_PERMITIDO_METROS;
    }

    private void guardarUbicacionEnFirebase(Location location) {
        String userId = AppData.usuarioActual != null ? AppData.usuarioActual.getUid() : "anonimo";

        FirebaseDatabase.getInstance()
                .getReference("UbicacionesClientes")
                .push()
                .setValue(new UbicacionData(userId, location.getLatitude(), location.getLongitude(), System.currentTimeMillis()))
                .addOnSuccessListener(unused -> {
                    txtMensajeFinal.setVisibility(TextView.VISIBLE);
                    Toast.makeText(this, "Ubicación enviada correctamente", Toast.LENGTH_SHORT).show();

                    registrarPedido(location); // Registra pedido con ubicación
                    abrirWhatsAppConMensaje(location);
                    mostrarMensajeAgradecimiento();// Abre WhatsApp
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al enviar ubicación a la base de datos", Toast.LENGTH_SHORT).show();
                });
    }

    private void abrirWhatsAppConMensaje(Location location) {
        String numero = "51931159141"; // Tu número con código de país
        String nombre = AppData.usuarioActual != null ? AppData.usuarioActual.getNombre() : "cliente";

        String mensaje = "Hola, soy *" + nombre + "*, ya realicé el pago. Esta es mi ubicación:\n\n" +
                "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();

        try {
            String url = "https://wa.me/" + numero + "?text=" + Uri.encode(mensaje);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    private void registrarPedido(Location location) {
        List<CartItem> listaCarrito = CartManager.getInstance().getItems();

        if (listaCarrito.isEmpty()) {
            Toast.makeText(this, "No hay productos en el carrito", Toast.LENGTH_SHORT).show();
            return;
        }

        String idPedido = FirebaseDatabase.getInstance().getReference().child("Pedidos").push().getKey();
        if (idPedido == null) {
            Toast.makeText(this, "Error al generar ID de pedido", Toast.LENGTH_SHORT).show();
            return;
        }


        for (CartItem item : listaCarrito) {
            total += item.getPrecio() * item.getCantidad();
        }

        String userId = AppData.usuarioActual != null ? AppData.usuarioActual.getUid() : "anonimo";
        UbicacionData ubicacion = new UbicacionData(userId, location.getLatitude(), location.getLongitude(), System.currentTimeMillis());

        Pedido pedido = new Pedido(idPedido, listaCarrito, total, System.currentTimeMillis(), AppData.usuarioActual, ubicacion, metodoPagoSeleccionado);

        FirebaseDatabase.getInstance()
                .getReference("Pedidos")
                .child(idPedido)
                .setValue(pedido)
                .addOnSuccessListener(unused -> {
                    CartManager.getInstance().limpiar(); // Limpia carrito
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar pedido", Toast.LENGTH_SHORT).show();
                });
    }

    // Permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarMensajeAgradecimiento() {
        // Inflamos el layout personalizado que hicimos
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_resumen_pedido, null);

        // Referencias a los TextView del XML
        CartManager car = new CartManager();
        TextView txtFechaEntrega = dialogView.findViewById(R.id.txtFechaEntrega);
        TextView txtDireccionEntrega = dialogView.findViewById(R.id.txtDireccionEntrega);
        TextView txtPago = dialogView.findViewById(R.id.txtPago);
        TextView txtTotal = dialogView.findViewById(R.id.txtTotal);


        txtFechaEntrega.setText(UtilsFecha.formatearFecha(System.currentTimeMillis()));
        txtDireccionEntrega.setText(obtenerDireccion(
                ultimaUbicacion.getLatitude(),
                ultimaUbicacion.getLongitude()
        ));

        txtPago.setText(metodoPagoSeleccionado);
        txtTotal.setText(String.valueOf(String.format("%.2f", total)));

        // Creamos el diálogo
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false) // para que no lo cierren con back
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Vaciar carrito
                    CartManager.getInstance().limpiar();
                    setResult(RESULT_OK);
                    // Cerrar activity
                    finish();
                })
                .show();
    }

    private String obtenerDireccion(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(lat, lng, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);

                String calle = direccion.getThoroughfare();      // Calle
                String numero = direccion.getSubThoroughfare(); // Número de casa
                String distrito = direccion.getLocality();      // Distrito / ciudad
                String provincia = direccion.getSubAdminArea(); // Provincia
                String region = direccion.getAdminArea();       // Región/departamento
                String pais = direccion.getCountryName();       // País

                // Si no hay calle ni número, mostramos distrito + provincia + país
                if (calle == null && numero == null) {
                    return (distrito != null ? distrito + ", " : "") +
                            (provincia != null ? provincia + ", " : "") +
                            (region != null ? region + ", " : "") +
                            (pais != null ? pais : "");
                }

                // Construir dirección normal si hay calle/número
                StringBuilder direccionFormateada = new StringBuilder();
                if (calle != null) direccionFormateada.append(calle).append(" ");
                if (numero != null) direccionFormateada.append(numero).append(", ");
                if (distrito != null) direccionFormateada.append(distrito).append(", ");
                if (provincia != null) direccionFormateada.append(provincia).append(", ");
                if (region != null) direccionFormateada.append(region).append(", ");
                if (pais != null) direccionFormateada.append(pais);

                return direccionFormateada.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Dirección no disponible";
    }


}
