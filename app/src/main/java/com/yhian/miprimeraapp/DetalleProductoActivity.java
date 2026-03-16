package com.yhian.miprimeraapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.yhian.miprimeraapp.modelo.CartManager;
import com.yhian.miprimeraapp.modelo.Product;

public class DetalleProductoActivity extends AppCompatActivity {

    private ImageView imgDetalleProducto;
    private TextView tvNombreDetalle, tvDescripcionDetalle, tvPrecioDetalle, tvCantidadDetalle;
    private Toolbar toolbar;
    private Button btnMenos, btnMas, btnAgregarCarrito;

    private int cantidad = 1; // cantidad inicial
    private Product producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        // Referencias
        imgDetalleProducto = findViewById(R.id.imgDetalleProducto);
        tvNombreDetalle = findViewById(R.id.txtNombreDetalle);
        tvDescripcionDetalle = findViewById(R.id.txtDescripcionDetalle);
        tvPrecioDetalle = findViewById(R.id.txtPrecioDetalle);
        tvCantidadDetalle = findViewById(R.id.txtCantidadDetalle);
        toolbar = findViewById(R.id.tbDetalleProducto);
        setSupportActionBar(toolbar);
        btnMenos = findViewById(R.id.btnDisminuirDetalle);
        btnMas = findViewById(R.id.btnAumentarDetalle);
        btnAgregarCarrito = findViewById(R.id.btnAgregarCarritoDetalle);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle de producto");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvDescripcionDetalle.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        // Recibir producto
        producto = (Product) getIntent().getSerializableExtra("producto");

        if (producto != null) {
            tvNombreDetalle.setText(producto.getNombre());
            tvDescripcionDetalle.setText(producto.getDescripcion());
            tvPrecioDetalle.setText("S/ " + String.format("%.2f", producto.getPrecio()));
            tvCantidadDetalle.setText(String.valueOf(cantidad));

            // Cargar imagen desde URL con Glide
            Glide.with(this)
                    .load(producto.getImagenUrl())
               //     .placeholder(R.drawable.) // pon un drawable de respaldo
                    .into(imgDetalleProducto);
        }

        // Botones de cantidad
        btnMas.setOnClickListener(v -> {
            cantidad++;
            tvCantidadDetalle.setText(String.valueOf(cantidad));
        });

        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                tvCantidadDetalle.setText(String.valueOf(cantidad));
            }
        });

        // Botón agregar al carrito
        btnAgregarCarrito.setOnClickListener(v -> {
            int cantidad = Integer.parseInt(tvCantidadDetalle.getText().toString());
            CartManager.getInstance().agregarAlCarrito(producto, cantidad);
            showLottieDialog(producto.getNombre(), cantidad);
        });

    }

    private void showLottieDialog(String nombreProducto, int cantidad) {
        Dialog dialog = new Dialog(DetalleProductoActivity.this);
        dialog.setContentView(R.layout.dialog_agregado);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtMensaje = dialog.findViewById(R.id.txtMensaje);
        txtMensaje.setText(cantidad + " " + nombreProducto + " agregado");


        dialog.show();

        // Cerrar automáticamente después de 2 segundos
        new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 1500);
    }
}
