package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    Switch switchDarkMode;
    MaterialButton btnChangeLanguage, btnLogout, btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Aplicar modo oscuro persistente
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        if(darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.topBarProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Perfil");

        // Switch para modo oscuro
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(darkMode);

        // Botones
        btnChangeLanguage = findViewById(R.id.btnChangeLanguage);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        // Switch Dark Mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Cambiar idioma a inglés
        btnChangeLanguage.setOnClickListener(v -> {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            android.content.res.Configuration config = getResources().getConfiguration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            Toast.makeText(this, "Idioma cambiado a Inglés", Toast.LENGTH_SHORT).show();
        });

        // Cerrar sesión
        btnLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
            preferences.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Borrar cuenta
        btnDeleteAccount.setOnClickListener(v -> {
            // Aquí llamarías a tu API para eliminar cuenta
            Toast.makeText(this, "Cuenta borrada (simulado)", Toast.LENGTH_SHORT).show();
            SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
            preferences.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // Flecha para volver
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}