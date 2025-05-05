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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.activities.OrderDetailActivity;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.firebase.UserRepository;
import com.example.share2need.models.Order;
import com.example.share2need.models.Product;
import com.example.share2need.models.User;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    private List<Order> listOrder;

    public OrderAdapter(Context context, List<Order> listOrder) {
        this.listOrder = listOrder;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        Order order = listOrder.get(position);

        // Hiển thị thông tin đơn hàng

        //Hien thi ten nguoi gui
        UserRepository userRepository = new UserRepository();
        userRepository.getUserInfo(order.getReceiverId(), new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                holder.tvReceiverName.setText(user.getFullname());
            }

            @Override
            public void onUserNotFound() {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        holder.tvNameProduct.setText(order.getProductName());
        holder.tvQuantity.setText("Số lượng: " + order.getQuantity() + "");
        if(order.getReceiveTime() != 0) {
            holder.tvReceiverTime.setText(formatTimestamp(order.getReceiveTime()));
        }

        //Hien thi anh san pham
        ProductRepository productRepository = new ProductRepository();
        productRepository.getProductById(order.getProductId(), new ProductRepository.ProductCallback() {
            @Override
            public void onProductLoaded(Product product) {
                String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ?
                        product.getImages().get(0) : null;
                if (holder.ivProduct != null) {
                    if (imageUrl != null) {
                        if (isValidUrl(imageUrl)) {
                            // 🔗 Ảnh là URL
                            Glide.with(holder.ivProduct)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.broken_image_24px)
                                    .into(holder.ivProduct);
                        } else {
                            // 🧬 Ảnh là Base64
                            try {
                                byte[] decodedBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                holder.ivProduct.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                Log.e("Base64Error", "Lỗi decode ảnh base64", e);
                                holder.ivProduct.setImageResource(R.drawable.broken_image_24px);
                            }
                        }
                    } else {
                        holder.ivProduct.setImageResource(R.drawable.ic_launcher_background);
                    }
                }
            }
            @Override
            public void onProductNotFound() {

            }
            @Override
            public void onError(Exception e) {

            }
        });



        holder.itemOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), OrderDetailActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                Log.d("orderId", order.getOrderId());
                holder.itemView.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverName,tvNameProduct, tvQuantity, tvReceiverTime;
        ImageView ivProduct;
        Button btnRate;
        ConstraintLayout itemOrder;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemOrder = itemView.findViewById(R.id.itemOrder);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvNameProduct = itemView.findViewById(R.id.tvNameProduct);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvReceiverTime = itemView.findViewById(R.id.tvReceiverTime);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
    private String formatTimestamp(long timestampMillis) {
        Calendar now = Calendar.getInstance();
        Calendar messageTime = Calendar.getInstance();
        messageTime.setTimeInMillis(timestampMillis);
        SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
        return fullFormat.format(messageTime.getTime());
    }
}
