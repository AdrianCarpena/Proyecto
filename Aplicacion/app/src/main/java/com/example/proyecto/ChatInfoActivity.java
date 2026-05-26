package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.ChatJoinCodeResponse;
import com.example.proyecto.model.ChatMemberResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatInfoActivity extends AppCompatActivity {

    private RecyclerView rvMembers;
    private TextView tvJoinCode;

    private ApiService api;
    private String token;
    private Long chatId;

    private final List<ChatMemberResponse> members = new ArrayList<>();
    private MemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        chatId = getIntent().getLongExtra("chatId", -1);

        SharedPreferences prefs = getSharedPreferences("Sesion", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("token", "");

        api = RetrofitClient.getClient().create(ApiService.class);

        // Toolbar con flecha de regreso
        MaterialToolbar toolbar = findViewById(R.id.toolbarInfo);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Información");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        tvJoinCode = findViewById(R.id.tvJoinCode);

        rvMembers = findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MemberAdapter(members, this::mostrarDialogoExpulsar);
        rvMembers.setAdapter(adapter);

        cargarJoinCode();
        cargarMiembros();

        MaterialButton btnLeaveChat = findViewById(R.id.btnLeaveChat);
        btnLeaveChat.setOnClickListener(v -> mostrarDialogoAbandonarChat());
    }

    private void cargarJoinCode() {
        api.getJoinCode(token, chatId)
                .enqueue(new Callback<ChatJoinCodeResponse>() {
                    @Override
                    public void onResponse(Call<ChatJoinCodeResponse> call,
                                           Response<ChatJoinCodeResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            tvJoinCode.setText("Código de unión: " + response.body().getJoinCode());
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatJoinCodeResponse> call,
                                          Throwable t) {
                    }
                });
    }

    private void cargarMiembros() {
        api.getChatMembers(token, chatId)
                .enqueue(new Callback<List<ChatMemberResponse>>() {
                    @Override
                    public void onResponse(Call<List<ChatMemberResponse>> call,
                                           Response<List<ChatMemberResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            members.clear();
                            members.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatMemberResponse>> call,
                                          Throwable t) {
                    }
                });
    }

    private void mostrarDialogoExpulsar(ChatMemberResponse member) {
        new AlertDialog.Builder(this)
                .setTitle(member.getUsername())
                .setMessage("¿Expulsar del grupo?")
                .setPositiveButton("Expulsar", (dialog, which) -> {

                    api.removeMember(token, chatId, member.getUserId())
                            .enqueue(new Callback<Void>() {

                                @Override
                                public void onResponse(Call<Void> call,
                                                       Response<Void> response) {

                                    if (response.isSuccessful()) {
                                        Toast.makeText(ChatInfoActivity.this,
                                                "Miembro expulsado",
                                                Toast.LENGTH_SHORT).show();

                                        cargarMiembros();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call,
                                                      Throwable t) {

                                    Toast.makeText(ChatInfoActivity.this,
                                            "Error",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoAbandonarChat() {
        new AlertDialog.Builder(this)
                .setTitle("Abandonar chat")
                .setMessage("¿Seguro que quieres abandonar este chat?")
                .setPositiveButton("Abandonar", (dialog, which) -> abandonarChat())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abandonarChat() {
        api.leaveChat(token, chatId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ChatInfoActivity.this,
                                    "Has abandonado el chat",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ChatInfoActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("openFragment", "chats");
                            startActivity(intent);

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ChatInfoActivity.this,
                                    "No se pudo abandonar el chat",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ChatInfoActivity.this,
                                "Error de conexión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}