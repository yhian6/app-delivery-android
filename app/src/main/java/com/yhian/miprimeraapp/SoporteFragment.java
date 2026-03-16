package com.yhian.miprimeraapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.*;
import com.yhian.miprimeraapp.modelo.Pedido;

public class SoporteFragment extends Fragment {

    private Button btnWhatsapp, btnCorreo, btnTelefono;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_soporte, container, false);

        btnWhatsapp = view.findViewById(R.id.btnWhatsapp);
        btnCorreo = view.findViewById(R.id.btnCorreo);
        btnTelefono = view.findViewById(R.id.btnTelefono);

        // WhatsApp
        btnWhatsapp.setOnClickListener(v -> {
            /*
            String phone = "51931159141"; // <-- aquí tu número real con código de país
            String message = "Hola, necesito ayuda con mi pedido.";
            Uri uri = Uri.parse("https://wa.me/" + phone + "?text=" + Uri.encode(message));
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);*/
        });

        // Correo
        btnCorreo.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("yhiancarloschiroqueramos@gmail.com"));
            email.putExtra(Intent.EXTRA_SUBJECT, "Soporte App Cevichería");
            email.putExtra(Intent.EXTRA_TEXT, "Hola, necesito ayuda con...");
            startActivity(Intent.createChooser(email, "Enviar correo"));
        });

        // Teléfono
        btnTelefono.setOnClickListener(v -> {
           /* Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:931159141")); // <-- aquí tu número real
            startActivity(call);*/
        });

        return view;
    }
}
