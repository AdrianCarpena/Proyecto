package com.example.proyecto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.LoginRequest;
import com.example.proyecto.model.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etNombre, etPassword;
    Button btnLogin;
    TextView tvRegister;

    SharedPreferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("language", "es");
        super.attachBaseContext(LocaleHelper.setLocale(newBase, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("Sesion", MODE_PRIVATE);

        // Si ya hay sesión iniciada → ir a Home
        if(preferences.getBoolean("sesion_iniciada", false)){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        etNombre = findViewById(R.id.etNombre);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {

            // 1️⃣ Tomar datos
            String username = etNombre.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(LoginActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2️⃣ Crear request
            LoginRequest request = new LoginRequest(username, password);

            // 3️⃣ Crear API
            ApiService api = RetrofitClient.getClient().create(ApiService.class);

            Call<AuthResponse> call = api.login(request);

            // 4️⃣ Ejecutar petición
            call.enqueue(new Callback<AuthResponse>() {

                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                    if(response.isSuccessful() && response.body() != null){

                        String token = response.body().getToken();

                        // 5️⃣ Guardar sesión
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("sesion_iniciada", true);
                        editor.putString("token", token);
                        editor.apply();

                        Toast.makeText(LoginActivity.this,
                                "Login correcto",
                                Toast.LENGTH_SHORT).show();

                        // 6️⃣ Ir a Home
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

                    }else{

                        Toast.makeText(LoginActivity.this,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {

                    Toast.makeText(LoginActivity.this,
                            "Error conexión servidor",
                            Toast.LENGTH_SHORT).show();
                }
            });

        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}