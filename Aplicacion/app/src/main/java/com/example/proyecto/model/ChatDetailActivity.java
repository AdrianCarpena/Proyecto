package com.example.proyecto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.MessageResponse;
import com.example.proyecto.model.SendMessageRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatDetailActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private List<MessageResponse> messageList = new ArrayList<>();
    private EditText etMensaje;
    private ApiService api;
    private String token;
    private Long chatId;
    private String myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        chatId = getIntent().getLongExtra("chatId", -1);
        String chatNombre = getIntent().getStringExtra("chatNombre");

        SharedPreferences prefs = getSharedPreferences("Sesion", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("token", "");
        myUsername = prefs.getString("username", "");

        api = RetrofitClient.getClient().create(ApiService.class);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarChatDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(chatNombre);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // RecyclerView
        rvMessages = findViewById(R.id.rvMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(messageList, myUsername);
        rvMessages.setAdapter(messageAdapter);

        etMensaje = findViewById(R.id.etMensaje);
        MaterialButton btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> enviarMensaje());

        cargarMensajes();
    }

    private void cargarMensajes() {
        api.getMessages(token, chatId).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    messageAdapter.notifyDataSetChanged();
                    if (!messageList.isEmpty()) {
                        rvMessages.scrollToPosition(messageList.size() - 1);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarMensaje() {
        String content = etMensaje.getText().toString().trim();
        if (content.isEmpty()) return;

        api.sendMessage(token, chatId, new SendMessageRequest(content))
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            etMensaje.setText("");
                            messageList.add(response.body());
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            rvMessages.scrollToPosition(messageList.size() - 1);
                        }
                    }
                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Toast.makeText(ChatDetailActivity.this, "Error al enviar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}