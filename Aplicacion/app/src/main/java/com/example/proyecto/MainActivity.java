package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button mvLogin, mvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mvLogin = findViewById(R.id.mvLogin);
        mvRegister = findViewById(R.id.mvRegister);

        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);

        if(preferences.getBoolean("sesion_iniciada", false)){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        mvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        mvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

    }
}