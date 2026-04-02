package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    Switch switchDarkMode;
    Spinner spinnerLanguage;
    MaterialButton btnLogout, btnDeleteAccount;

    boolean firstSelection = true; // 🔥 para evitar ejecución inicial del spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Preferencias
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        String language = prefs.getString("language", "es");

        // Aplicar modo oscuro
        if(darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Aplicar idioma
        LocaleHelper.setLocale(this, language);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.topBarProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.perfil));

        // Views
        switchDarkMode = findViewById(R.id.switchDarkMode);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        // Switch estado inicial
        switchDarkMode.setChecked(darkMode);

        // 🔥 Switch Dark Mode
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

        // 🔥 Spinner idioma
        String[] languages = {"Español", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // Selección actual
        spinnerLanguage.setSelection(language.equals("en") ? 1 : 0);

        spinnerLanguage.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {

                if(firstSelection){
                    firstSelection = false;
                    return; // 🚫 evita ejecución automática
                }

                String selectedLang = position == 0 ? "es" : "en";

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("language", selectedLang);
                editor.apply();

                // 🔁 Reiniciar app
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        // 🔐 Cerrar sesión
        btnLogout.setOnClickListener(v -> {
            prefs.edit().remove("sesion_iniciada").apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        // ❌ Borrar cuenta
        btnDeleteAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Cuenta borrada (simulado)", Toast.LENGTH_SHORT).show();
            prefs.edit().clear().apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    // Flecha atrás
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}