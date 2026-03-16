package com.yhian.miprimeraapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yhian.miprimeraapp.modelo.Usuario;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextView regresar;
    EditText nombre, apellidos, correo, contrasena, confContrasena;
    Button btnRegistrar;
    String nom="",ape="", email="", pass="", confPass="";
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        regresar = findViewById(R.id.regresar);
        nombre = findViewById(R.id.edNombre);
        apellidos = findViewById(R.id.edApellidos);
        correo = findViewById(R.id.edCorreo);
        contrasena = findViewById(R.id.edContrasena);
        confContrasena = findViewById(R.id.edCinfirmeContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        firebaseAuth = FirebaseAuth.getInstance();


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validarCampos();
            }
        });


        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


    }

    private void validarCampos() {
        nom = nombre.getText().toString().trim();
        ape = apellidos.getText().toString().trim();
        email = correo.getText().toString().trim();
        pass = contrasena.getText().toString().trim();
        confPass = confContrasena.getText().toString().trim();

        if (nom.isEmpty() || ape.isEmpty() || email.isEmpty() || pass.isEmpty() || confPass.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();

            if (nom.isEmpty()) nombre.setError("Requerido");
            if (ape.isEmpty()) apellidos.setError("Requerido");
            if (email.isEmpty()) correo.setError("Requerido");
            if (pass.isEmpty()) contrasena.setError("Requerido");
            if (confPass.isEmpty()) confContrasena.setError("Requerido");

        } else if (nom.length() < 4) {
            nombre.setError("Debe tener al menos 4 caracteres");
        } else if (ape.length() < 4) {
            apellidos.setError("Debe tener al menos 4 caracteres");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            correo.setError("Correo no válido");
        } else if (pass.length() < 6) {
            contrasena.setError("Contraseña muy corta");
        } else if (!pass.equals(confPass)) {
            confContrasena.setError("Las contraseñas no coinciden");
        } else {

            registrar();
        }
    }

    private void registrar() {
        Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LottieAnimationView anim = dialog.findViewById(R.id.lottieloading);
        anim.playAnimation();
        dialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        guardarUsuario();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Ocurrió un problema, revise los campos", Toast.LENGTH_SHORT).show();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterActivity.this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(RegisterActivity.this, "Contraseña demasiado débil", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(RegisterActivity.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Ocurrió un error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void guardarUsuario() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        Usuario usuario = new Usuario(uid, nom, ape, email, pass, "1", ""); // Foto vacía al inicio

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid).setValue(usuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, Dashboard.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Ocurrió un problema al guardar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }



}


