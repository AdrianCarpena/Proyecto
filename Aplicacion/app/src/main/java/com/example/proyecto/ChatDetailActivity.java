package com.example.proyecto;

import android.content.Intent;
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
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class ChatDetailActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private List<MessageResponse> messageList = new ArrayList<>();
    private TextInputEditText etMensaje;
    private ApiService api;
    private String token;
    private long chatId;
    private String myUsername;

    private StompClient stompClient;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Gson gson = new Gson();

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

        // Volver atrás
        toolbar.setNavigationOnClickListener(v -> finish());

        // Abrir perfil del grupo al pulsar toolbar
        toolbar.setOnClickListener(v -> {

            Intent intent = new Intent(ChatDetailActivity.this,
                    ChatInfoActivity.class);

            intent.putExtra("chatId", chatId);
            intent.putExtra("chatNombre", chatNombre);

            startActivityForResult(intent, 100);
        });

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
        conectarWebSocket();
    }

    private void cargarMensajes() {

        api.getMessages(token, chatId)
                .enqueue(new Callback<List<MessageResponse>>() {

                    @Override
                    public void onResponse(Call<List<MessageResponse>> call,
                                           Response<List<MessageResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            messageList.clear();

                            messageList.addAll(response.body());

                            messageAdapter.notifyDataSetChanged();

                            if (!messageList.isEmpty()) {

                                rvMessages.scrollToPosition(messageList.size() - 1);
                            }

                        } else {

                            Toast.makeText(ChatDetailActivity.this,
                                    "No se pudieron cargar los mensajes",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MessageResponse>> call,
                                          Throwable t) {

                        Toast.makeText(ChatDetailActivity.this,
                                "Error de conexión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void enviarMensaje() {

        if (etMensaje == null || etMensaje.getText() == null) return;

        String content = etMensaje.getText().toString().trim();

        if (content.isEmpty()) return;

        api.sendMessage(token, chatId,
                        new SendMessageRequest(content))
                .enqueue(new Callback<MessageResponse>() {

                    @Override
                    public void onResponse(Call<MessageResponse> call,
                                           Response<MessageResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {
                            etMensaje.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call,
                                          Throwable t) {

                        Toast.makeText(ChatDetailActivity.this,
                                "Error al enviar",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            finish();
        }
    }

    private void conectarWebSocket() {

        // Emulador Android: 10.0.2.2 apunta al localhost del PC
        String url = RetrofitClient.WS_URL;

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);

        List<StompHeader> headers = Arrays.asList(
                new StompHeader("Authorization", token)
        );

        compositeDisposable.add(
                stompClient.lifecycle()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(lifecycleEvent -> {
                            switch (lifecycleEvent.getType()) {
                                case OPENED:
                                    Toast.makeText(this, "Chat conectado", Toast.LENGTH_SHORT).show();
                                    break;

                                case ERROR:
                                    Toast.makeText(this, "Error WebSocket", Toast.LENGTH_SHORT).show();
                                    break;

                                case CLOSED:
                                    break;
                            }
                        }, throwable -> {
                            Toast.makeText(this, "Error al conectar WebSocket", Toast.LENGTH_SHORT).show();
                        })
        );

        stompClient.connect(headers);

        compositeDisposable.add(
                stompClient.topic("/topic/chats/" + chatId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(topicMessage -> {

                            MessageResponse message =
                                    gson.fromJson(topicMessage.getPayload(), MessageResponse.class);

                            messageList.add(message);

                            messageAdapter.notifyItemInserted(messageList.size() - 1);

                            rvMessages.scrollToPosition(messageList.size() - 1);

                        }, throwable -> {
                            Toast.makeText(this, "Error recibiendo mensajes", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void enviarMensajeWebSocket() {

        if (etMensaje == null || etMensaje.getText() == null) return;

        String content = etMensaje.getText().toString().trim();

        if (content.isEmpty()) return;

        if (stompClient == null || !stompClient.isConnected()) {
            Toast.makeText(this, "Chat no conectado", Toast.LENGTH_SHORT).show();
            return;
        }

        SendMessageRequest request = new SendMessageRequest(content);

        String json = gson.toJson(request);

        compositeDisposable.add(
                stompClient.send("/app/chats/" + chatId + "/send", json)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            etMensaje.setText("");
                        }, throwable -> {
                            Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }

        if (stompClient != null) {
            stompClient.disconnect();
        }
    }
}