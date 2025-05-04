package com.example.share2need.adapters;

import static android.webkit.URLUtil.isValidUrl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.Timestamp;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.activities.ProductDetailActivity;
import com.example.share2need.firebase.UserRepository;
import com.example.share2need.models.Message;
import com.example.share2need.models.Product;
import com.example.share2need.models.User;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ChatDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_RIGHT = 1;
    private static final int TYPE_LEFT = 2;

    private List<Message> messageList;
    private String currentUserId;


    public ChatDetailAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_right, parent, false);
            return new RightViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_left, parent, false);
            return new LeftViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof RightViewHolder) {
            //Message ben phai
            ((RightViewHolder) holder).tvMessageText.setText(message.getMessage());
            ((RightViewHolder) holder).tvMessageTime.setText(formatTimestamp(message.getTimestamp()));
            int adapterPosition = holder.getAdapterPosition();
//            UserRepository userRepository = new UserRepository();
//            userRepository.getUserInfo(message.getSenderId(), new UserRepository.UserCallback() {
//                @Override
//                public void onUserLoaded(User user) {
//                    if (holder.getAdapterPosition() != adapterPosition) return;
//                    String imageUrl = (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) ?
//                            user.getProfileImage() : null;
//                    if (((RightViewHolder) holder).userAvatar != null) {
//                        if (imageUrl != null) {
//                            if (isValidUrl(imageUrl)) {
//                                // 🔗 Ảnh là URL
//                                Glide.with(holder.itemView.getContext())
//                                        .load(imageUrl)
//                                        .placeholder(R.drawable.ic_launcher_background)
//                                        .error(R.drawable.broken_image_24px)
//                                        .into(((RightViewHolder) holder).userAvatar);
//                            } else {
//                                // 🧬 Ảnh là Base64
//                                try {
//                                    byte[] decodedBytes = Base64.decode(imageUrl, Base64.DEFAULT);
//                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//                                    ((RightViewHolder) holder).userAvatar.setImageBitmap(bitmap);
//                                } catch (Exception e) {
//                                    Log.e("Base64Error", "Lỗi decode ảnh base64", e);
//                                    ((RightViewHolder) holder).userAvatar.setImageResource(R.drawable.broken_image_24px);
//                                }
//                            }
//                        } else {
//                            ((RightViewHolder) holder).userAvatar.setImageResource(R.drawable.ic_launcher_background);
//                        }
//                    }
//                }
//
//                @Override
//                public void onUserNotFound() {
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    Log.e("Firestores", "Error getting document", e);
//                }
//            });
        } else {
            //Message ben trai
            ((LeftViewHolder) holder).tvMessageText.setText(message.getMessage());
            ((LeftViewHolder) holder).tvMessageTime.setText(formatTimestamp(message.getTimestamp()));
            int adapterPosition = holder.getAdapterPosition();
            UserRepository userRepository = new UserRepository();
            userRepository.getUserInfo(message.getReceiverId(), new UserRepository.UserCallback() {
                @Override
                public void onUserLoaded(User user) {
                    if (holder.getAdapterPosition() != adapterPosition) return;
                    String imageUrl = (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) ?
                            user.getProfileImage() : null;
                    if (((LeftViewHolder) holder).userAvatar != null) {
                        if (imageUrl != null) {
                            if (isValidUrl(imageUrl)) {
                                // 🔗 Ảnh là URL
                                Glide.with(holder.itemView.getContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.broken_image_24px)
                                        .into(((LeftViewHolder) holder).userAvatar);
                            } else {
                                // 🧬 Ảnh là Base64
                                try {
                                    byte[] decodedBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                    ((LeftViewHolder) holder).userAvatar.setImageBitmap(bitmap);
                                } catch (Exception e) {
                                    Log.e("Base64Error", "Lỗi decode ảnh base64", e);
                                    ((LeftViewHolder) holder).userAvatar.setImageResource(R.drawable.broken_image_24px);
                                }
                            }
                        } else {
                            ((LeftViewHolder) holder).userAvatar.setImageResource(R.drawable.ic_launcher_background);
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
        }
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
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return TYPE_RIGHT;
        } else {
            return TYPE_LEFT;
        }
    }
    static class RightViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageText, tvMessageTime;
//        ImageView userAvatar;

        RightViewHolder(View itemView) {
            super(itemView);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
//            userAvatar = itemView.findViewById(R.id.userAvatar);
        }
    }

    static class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageText, tvMessageTime;
        ImageView userAvatar;

        LeftViewHolder(View itemView) {
            super(itemView);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            userAvatar = itemView.findViewById(R.id.userAvatar);
        }
    }

}
