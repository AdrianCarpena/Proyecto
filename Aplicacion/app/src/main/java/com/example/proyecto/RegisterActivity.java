package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.LoginRequest;
import com.example.proyecto.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etNuevoNombre, etNuevaPassword, etRepetirPassword;
    Button btnRegistrar;

    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNuevoNombre = findViewById(R.id.etNuevoNombre);
        tvLogin = findViewById(R.id.tvLogin);
        etNuevaPassword = findViewById(R.id.etNuevaPassword);
        etRepetirPassword = findViewById(R.id.etRepetirPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> {

            String nombre = etNuevoNombre.getText().toString().trim();
            String password = etNuevaPassword.getText().toString().trim();
            String repetir = etRepetirPassword.getText().toString().trim();

            if(nombre.isEmpty() || password.isEmpty() || repetir.isEmpty()){
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if(nombre.length() < 3 || nombre.length() > 20){
                Toast.makeText(this, "El nombre debe tener al menos 3 caracteres y no mas de 20", Toast.LENGTH_LONG).show();
                return;
            }

            if(password.length() < 6){
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                return;
            }

            if(!password.equals(repetir)){
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                return;
            }

            // 🔹 Crear request
            LoginRequest request = new LoginRequest(nombre, password);

            // 🔹 Llamar a la API
            ApiService api = RetrofitClient.getClient().create(ApiService.class);

            Call<Void> call = api.register(request);

            call.enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if(response.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }else{
                        Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });

        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}