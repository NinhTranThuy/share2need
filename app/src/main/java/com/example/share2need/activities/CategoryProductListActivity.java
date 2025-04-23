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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.R;
import com.example.share2need.adapters.ProductAdapter;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.firebase.UserRepository;
import com.example.share2need.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductListActivity extends AppCompatActivity {
    TextView tvCategoryName = null;
    ImageView noProductFoundIcon = null;
    RecyclerView recyclerViewProducts = null;
    List<Product> productList = new ArrayList<>();
    ProductRepository productRepository = null;
    UserRepository userRepository = null;
    String userId = "u1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_product_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        tvCategoryName = findViewById(R.id.tvCategoryName);
        noProductFoundIcon = findViewById(R.id.noProductFoundIcon);
        userRepository = new UserRepository();
        productRepository = new ProductRepository();
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);

        Intent intent = getIntent();
        String categoryName = intent.getStringExtra("categoryName");
        tvCategoryName.setText("Sản phẩm " + categoryName);

        productRepository.getCategoryProductsLive(categoryName).observe(this, products -> {
            if(products == null || products.isEmpty() || products.size() == 0) {
                noProductFoundIcon.setVisibility(View.VISIBLE);
                Log.d("CategoryProductListActivity", "Received products: NULL");
                return;
            }

            ProductAdapter productAdapter = new ProductAdapter(this, products);
            recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerViewProducts.setAdapter(productAdapter);
        });

        productRepository.startListening();
    }

    public void backCategoryActivity_onClick(View view) {
        finish();
    }
}