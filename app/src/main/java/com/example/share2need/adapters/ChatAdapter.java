package com.example.share2need.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.R;
import com.example.share2need.models.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Chat> chatList;
    private OnChatClickListener listener;

    public ChatAdapter(List<Chat> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.userName.setText(chat.getUserName());
        holder.lastMessage.setText(chat.getLastMessage());
        holder.timestamp.setText(chat.getTimestamp());

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastMessage, timestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }
}
