package com.example.proyecto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    BottomNavigationView bottomNav;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("language", "es");
        super.attachBaseContext(LocaleHelper.setLocale(newBase, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 🔹 Aplicar modo oscuro persistente
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        String language = prefs.getString("language", "es");

        if(darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 🔹 Aplicar idioma persistente
        LocaleHelper.setLocale(this, language);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🔹 Toolbar
        MaterialToolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // quitar texto “Proyecto”

        // 🔹 Views
        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottomNav);

        // 🔹 Adapter
        HomePagerAdapter adapter = new HomePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 🔹 Sincronizar swipe con BottomNavigation
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if(position == 0) bottomNav.setSelectedItemId(R.id.nav_home);
                else if(position == 1) bottomNav.setSelectedItemId(R.id.nav_cal);
                else if(position == 2) bottomNav.setSelectedItemId(R.id.nav_tareas);
                else if(position == 3) bottomNav.setSelectedItemId(R.id.nav_examen);
                else if(position == 4) bottomNav.setSelectedItemId(R.id.nav_chat);
            }
        });

        // 🔹 Cambiar página al pulsar BottomNavigation

    }

    // 🔹 Toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    // 🔹 Detectar click de perfil
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_profile){
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}