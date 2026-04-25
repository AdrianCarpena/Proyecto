package com.example.proyecto;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatDetailActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private List<MessageResponse> messageList = new ArrayList<>();
    private TextInputEditText etMensaje;
    private ApiService api;
    private String token;
    private long chatId;
    private String myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        // Recoger datos del Intent de forma segura
        chatId = getIntent().getLongExtra("chatId", -1L);
        String chatNombre = getIntent().getStringExtra("chatNombre");

        if (chatId == -1L) {
            Toast.makeText(this, "Error: chat no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("Sesion", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("token", "");
        myUsername = prefs.getString("username", "");

        api = RetrofitClient.getClient().create(ApiService.class);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarChatDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(chatNombre != null ? chatNombre : "Chat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // RecyclerView
        rvMessages = findViewById(R.id.rvMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(messageList, myUsername);
        rvMessages.setAdapter(messageAdapter);

        // Input y botón
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
                } else {
                    Toast.makeText(ChatDetailActivity.this,
                            "No se pudieron cargar los mensajes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarMensaje() {
        if (etMensaje == null || etMensaje.getText() == null) return;
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
                        Toast.makeText(ChatDetailActivity.this,
                                "Error al enviar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}