package com.example.share2need.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.models.Product;

public class ProductDetailActivity extends AppCompatActivity {
    ProductRepository productRepository = null;

    //UI mapping
    TextView tvNameProduct = null, tvCategory = null, tvQuantity = null, tvAddress = null,
            tvcreatedAt = null, tvDescription = null;
    ImageView imageProduct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initLayout();
        initData();
    }

    private void initLayout() {
        // Thêm đoạn này vào đầu phương thức onCreate() sau setContentView()
        tvNameProduct = findViewById(R.id.tvNameProduct);
        tvCategory = findViewById(R.id.tvCategory);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvAddress = findViewById(R.id.tvAddress);
        tvcreatedAt = findViewById(R.id.tvcreatedAt);
        tvDescription = findViewById(R.id.tvDescription);
        imageProduct = findViewById(R.id.imageProduct);
    }

    private void initData() {
        Intent intent = getIntent();
        String productId = intent.getStringExtra("productId").trim();
        Log.e("productId", productId);

        productRepository = new ProductRepository();
        productRepository.getProductById(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onProductLoaded(Product product) {
                if (product != null){
                    try {
                        // Kiểm tra product null
                        if (product == null) {
                            Log.e("ProductError", "Product is null");
                            return;
                        }

                        // Hiển thị thông tin cơ bản với kiểm tra null
                        if (tvNameProduct != null) {
                            tvNameProduct.setText(product.getName() != null ? product.getName() : "Không có tên");
                        }

                        if (tvCategory != null) {
                            tvCategory.setText(product.getCategory() != null ? product.getCategory() : "Không có danh mục");
                        }

                        if (tvQuantity != null) {
                            tvQuantity.setText(String.format("Số lượng: %d", product.getQuantity()));
                        }

                        if (tvAddress != null) {
                            tvAddress.setText(product.getAddress() != null ? product.getAddress() : "Không có địa chỉ");
                        }

                        // Xử lý thời gian với kiểm tra null
                        if (tvcreatedAt != null) {
                            try {
                                if (product.getCreatedAt() != null && product.getCreatedAt().toDate() != null) {
                                    tvcreatedAt.setText(getTimeAgo(product.getCreatedAt().toDate().getTime()));
                                } else {
                                    tvcreatedAt.setText("Không rõ thời gian");
                                }
                            } catch (Exception e) {
                                Log.e("TimeError", "Error parsing date", e);
                                tvcreatedAt.setText("Lỗi thời gian");
                            }
                        }

                        if (tvDescription != null) {
                            tvDescription.setText(product.getDescription() != null ? product.getDescription() : "Không có mô tả");
                        }

                        // Xử lý ảnh
                        String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ?
                                product.getImages().get(0) : null;

                        if (imageProduct != null) {
                            try {
                                if (imageUrl != null) {
                                    Glide.with(imageProduct.getContext())
                                            .load(imageUrl)
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .error(R.drawable.broken_image_24px)
                                            .into(imageProduct);
                                } else {
                                    imageProduct.setImageResource(R.drawable.ic_launcher_background);
                                }
                            } catch (Exception e) {
                                Log.e("ImageError", "Error loading image", e);
                                imageProduct.setImageResource(R.drawable.broken_image_24px);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("ProductDisplay", "Error displaying product", e);
                        // Xử lý fallback nếu cần
                    }
                } else {
                    tvNameProduct.setText("CRASH ROI");
                }

            }

            @Override
            public void onProductNotFound() {
                tvNameProduct.setText("404 Not Found");
            }

            @Override
            public void onError(Exception e) {
                Log.e("Firestore", "Error getting document", e);
            }
        });

    }

    public void openChatActivity_onClick(View view) {
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.putExtra("product_id", 1);
//        startActivity(intent);
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

    public void backActivity_onClick(View view) {
        finish();
    }
}