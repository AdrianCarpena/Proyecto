package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottomNav);

        HomePagerAdapter adapter = new HomePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Cambiar pestaña al deslizar
        viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {

                        switch (position){
                            case 0: bottomNav.setSelectedItemId(R.id.nav_home); break;
                            case 1: bottomNav.setSelectedItemId(R.id.nav_cal); break;
                            case 2: bottomNav.setSelectedItemId(R.id.nav_tareas); break;
                            case 3: bottomNav.setSelectedItemId(R.id.nav_examen); break;
                            case 4: bottomNav.setSelectedItemId(R.id.nav_chat); break;
                        }

                    }
                });

        // Cambiar página al pulsar botón
        bottomNav.setOnItemSelectedListener(item -> {

            if(item.getItemId()==R.id.nav_home) viewPager.setCurrentItem(0);
            if(item.getItemId()==R.id.nav_cal) viewPager.setCurrentItem(1);
            if(item.getItemId()==R.id.nav_tareas) viewPager.setCurrentItem(2);
            if(item.getItemId()==R.id.nav_examen) viewPager.setCurrentItem(3);
            if(item.getItemId()==R.id.nav_chat) viewPager.setCurrentItem(4);

            return true;
        });
    }
}