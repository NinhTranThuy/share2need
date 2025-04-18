package com.example.share2need.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.R;
import com.example.share2need.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getText());

        if (message.isSentByMe()) {
            holder.messageText.setBackgroundResource(android.R.color.holo_blue_light);
            holder.messageText.setGravity(Gravity.END);
            holder.userAvatar.setVisibility(View.GONE);
        } else {
            holder.messageText.setBackgroundResource(android.R.color.darker_gray);
            holder.messageText.setGravity(Gravity.START);
            holder.userAvatar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView userAvatar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            userAvatar = itemView.findViewById(R.id.userAvatar);
        }
    }
}