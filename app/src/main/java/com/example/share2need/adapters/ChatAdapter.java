package com.example.share2need.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.R;
import com.example.share2need.models.ChatSummary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatSummary> chatSummaryList;
    private OnChatClickListener listener;

    public ChatAdapter(List<ChatSummary> chatSummaryList, OnChatClickListener listener) {
        this.chatSummaryList = chatSummaryList;
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
        ChatSummary chatSummary = chatSummaryList.get(position);
        holder.userName.setText(chatSummary.getUserName());
        holder.lastMessage.setText(chatSummary.getLastMessage());
        holder.timestamp.setText(formatTimestamp(chatSummary.getTimestamp()));

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chatSummary));
    }

    private String formatTimestamp(long timestampMillis) {
        Calendar now = Calendar.getInstance();
        Calendar messageTime = Calendar.getInstance();
        messageTime.setTimeInMillis(timestampMillis);

        long diffMillis = now.getTimeInMillis() - messageTime.getTimeInMillis();
        long diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (diffDays < 7) {
            // Hiển thị "Thứ Hai", "Thứ Ba", ...
            String[] days = {"Chủ nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};
            String weekday = days[messageTime.get(Calendar.DAY_OF_WEEK) - 1];
            return weekday + ", " + timeFormat.format(messageTime.getTime());
        } else {
            SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
            return fullFormat.format(messageTime.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatSummaryList.size();
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
        void onChatClick(ChatSummary chatSummary);
    }
}
