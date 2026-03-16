package com.yhian.miprimeraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombre, tvCorreo;
    private ShapeableImageView imgPerfil;
    private TextView btnEditarPerfil, btnCambiarContrasena, btnCerrarSesion;
    private Toolbar toolbar;

    private FirebaseAuth auth;
    private DatabaseReference usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        imgPerfil = findViewById(R.id.imgPerfil);

        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        toolbar = findViewById(R.id.toolbarPerfil);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Perfil");
        }


        auth = FirebaseAuth.getInstance();
        usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios");



        cargarDatosUsuario();

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);
            startActivityForResult(intent, 2001); // requestCode arbitrario
        });

        btnCambiarContrasena.setOnClickListener(v -> {
           mostrarDialogCambiarContrasena();
        });

        btnCerrarSesion.setOnClickListener(v -> {
           mostrarDialogCerrarSesion();
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }

    private void mostrarDialogCambiarContrasena() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.dialog_cambiar_contrasena, null));
        builder.setPositiveButton("Sí", (dialog, which) -> {
            auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail())
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Revisa tu correo para cambiar la contraseña", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void mostrarDialogCerrarSesion() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.dialog_cerrar_sesion, null));
        builder.setPositiveButton("Sí, salir", (dialog, which) -> {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void cargarDatosUsuario() {
        String uid = auth.getCurrentUser().getUid();



        // Usamos addValueEventListener para escuchar cambios en tiempo real
        usuariosRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String apellidos = snapshot.child("apellidos").getValue(String.class);
                    String correo = snapshot.child("correo").getValue(String.class);
                    String fotoNombre = snapshot.child("foto").getValue(String.class);

                    tvNombre.setText(nombre + " " + apellidos);
                    tvCorreo.setText(correo);



                    // Actualizamos la foto en tiempo real
                    if(fotoNombre != null && !fotoNombre.isEmpty()) {
                        File file = new File(getFilesDir(), fotoNombre);

                        if(file.exists()){
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            if(bitmap != null){
                                imgPerfil.setImageBitmap(bitmap);
                            } else {
                                imgPerfil.setImageResource(R.drawable.user); // placeholder
                            }
                        } else {
                            imgPerfil.setImageResource(R.drawable.user); // placeholder
                        }
                    } else {
                        imgPerfil.setImageResource(R.drawable.user); // placeholder
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PerfilActivity.this, "Error al cargar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2001 && resultCode == RESULT_OK){
            // Si hubo cambios, recargamos datos del usuario
            cargarDatosUsuario();
        }
    }


}