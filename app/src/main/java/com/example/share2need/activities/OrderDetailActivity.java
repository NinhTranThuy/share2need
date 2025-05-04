package com.example.share2need.activities;

import static android.webkit.URLUtil.isValidUrl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.firebase.OrderRepository;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.firebase.UserRepository;
import com.example.share2need.models.Order;
import com.example.share2need.models.Product;
import com.example.share2need.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderDetailActivity extends AppCompatActivity {
    TextView tvReceiverName, tvReceiverAdress, tvReceiverPhone;
    TextView tvProductName, tvQuantity, tvAddress;
    ImageView imgProduct;
    Button btnCancel, btnReceived;
    LinearLayout orderStateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initDataView();
        
    }
    private void initDataView() {
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        OrderRepository orderRepository = new OrderRepository();
        orderRepository.getOrderByOrderId(orderId, new OrderRepository.getProductByOrderIdCallback() {
            @Override
            public void onProductLoaded(Order order) {
                //Lay thong tin nguoi dung
                UserRepository userRepository = new UserRepository();
                userRepository.getUserInfo(order.getReceiverId(), new UserRepository.UserCallback() {
                    @Override
                    public void onUserLoaded(User user) {
                        tvReceiverName.setText("Người nhận: " + user.getFullname());
                        tvReceiverAdress.setText("Địa chỉ: " + user.getAddress());
                        tvReceiverPhone.setText("Số điện thoại: " + user.getPhone());
                    }

                    @Override
                    public void onUserNotFound() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                //Lay thong tin san pham
                ProductRepository productRepository = new ProductRepository();
                productRepository.getProductById(order.getProductId(), new ProductRepository.ProductCallback() {
                    @Override
                    public void onProductLoaded(Product product) {
                        tvProductName.setText(product.getName());
                        tvQuantity.setText("Số lượng: " + product.getQuantity());
                        tvAddress.setText("Địa chỉ: " + product.getAddress());
                        //Hien thi hinh anh san pham
                        String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ?
                                product.getImages().get(0) : null;
                        if (imgProduct != null) {
                            if (imageUrl != null) {
                                if (isValidUrl(imageUrl)) {
                                    // 🔗 Ảnh là URL
                                    Glide.with(imgProduct.getContext())
                                            .load(imageUrl)
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .error(R.drawable.broken_image_24px)
                                            .into(imgProduct);
                                } else {
                                    // 🧬 Ảnh là Base64
                                    try {
                                        byte[] decodedBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                        imgProduct.setImageBitmap(bitmap);
                                    } catch (Exception e) {
                                        Log.e("Base64Error", "Lỗi decode ảnh base64", e);
                                        imgProduct.setImageResource(R.drawable.broken_image_24px);
                                    }
                                }
                            } else {
                                imgProduct.setImageResource(R.drawable.ic_launcher_background);
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
            }
            @Override
            public void onProductNotFound() {
            }
            @Override
            public void onError(Exception e) {
            }
        });
    }
    private void initView() {
        orderStateView = findViewById(R.id.orderStateView);
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverAdress = findViewById(R.id.tvReceiverAdress);
        tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        tvProductName = findViewById(R.id.tvProductName);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvAddress = findViewById(R.id.tvAddress);
        imgProduct = findViewById(R.id.imgProduct);
        btnCancel = findViewById(R.id.btnCancel);
        btnReceived = findViewById(R.id.btnReceived);
    }

    public void backActivity_onClick(View view) {
        finish();
    }
    public void refuseOrder_onClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    Intent intent = getIntent();
                    String orderId = intent.getStringExtra("orderId");
                    OrderRepository orderRepository = new OrderRepository();
                    orderRepository.refuseOrder(orderId);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }
    public void confirmReceived_onClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Đã nhận được")
                .setMessage("Bạn đã nhận được sản phẩm này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    Intent intent = getIntent();
                    String orderId = intent.getStringExtra("orderId");
                    OrderRepository orderRepository = new OrderRepository();
                    orderRepository.acceptOrder(orderId);
                    orderStateView.setVisibility(View.VISIBLE);
                })
                .setNegativeButton("Không", null)
                .show();
    }
}