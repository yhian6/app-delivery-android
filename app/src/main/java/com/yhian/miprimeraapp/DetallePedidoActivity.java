package com.yhian.miprimeraapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yhian.miprimeraapp.adapter.DetallePedidoAdapter;
import com.yhian.miprimeraapp.modelo.CartItem;
import com.yhian.miprimeraapp.modelo.Pedido;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetallePedidoActivity extends AppCompatActivity {

    private TextView txtFecha, txtTotal, txtMetodoPago;
    private RecyclerView recyclerDetalle;
    private DetallePedidoAdapter adapter;
    private Button btnVerComprobante;
    private Toolbar toolbar;


    // Tracking
    private TextView pasoPendiente, pasoPreparando, pasoEnCamino, pasoEntregado;

    private Pedido pedido;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        // Inicializar vistas
        txtFecha = findViewById(R.id.valorFechaDetalle);
        txtTotal = findViewById(R.id.valorTotalDetalle);
        txtMetodoPago = findViewById(R.id.valorMetodoPago);
        recyclerDetalle = findViewById(R.id.recyclerDetallePedido);
        btnVerComprobante = findViewById(R.id.btnVerComprobante);
        toolbar = findViewById(R.id.tbDetallePedidos);

        pasoPendiente = findViewById(R.id.pasoPendiente);
        pasoPreparando = findViewById(R.id.pasoPreparando);
        pasoEnCamino = findViewById(R.id.pasoEnCamino);
        pasoEntregado = findViewById(R.id.pasoEntregado);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle de mi pedido");

        }

        toolbar.setNavigationOnClickListener(view -> onBackPressed());






        btnVerComprobante.setOnClickListener(v -> {
            File archivo = new File(getExternalFilesDir(null), "Pedido_" + pedido.getTimestamp() + ".pdf");

            if (archivo.exists()) {
                abrirPDF(archivo);
            } else {
                generarPDF(pedido); // lo genera y dentro puedes también abrirlo
                abrirPDF(archivo);
            }
        });


        // Recibir el pedido
         pedido = (Pedido) getIntent().getSerializableExtra("pedido");

        if (pedido != null) {
            // Mostrar fecha
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(pedido.getTimestamp()));
            txtFecha.setText(fecha);

            // Mostrar total
            txtTotal.setText("S/ " + String.format("%.2f", pedido.getTotal()));

            // Mostrar metodo de pago
            if (pedido.getMetodoPago() != null) {
                txtMetodoPago.setText(pedido.getMetodoPago());
            } else {
                txtMetodoPago.setText("No especificado");
            }

            // Mostrar botón si está entregado
            if (pedido.getEstado().equals("entregado")) {
                btnVerComprobante.setVisibility(View.VISIBLE);
            }

            // Mostrar productos
            List<CartItem> lista = pedido.getProductos();
            adapter = new DetallePedidoAdapter(this, lista);
            recyclerDetalle.setLayoutManager(new LinearLayoutManager(this));
            recyclerDetalle.setAdapter(adapter);

            // Mostrar tracking
            actualizarTrackingEstado(pedido.getEstado());
        }
    }

    private void actualizarTrackingEstado(String estado) {
        int verde = getResources().getColor(android.R.color.holo_green_dark);
        int gris = getResources().getColor(android.R.color.darker_gray);

        pasoPendiente.setTextColor(gris);
        pasoPreparando.setTextColor(gris);
        pasoEnCamino.setTextColor(gris);
        pasoEntregado.setTextColor(gris);

        if (estado.equals("pendiente")) {
            pasoPendiente.setTextColor(verde);
        } else if (estado.equals("preparando")) {
            pasoPendiente.setTextColor(verde);
            pasoPreparando.setTextColor(verde);
        } else if (estado.equals("en_camino")) {
            pasoPendiente.setTextColor(verde);
            pasoPreparando.setTextColor(verde);
            pasoEnCamino.setTextColor(verde);
        } else if (estado.equals("entregado")) {
            pasoPendiente.setTextColor(verde);
            pasoPreparando.setTextColor(verde);
            pasoEnCamino.setTextColor(verde);
            pasoEntregado.setTextColor(verde);
        }
    }

    private void abrirPDF(File archivo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(this,
                getPackageName() + ".provider", archivo), "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No hay visor de PDF instalado", Toast.LENGTH_SHORT).show();
        }
    }

    private void generarPDF(Pedido pedido) {
        try {
            File archivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "Pedido_" + pedido.getTimestamp() + ".pdf");

            FileOutputStream fos = new FileOutputStream(archivo);

            PdfDocument documento = new PdfDocument();
            PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
            PdfDocument.Page pagina = documento.startPage(info);

            Canvas canvas = pagina.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(12);

            int y = 25;
            canvas.drawText("COMPROBANTE DE COMPRA", 60, y, paint);
            y += 25;
            canvas.drawText("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(pedido.getTimestamp())), 10, y, paint);
            y += 20;
            canvas.drawText("Total: S/ " + String.format("%.2f", pedido.getTotal()), 10, y, paint);
            y += 30;

            canvas.drawText("Productos:", 10, y, paint);
            y += 20;

            for (CartItem item : pedido.getProductos()) {
                String linea = "- " + item.getProducto().getNombre() +
                        " x" + item.getCantidad() +
                        " = S/ " + String.format("%.2f", item.getCantidad() * item.getPrecio());
                canvas.drawText(linea, 10, y, paint);
                y += 18;
            }

            documento.finishPage(pagina);
            documento.writeTo(fos);
            documento.close();
            fos.close();

            Toast.makeText(this, "PDF guardado en Documentos", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar PDF", Toast.LENGTH_SHORT).show();
        }
    }



}
