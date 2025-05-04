package com.example.share2need.adapters;

import static android.webkit.URLUtil.isValidUrl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.firebase.UserRepository;
import com.example.share2need.models.ChatSummary;
import com.example.share2need.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private List<ChatSummary> chatSummaryList;
    private OnChatClickListener listener;

    public ChatListAdapter(List<ChatSummary> chatSummaryList, OnChatClickListener listener) {
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

        int adapterPosition = holder.getAdapterPosition();
        UserRepository userRepository = new UserRepository();
        userRepository.getUserInfo(chatSummary.getReceiverUserID(), new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (holder.getAdapterPosition() != adapterPosition) return;
                String imageUrl = (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) ?
                        user.getProfileImage() : null;
                if (holder.userAvatar != null) {
                    if (imageUrl != null) {
                        if (isValidUrl(imageUrl)) {
                            // 🔗 Ảnh là URL
                            Glide.with(holder.itemView.getContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.broken_image_24px)
                                    .into(holder.userAvatar);
                        } else {
                            // 🧬 Ảnh là Base64
                            try {
                                byte[] decodedBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                holder.userAvatar.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                Log.e("Base64Error", "Lỗi decode ảnh base64", e);
                                holder.userAvatar.setImageResource(R.drawable.broken_image_24px);
                            }
                        }
                    } else {
                        holder.userAvatar.setImageResource(R.drawable.ic_launcher_background);
                    }
                }
            }

            @Override
            public void onUserNotFound() {
            }

            @Override
            public void onError(Exception e) {
                Log.e("Firestores", "Error getting document", e);
            }
        });
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chatSummary));
    }

    private String formatTimestamp(long timestampMillis) {
        Calendar now = Calendar.getInstance();
        Calendar messageTime = Calendar.getInstance();
        messageTime.setTimeInMillis(timestampMillis);

        long diffMillis = now.getTimeInMillis() - messageTime.getTimeInMillis();
        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis);
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
        long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        long diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (diffSeconds < 60) {
            return "Vừa xong";
        } else if (diffMinutes < 60) {
            return diffMinutes + " phút trước";
        } else if (diffHours < 24) {
            return diffHours + " giờ trước";
        } else if (diffDays < 7) {
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
        ImageView userAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }

    public interface OnChatClickListener {
        void onChatClick(ChatSummary chatSummary);
    }
}
