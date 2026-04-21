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
        return messages.get(position).getSenderUsername().equals(myUsername)
                ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageResponse msg = messages.get(position);
        String hora = msg.getSentAt() != null && msg.getSentAt().length() >= 16
                ? msg.getSentAt().substring(11, 16) : "";

        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).tvContent.setText(msg.getContent());
            ((SentViewHolder) holder).tvHora.setText(hora);
        } else {
            ((ReceivedViewHolder) holder).tvContent.setText(msg.getContent());
            ((ReceivedViewHolder) holder).tvSender.setText(msg.getSenderUsername());
            ((ReceivedViewHolder) holder).tvHora.setText(hora);
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

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