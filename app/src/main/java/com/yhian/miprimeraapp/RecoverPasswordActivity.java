package com.yhian.miprimeraapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecoverPasswordActivity extends AppCompatActivity {

    EditText recuContra;
    Button continuar;
    String recu;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recover_password);

        recuContra = findViewById(R.id.edRecuperarContrasena);
        continuar = findViewById(R.id.btnContinuar);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RecoverPasswordActivity.this);
        progressDialog.setTitle("Espere por favor...");

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCampo();
            }
        });

    }

    private void validarCampo() {
        recu = recuContra.getText().toString().trim();
        if (recu.isEmpty()) {
            recuContra.setError("Ingrese correo");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(recu).matches()) {
            recuContra.setError("Ingrese correo válido");
        } else {
            recuperarContrasena();
        }

    }

    private void recuperarContrasena() {

        progressDialog.setMessage("Enviando correo...");
        progressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios");

        // Verificar si el correo existe en la base de datos
        ref.orderByChild("correo").equalTo(recu)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            DataSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                // El correo existe, ahora enviamos el email de recuperación
                                progressDialog.dismiss();
                                firebaseAuth.sendPasswordResetEmail(recu)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Dialog dialog = new Dialog(RecoverPasswordActivity.this);
                                                dialog.setContentView(R.layout.dialog_exito);
                                                dialog.setCanceledOnTouchOutside(false);
                                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                                                LottieAnimationView anim = dialog.findViewById(R.id.lottieAnimacion);
                                                anim.playAnimation();
                                                dialog.show();

                                                new android.os.Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.dismiss();

                                                    }
                                                }, 3000);
                                               // Toast.makeText(RecoverPasswordActivity.this, "Correo enviado. Revise su bandeja", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RecoverPasswordActivity.this, "No se pudo enviar el correo", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RecoverPasswordActivity.this, "El correo no está registrado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Exception e = task.getException();
                            Toast.makeText(RecoverPasswordActivity.this, "Error: " + (e != null ? e.getMessage() : "Error desconocido"), Toast.LENGTH_LONG).show();                        }
                    }
                });
    }

}