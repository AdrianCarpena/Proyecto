package com.example.proyecto;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Toolbar con botón de volver
        MaterialToolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // flecha atrás
            getSupportActionBar().setTitle("Perfil");
        }
    }

    // Manejar botón de volver
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}