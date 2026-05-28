package com.example.proyecto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.model.MessageResponse;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;

    private final List<MessageResponse> messages;
    private final String myUsername;

    public MessageAdapter(List<MessageResponse> messages, String myUsername) {
        this.messages = messages;
        this.myUsername = myUsername;
    }

    @Override
    public int getItemViewType(int position) {

        MessageResponse msg = messages.get(position);

        String sender = msg.getSenderUsername();

        // Debug para Logcat
        android.util.Log.d(
                "CHAT_DEBUG",
                "sender=" + sender + " myUsername=" + myUsername
        );

        // Protección contra null
        if (sender == null || myUsername == null) {
            return TYPE_RECEIVED;
        }

        // Comparación segura
        sender = sender.trim().toLowerCase();
        String me = myUsername.trim().toLowerCase();

        return sender.equals(me)
                ? TYPE_SENT
                : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        if (viewType == TYPE_SENT) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);

            return new SentViewHolder(view);

        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);

            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {

        MessageResponse msg = messages.get(position);

        String hora = "";

        if (msg.getSentAt() != null
                && msg.getSentAt().length() >= 16) {

            hora = msg.getSentAt().substring(11, 16);
        }

        String content = msg.getContent() != null
                ? msg.getContent()
                : "";

        if (holder instanceof SentViewHolder) {

            SentViewHolder h = (SentViewHolder) holder;

            h.tvContent.setText(content);
            h.tvHora.setText(hora);

        } else {

            ReceivedViewHolder h = (ReceivedViewHolder) holder;

            h.tvContent.setText(content);

            h.tvSender.setText(
                    msg.getSenderUsername() != null
                            ? msg.getSenderUsername()
                            : ""
            );

            h.tvHora.setText(hora);
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent, tvHora;

        SentViewHolder(View view) {
            super(view);

            tvContent = view.findViewById(R.id.tvContent);
            tvHora = view.findViewById(R.id.tvHora);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent, tvHora, tvSender;

        ReceivedViewHolder(View view) {
            super(view);

            tvContent = view.findViewById(R.id.tvContent);
            tvHora = view.findViewById(R.id.tvHora);
            tvSender = view.findViewById(R.id.tvSender);
        }
    }
}