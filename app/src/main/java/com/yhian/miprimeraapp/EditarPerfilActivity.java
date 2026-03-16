package com.yhian.miprimeraapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imgPerfilEditar;
    private EditText etNombreEditar, etApellidosEditar;
    private Button btnGuardarCambios;
    private Toolbar toolbar;

    private FirebaseAuth auth;
    private DatabaseReference usuariosRef;

    private Uri imagenUri; // Nueva foto seleccionada
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        imgPerfilEditar = findViewById(R.id.imgPerfilEditar);
        etNombreEditar = findViewById(R.id.etNombreEditar);
        etApellidosEditar = findViewById(R.id.etApellidosEditar);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        toolbar = findViewById(R.id.toolbarEditarPerfil);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        uid = auth.getCurrentUser().getUid();

        cargarDatosUsuario();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Editar Perfil");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        imgPerfilEditar.setOnClickListener(v -> abrirGaleria());
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatosUsuario() {
        usuariosRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String apellidos = snapshot.child("apellidos").getValue(String.class);
                    String fotoNombre = snapshot.child("foto").getValue(String.class);

                    etNombreEditar.setText(nombre);
                    etApellidosEditar.setText(apellidos);

                    if(fotoNombre != null && !fotoNombre.isEmpty()){
                        File file = new File(getFilesDir(), fotoNombre);
                        if(file.exists()){
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imgPerfilEditar.setImageBitmap(bitmap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001 && resultCode == RESULT_OK && data != null){
            imagenUri = data.getData();
            imgPerfilEditar.setImageURI(imagenUri);
        }
    }

    private void guardarCambios() {
        String nombre = etNombreEditar.getText().toString().trim();
        String apellidos = etApellidosEditar.getText().toString().trim();

        if(nombre.isEmpty() || apellidos.isEmpty()){
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("apellidos", apellidos);

        if(imagenUri != null){
            try {
                InputStream is = getContentResolver().openInputStream(imagenUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                String fotoNombre = "perfil_" + uid + ".jpg";
                File file = new File(getFilesDir(), fotoNombre);
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.close();

                updates.put("foto", fotoNombre);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar la imagen localmente", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        usuariosRef.child(uid).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
