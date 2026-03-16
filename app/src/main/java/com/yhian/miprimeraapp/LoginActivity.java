package com.yhian.miprimeraapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yhian.miprimeraapp.modelo.Usuario;
import com.yhian.miprimeraapp.util.AppData;

public class LoginActivity extends AppCompatActivity {

    TextView registro;
    EditText usuario, password;
    Button ingresar;
    TextView olvidasteContra;
    String usu, pass;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        registro = findViewById(R.id.registrate);
        usuario = findViewById(R.id.edUsuario);
        password = findViewById(R.id.edPassword);
        olvidasteContra = findViewById(R.id.tvOlvidasteTuContrasena);
        ingresar = findViewById(R.id.btnIngresar);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);


        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });

        olvidasteContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RecoverPasswordActivity.class));

            }
        });

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });
    }

    private void validarDatos() {
        usu = usuario.getText().toString().trim();
        pass = password.getText().toString().trim();

        if (usu.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Ingrese datos", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(usu).matches()) {
            Toast.makeText(this, "Ingrese correo valido", Toast.LENGTH_SHORT).show();

        } else {
            logearUsuario();
        }
    }

    private void logearUsuario() {
        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        LottieAnimationView anim = dialog.findViewById(R.id.lottieloading);
        anim.playAnimation();
        dialog.show();

        firebaseAuth.signInWithEmailAndPassword(usu, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Obtener UID
                        String uid = firebaseAuth.getCurrentUser().getUid();

                        // Ir a Firebase y buscar datos del usuario
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                dialog.dismiss();

                                Usuario usuario = snapshot.getValue(Usuario.class);
                                if (usuario != null) {
                                    AppData.usuarioActual = usuario;

                                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Error: datos de usuario no encontrados", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Usuario no existe, verifique datos", Toast.LENGTH_SHORT).show();
                    }
                });


    }

}