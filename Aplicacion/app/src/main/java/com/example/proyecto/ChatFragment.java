package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.api.ApiService;
import com.example.proyecto.api.RetrofitClient;
import com.example.proyecto.model.ChatResponse;
import com.example.proyecto.model.CreateChatRequest;
import com.example.proyecto.model.JoinChatRequest;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView rvChats;
    private ChatAdapter chatAdapter;
    private List<ChatResponse> chatList = new ArrayList<>();
    private ApiService api;
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("Sesion", android.content.Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("token", "");

        api = RetrofitClient.getClient().create(ApiService.class);

        rvChats = view.findViewById(R.id.rvChats);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(chatList, chat -> {
            // Proteger contra chatId null antes de abrir el detalle
            if (chat.getId() == null) {
                Toast.makeText(getContext(), "Chat no disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
            intent.putExtra("chatId", chat.getId().longValue());
            intent.putExtra("chatNombre", chat.getNombre());
            startActivity(intent);
        });
        rvChats.setAdapter(chatAdapter);

        view.findViewById(R.id.btnCrearChat).setOnClickListener(v -> mostrarDialogoCrear());
        view.findViewById(R.id.btnUnirseChat).setOnClickListener(v -> mostrarDialogoUnirse());

        cargarChats();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarChats();
    }

    private void cargarChats() {
        api.getChats(token).enqueue(new Callback<List<ChatResponse>>() {
            @Override
            public void onResponse(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatList.clear();
                    chatList.addAll(response.body());
                    chatAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<ChatResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar chats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoCrear() {
        EditText etNombre = new EditText(getContext());
        etNombre.setHint("Nombre del grupo");

        new AlertDialog.Builder(requireContext())
                .setTitle("Crear grupo")
                .setView(etNombre)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    if (nombre.isEmpty()) {
                        Toast.makeText(getContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    api.createChat(token, new CreateChatRequest(nombre))
                            .enqueue(new Callback<ChatResponse>() {
                                @Override
                                public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        String code = response.body().getJoinCode();
                                        new AlertDialog.Builder(requireContext())
                                                .setTitle("Grupo creado")
                                                .setMessage("Código para unirse:\n\n" + code)
                                                .setPositiveButton("OK", null)
                                                .show();
                                        cargarChats();
                                    }
                                }
                                @Override
                                public void onFailure(Call<ChatResponse> call, Throwable t) {
                                    Toast.makeText(getContext(), "Error al crear grupo", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoUnirse() {
        EditText etCodigo = new EditText(getContext());
        etCodigo.setHint("Código del grupo");

        new AlertDialog.Builder(requireContext())
                .setTitle("Unirse a grupo")
                .setView(etCodigo)
                .setPositiveButton("Unirse", (dialog, which) -> {
                    String codigo = etCodigo.getText().toString().trim();
                    if (codigo.isEmpty()) {
                        Toast.makeText(getContext(), "Escribe el código", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    api.joinChat(token, new JoinChatRequest(codigo))
                            .enqueue(new Callback<ChatResponse>() {
                                @Override
                                public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Te has unido al grupo", Toast.LENGTH_SHORT).show();
                                        cargarChats();
                                    } else {
                                        Toast.makeText(getContext(), "Código incorrecto o ya eres miembro", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<ChatResponse> call, Throwable t) {
                                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}