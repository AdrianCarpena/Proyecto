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
import com.example.proyecto.model.UserProfileResponse;

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

        String language = prefs.getString("language", "es");

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

        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        SharedPreferences sessionPrefs =
                getSharedPreferences("Sesion", MODE_PRIVATE);

        if (sessionPrefs.getBoolean("sesion_iniciada", false)) {

            String token = sessionPrefs.getString("token", "");

            validarTokenYEntrar(token, prefs, sessionPrefs);
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

    private void validarTokenYEntrar(String token,
                                     SharedPreferences settingsPrefs,
                                     SharedPreferences sessionPrefs) {

        apiService.getMyProfile("Bearer " + token)
                .enqueue(new Callback<UserProfileResponse>() {

                    @Override
                    public void onResponse(Call<UserProfileResponse> call,
                                           Response<UserProfileResponse> response) {

                        if (response.isSuccessful()) {
                            comprobarReubicacionYEntrar(token, settingsPrefs);
                        } else {
                            limpiarSesion(sessionPrefs);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileResponse> call,
                                          Throwable t) {

                        limpiarSesion(sessionPrefs);
                    }
                });
    }

    private void comprobarReubicacionYEntrar(String token,
                                             SharedPreferences prefs) {

        apiService.reubicarSesionesNoHechas("Bearer " + token)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call,
                                           Response<Void> response) {

                        abrirHome();
                    }

                    @Override
                    public void onFailure(Call<Void> call,
                                          Throwable t) {

                        abrirHome();
                    }
                });
    }

    private void limpiarSesion(SharedPreferences sessionPrefs) {

        sessionPrefs.edit()
                .clear()
                .apply();
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