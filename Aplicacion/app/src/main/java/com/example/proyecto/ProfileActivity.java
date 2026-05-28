package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.ChangePasswordRequest;
import com.example.proyecto.model.ChangeUsernameRequest;
import com.example.proyecto.model.UserProfileResponse;
import com.example.proyecto.model.UserProfileWithTokenResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private Spinner spinnerLanguage;

    private MaterialButton btnChangeUsername;
    private MaterialButton btnChangePassword;
    private MaterialButton btnLogout;
    private MaterialButton btnDeleteAccount;

    private TextView tvUsername;

    private boolean firstSelection = true;

    private ApiService apiService;
    private SharedPreferences prefs;
    private SharedPreferences sessionPrefs;

    private String token;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs =
                newBase.getSharedPreferences(
                        "Settings",
                        MODE_PRIVATE
                );

        String language =
                prefs.getString("language", "es");

        super.attachBaseContext(
                LocaleHelper.setLocale(
                        newBase,
                        language
                )
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prefs =
                getSharedPreferences(
                        "Settings",
                        MODE_PRIVATE
                );

        sessionPrefs =
                getSharedPreferences(
                        "Sesion",
                        MODE_PRIVATE
                );

        boolean darkMode =
                prefs.getBoolean(
                        "dark_mode",
                        false
                );

        String language =
                prefs.getString(
                        "language",
                        "es"
                );

        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        LocaleHelper.setLocale(this, language);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        token =
                sessionPrefs.getString(
                        "token",
                        ""
                );

        MaterialToolbar toolbar =
                findViewById(
                        R.id.topBarProfile
                );

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);

            getSupportActionBar()
                    .setTitle(
                            getString(R.string.perfil)
                    );
        }

        tvUsername =
                findViewById(R.id.tvUsername);

        switchDarkMode =
                findViewById(
                        R.id.switchDarkMode
                );

        spinnerLanguage =
                findViewById(
                        R.id.spinnerLanguage
                );

        btnChangeUsername =
                findViewById(
                        R.id.btnChangeUsername
                );

        btnChangePassword =
                findViewById(
                        R.id.btnChangePassword
                );

        btnLogout =
                findViewById(
                        R.id.btnLogout
                );

        btnDeleteAccount =
                findViewById(
                        R.id.btnDeleteAccount
                );

        cargarPerfil();

        // DARK MODE
        switchDarkMode.setChecked(darkMode);

        switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    prefs.edit()
                            .putBoolean(
                                    "dark_mode",
                                    isChecked
                            )
                            .apply();

                    if (isChecked) {
                        AppCompatDelegate
                                .setDefaultNightMode(
                                        AppCompatDelegate.MODE_NIGHT_YES
                                );
                    } else {
                        AppCompatDelegate
                                .setDefaultNightMode(
                                        AppCompatDelegate.MODE_NIGHT_NO
                                );
                    }
                }
        );

        // IDIOMA
        String[] languages = {
                "Español",
                "English"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        languages
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerLanguage.setAdapter(adapter);

        spinnerLanguage.setSelection(
                language.equals("en") ? 1 : 0
        );

        spinnerLanguage.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (firstSelection) {
                            firstSelection = false;
                            return;
                        }

                        String selectedLang =
                                position == 0
                                        ? "es"
                                        : "en";

                        if (!selectedLang.equals(language)) {

                            prefs.edit()
                                    .putString(
                                            "language",
                                            selectedLang
                                    )
                                    .apply();

                            Intent intent =
                                    new Intent(
                                            ProfileActivity.this,
                                            MainActivity.class
                                    );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {

                    }
                }
        );

        btnChangeUsername.setOnClickListener(
                v -> mostrarDialogoCambiarUsername()
        );

        btnChangePassword.setOnClickListener(
                v -> mostrarDialogoCambiarPassword()
        );

        btnLogout.setOnClickListener(v -> logout());

        btnDeleteAccount.setOnClickListener(
                v -> confirmarBorrarCuenta()
        );
    }

    private void cargarPerfil() {

        apiService.getMyProfile(
                        "Bearer " + token
                )
                .enqueue(
                        new Callback<UserProfileResponse>() {

                            @Override
                            public void onResponse(
                                    Call<UserProfileResponse> call,
                                    Response<UserProfileResponse> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    tvUsername.setText(
                                            response.body()
                                                    .getUsername()
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<UserProfileResponse> call,
                                    Throwable t
                            ) {

                            }
                        }
                );
    }

    private void mostrarDialogoCambiarUsername() {

        EditText etUsername =
                new EditText(this);

        etUsername.setHint(
                getString(R.string.nuevo_username)
        );

        new AlertDialog.Builder(this)
                .setTitle(
                        getString(R.string.cambiar_nombre)
                )
                .setView(etUsername)
                .setPositiveButton(
                        "Guardar",
                        (dialog, which) -> {

                            String nuevoUsername =
                                    etUsername.getText()
                                            .toString()
                                            .trim();

                            if (nuevoUsername.isEmpty()) {
                                Toast.makeText(
                                        this,
                                        "Campo vacío",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            ChangeUsernameRequest request =
                                    new ChangeUsernameRequest(
                                            nuevoUsername
                                    );

                            apiService.changeUsername(
                                            "Bearer " + token,
                                            request
                                    )
                                    .enqueue(
                                            new Callback<UserProfileWithTokenResponse>() {

                                                @Override
                                                public void onResponse(
                                                        Call<UserProfileWithTokenResponse> call,
                                                        Response<UserProfileWithTokenResponse> response
                                                ) {

                                                    if (response.isSuccessful()
                                                            && response.body() != null) {

                                                        token =
                                                                response.body()
                                                                        .getToken();

                                                        sessionPrefs.edit()
                                                                .putString(
                                                                        "token",
                                                                        token
                                                                )
                                                                .apply();

                                                        tvUsername.setText(
                                                                response.body()
                                                                        .getUsername()
                                                        );

                                                        Toast.makeText(
                                                                ProfileActivity.this,
                                                                "Nombre actualizado",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                    } else {

                                                        Toast.makeText(
                                                                ProfileActivity.this,
                                                                "No se pudo cambiar",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(
                                                        Call<UserProfileWithTokenResponse> call,
                                                        Throwable t
                                                ) {

                                                    Toast.makeText(
                                                            ProfileActivity.this,
                                                            "Error de conexión",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }
                                    );
                        }
                )
                .setNegativeButton(
                        getString(R.string.cancelar),
                        null
                )
                .show();
    }

    private void mostrarDialogoCambiarPassword() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        EditText etCurrent =
                new EditText(this);

        etCurrent.setHint(
                getString(R.string.contraseña_actual)
        );

        etCurrent.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        EditText etNew =
                new EditText(this);

        etNew.setHint(
                getString(R.string.nueva_contraseña)
        );

        etNew.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        EditText etConfirm =
                new EditText(this);

        etConfirm.setHint(
                getString(R.string.confirmar_contraseña)
        );

        etConfirm.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        layout.addView(etCurrent);
        layout.addView(etNew);
        layout.addView(etConfirm);

        new AlertDialog.Builder(this)
                .setTitle(
                        getString(R.string.cambiar_contraseña)
                )
                .setView(layout)
                .setPositiveButton(
                        getString(R.string.guardar),
                        (dialog, which) -> {

                            String current =
                                    etCurrent.getText()
                                            .toString();

                            String nueva =
                                    etNew.getText()
                                            .toString();

                            String confirm =
                                    etConfirm.getText()
                                            .toString();

                            if (current.isEmpty()
                                    || nueva.isEmpty()
                                    || confirm.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        getString(R.string.complete_todos_campos),
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            if (!nueva.equals(confirm)) {

                                Toast.makeText(
                                        this,
                                        getString(R.string.contraseñas_no_coinciden),
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            ChangePasswordRequest request =
                                    new ChangePasswordRequest(
                                            current,
                                            nueva
                                    );

                            apiService.changePassword(
                                            "Bearer " + token,
                                            request
                                    )
                                    .enqueue(
                                            new Callback<Void>() {

                                                @Override
                                                public void onResponse(
                                                        Call<Void> call,
                                                        Response<Void> response
                                                ) {

                                                    if (response.isSuccessful()) {

                                                        Toast.makeText(
                                                                ProfileActivity.this,
                                                                "Contraseña actualizada",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                    } else {

                                                        Toast.makeText(
                                                                ProfileActivity.this,
                                                                "Contraseña actual incorrecta",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(
                                                        Call<Void> call,
                                                        Throwable t
                                                ) {

                                                    Toast.makeText(
                                                            ProfileActivity.this,
                                                            "Error de conexión",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }
                                    );
                        }
                )
                .setNegativeButton(
                        "Cancelar",
                        null
                )
                .show();
    }

    private void confirmarBorrarCuenta() {

        new AlertDialog.Builder(this)
                .setTitle(
                        getString(R.string.borrar_cuenta)
                )
                .setMessage(
                        getString(R.string.seguro_borrar_cuenta)
                )
                .setPositiveButton(
                        getString(R.string.borrar),
                        (dialog, which) -> borrarCuenta()
                )
                .setNegativeButton(
                        getString(R.string.cancelar),
                        null
                )
                .show();
    }

    private void borrarCuenta() {

        apiService.deleteMyAccount(
                        "Bearer " + token
                )
                .enqueue(
                        new Callback<Void>() {

                            @Override
                            public void onResponse(
                                    Call<Void> call,
                                    Response<Void> response
                            ) {

                                if (response.isSuccessful()) {

                                    sessionPrefs.edit()
                                            .clear()
                                            .apply();

                                    Intent intent =
                                            new Intent(
                                                    ProfileActivity.this,
                                                    LoginActivity.class
                                            );

                                    intent.setFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );

                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<Void> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        ProfileActivity.this,
                                        "Error de conexión",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    private void logout() {

        sessionPrefs.edit()
                .clear()
                .apply();

        Intent intent =
                new Intent(
                        ProfileActivity.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}