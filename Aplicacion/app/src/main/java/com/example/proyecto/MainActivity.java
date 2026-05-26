package com.example.proyecto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button mvLogin, mvRegister;
    private ApiService apiService;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs =
                newBase.getSharedPreferences("Settings", MODE_PRIVATE);

        String language =
                prefs.getString("language", "es");

        super.attachBaseContext(
                LocaleHelper.setLocale(newBase, language)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs =
                getSharedPreferences("Settings", MODE_PRIVATE);

        boolean darkMode =
                prefs.getBoolean("dark_mode", false);

        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        mvLogin = findViewById(R.id.mvLogin);
        mvRegister = findViewById(R.id.mvRegister);

        // Inicializar ApiService correctamente
        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        SharedPreferences sessionPrefs =
                getSharedPreferences("Sesion", MODE_PRIVATE);

        // Si ya hay sesión iniciada
        if (sessionPrefs.getBoolean("sesion_iniciada", false)) {

            String token =
                    sessionPrefs.getString("token", "");

            // Última fecha de reubicación
            String ultimaFecha =
                    prefs.getString(
                            "ultima_reubicacion",
                            ""
                    );

            // Fecha actual compatible API 24
            String hoy =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(new Date());

            // Ejecutar solo una vez al día
            if (!hoy.equals(ultimaFecha)) {

                apiService.reubicarSesionesNoHechas(
                                "Bearer " + token
                        )
                        .enqueue(new Callback<Void>() {

                            @Override
                            public void onResponse(
                                    Call<Void> call,
                                    Response<Void> response
                            ) {

                                if (response.isSuccessful()) {

                                    prefs.edit()
                                            .putString(
                                                    "ultima_reubicacion",
                                                    hoy
                                            )
                                            .apply();
                                }

                                abrirHome();
                            }

                            @Override
                            public void onFailure(
                                    Call<Void> call,
                                    Throwable t
                            ) {
                                abrirHome();
                            }
                        });

            } else {
                abrirHome();
            }
        }

        mvLogin.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                LoginActivity.class
                        )
                )
        );

        mvRegister.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                RegisterActivity.class
                        )
                )
        );
    }

    private void abrirHome() {

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);

        String openFragment = getIntent().getStringExtra("openFragment");

        if (openFragment != null) {
            intent.putExtra("openFragment", openFragment);
        }

        startActivity(intent);
        finish();
    }
}