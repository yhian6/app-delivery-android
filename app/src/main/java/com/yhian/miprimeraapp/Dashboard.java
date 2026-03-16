package com.yhian.miprimeraapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.yhian.miprimeraapp.modelo.CartManager;

public class Dashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Fragmento inicial
        loadFragment(new HomeFragment());

        // Mostrar badge del carrito al iniciar
        actualizarBadgeCarrito();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    fragment = new HomeFragment();
                } else if (id == R.id.nav_orders) {
                    fragment = new CarritoFragment();
                } else if (id == R.id.nav_bookmark) {
                    fragment = new MisPedidosFragment();
                } else if (id == R.id.nav_profile) {
                    fragment = new SoporteFragment();
                }

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                    return true;
                }

                return false;
            }
        });

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public void actualizarBadgeCarrito() {
        int cantidad = CartManager.getInstance().getCantidadProductosDiferentes();

        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_orders);

        if (cantidad > 0) {
            badge.setVisible(true);
            badge.setNumber(cantidad);
        } else {
            badge.clearNumber();
            badge.setVisible(false);
        }
    }
}