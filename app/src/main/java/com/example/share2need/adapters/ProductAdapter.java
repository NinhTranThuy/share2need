package com.example.share2need.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.activities.ProductDetailActivity;
import com.example.share2need.models.Product;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ?
                    product.getImages().get(0) : null;
        Log.e("ImageTest", imageUrl);
        if (holder.imgProduct != null) {
            if (imageUrl != null) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.broken_image_24px)
                        .into(holder.imgProduct);
            } else {
                holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
            }
        }
            holder.tvNameProduct.setText(product.getName());
            //Tinh toán khoảng cách rồi hiện lên
            holder.tvDistance.setText("Địa chỉ "+ product.getAddress());
            holder.tvCreatedAt.setText(getTimeAgo((long)product.getCreatedAt().toDate().getTime()));
            holder.tvStatus.setText(product.getStatus());
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("productId", product.getId());
                context.startActivity(intent);
            });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public static String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diffInMillis = now - timestamp;
        long diffInSeconds = diffInMillis / 1000;

        if (diffInSeconds < 60) {
            return "Vừa xong";
        } else if (diffInSeconds < 3600) { // Dưới 1 giờ
            long minutes = diffInSeconds / 60;
            return minutes + " phút trước";
        } else if (diffInSeconds < 86400) { // Dưới 1 ngày
            long hours = diffInSeconds / 3600;
            return hours == 1 ? "1 giờ trước" : hours + " giờ trước";
        } else if (diffInSeconds < 2592000) { // Dưới 30 ngày
            long days = diffInSeconds / 86400;
            return days == 1 ? "1 ngày trước" : days + " ngày trước";
        } else if (diffInSeconds < 31536000) { // Dưới 1 năm
            long months = diffInSeconds / 2592000;
            return months == 1 ? "1 tháng trước" : months + " tháng trước";
        } else { // Trên 1 năm
            long years = diffInSeconds / 31536000;
            return years == 1 ? "1 năm trước" : years + " năm trước";
        }
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgUserPost;
        TextView tvNameProduct, tvUserPostName, tvDistance, tvCreatedAt, tvStatus;
        CardView cardProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvNameProduct = itemView.findViewById(R.id.tvNameProduct);
            tvUserPostName = itemView.findViewById(R.id.tvUserPostName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvCreatedAt = itemView.findViewById(R.id.tvcreatedAt);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            cardProduct = itemView.findViewById(R.id.cardProduct);
        }
    }
}
