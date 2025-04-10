package com.example.share2need;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ArrayList<Product> favoriteList = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration listener;

    private String currentUserId = "userA"; // Bạn có thể lấy từ FirebaseAuth.getInstance().getCurrentUser().getUid()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(this, favoriteList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadFavorites();
    }

    private void loadFavorites() {
        db.collection("favorites").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> favoritesMap = documentSnapshot.getData();
                        if (favoritesMap != null) {
                            favoriteList.clear();
                            for (String productId : favoritesMap.keySet()) {
                                db.collection("products").document(productId).get()
                                        .addOnSuccessListener(productSnap -> {
                                            if (productSnap.exists()) {
                                                Product product = productSnap.toObject(Product.class);
                                                if (product != null) {
                                                    favoriteList.add(product);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (listener != null) listener.remove();
        super.onDestroy();
    }
}