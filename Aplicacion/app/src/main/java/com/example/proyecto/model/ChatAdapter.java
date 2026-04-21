package com.example.proyecto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.model.ChatResponse;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public interface OnChatClickListener {
        void onClick(ChatResponse chat);
    }

    private final List<ChatResponse> chats;
    private final OnChatClickListener listener;

    public ChatAdapter(List<ChatResponse> chats, OnChatClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatResponse chat = chats.get(position);
        holder.tvNombre.setText(chat.getNombre());
        holder.tvMiembros.setText(chat.getMemberCount() + " miembros");

        // Inicial del grupo como avatar
        String inicial = chat.getNombre().length() > 0
                ? String.valueOf(chat.getNombre().charAt(0)).toUpperCase()
                : "?";
        holder.tvAvatar.setText(inicial);

        holder.itemView.setOnClickListener(v -> listener.onClick(chat));
    }

    @Override
    public int getItemCount() { return chats.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMiembros, tvAvatar;
        ViewHolder(View view) {
            super(view);
            tvNombre = view.findViewById(R.id.tvChatNombre);
            tvMiembros = view.findViewById(R.id.tvMiembros);
            tvAvatar = view.findViewById(R.id.tvAvatar);
        }
    }
}