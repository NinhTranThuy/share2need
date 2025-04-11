package com.example.share2need.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavouriteProductAdapter extends RecyclerView.Adapter<FavouriteProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    //String userId = auth.getCurrentUser().getUid();
    String userId = "userA";

    public FavouriteProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView favoriteIcon;
        ImageView imageView;
        TextView tvTitle, tvDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
            imageView = itemView.findViewById(R.id.productImage);
            tvTitle = itemView.findViewById(R.id.productTitle);
            tvDescription = itemView.findViewById(R.id.productDescription);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvTitle.setText(product.getName());
        holder.tvDescription.setText(product.getDescription());

        // Load ảnh sản phẩm
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(context)
                    .load(product.getImages().get(0))
                    .into(holder.imageView);
        }

        // Hiển thị icon yêu thích (vì là danh sách yêu thích, nên luôn hiển thị ❤️)
        holder.favoriteIcon.setImageResource(R.drawable.ic_star_filled); // trái tim đầy

        // Xử lý bỏ yêu thích
        holder.favoriteIcon.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // đảm bảo vị trí đúng
            if (currentPosition != RecyclerView.NO_POSITION) {
                db.collection("favorites")
                        .document(userId)
                        .update(productList.get(currentPosition).getId(), FieldValue.delete())
                        .addOnSuccessListener(unused -> {
                            productList.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, productList.size());
                            Toast.makeText(context, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Lỗi khi bỏ yêu thích", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
    @Override
    public int getItemCount() {
        return productList.size();
    }
}

