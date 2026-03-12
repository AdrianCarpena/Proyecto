package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnCerrarSesion;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(v -> {
            preferences.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}