package com.example.share2need;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.adapters.ProductAdapter;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewProducts = null;
    List<Product> productList = new ArrayList<>();
    ProductRepository productRepository = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        Log.d("Network", "Online: " + isConnected);
        FirebaseFirestore.setLoggingEnabled(true); // Thêm vào onCreate()
        initDataView();
    }

    private void initDataView() {


        productRepository = new ProductRepository();
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);


        // 1. Khởi tạo observer
        productRepository.getProductsLiveData().observe(this, products -> {
            if (products == null) {
                Log.e("MainActivity", "Received null products");
                return;
            }
            Log.d("MainActivity", "Received " + products.size() + " products");
            ProductAdapter productAdapter = new ProductAdapter(this, products);
            recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerViewProducts.setAdapter(productAdapter);
        });

        // 2. Bắt đầu lắng nghe
        productRepository.startListening();
//            productList = (List<Product>) productRepository.getAllProductsLive();



    }
}