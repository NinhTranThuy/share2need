package com.example.share2need.firebase;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.share2need.models.Product;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private ListenerRegistration listener;
    public void getProductById(String productId, final ProductCallback callback){
        db.collection("products")
                .document(productId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            Product product = document.toObject(Product.class);
                            Log.e("testtest", product.toString());
                            callback.onProductLoaded(product);
                        }else{
                            callback.onProductNotFound();
                        }
                    }else{
                        callback.onError(task.getException());
                        }

                });
    }

    public void startListening() {
        // 1. Hủy listener cũ nếu có
        stopListening();

        // 2. Khởi tạo listener mới
        listener = db.collection("products")
                .addSnapshotListener((snapshots, error) -> {

                    if (error != null) {
                        Log.e("Firestore", "Listen failed", error);
                        productsLiveData.postValue(null);
                        return;
                    }
// Log số lượng document nhận được
                    Log.d("FirestoreDebug", "Total documents: " + snapshots.size());

                    // Log từng document ID
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Log.d("FirestoreDebug", "Change type: " + dc.getType() +
                                ", Doc ID: " + dc.getDocument().getId());
                    }
                    // 3. Xử lý snapshot
                    List<Product> products = new ArrayList<>();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            try {
                                Product product = doc.toObject(Product.class);
                                if (product != null) {
                                    product.setId(doc.getId());
                                    products.add(product);
                                    Log.d("DocDebug", "Document: " + doc.getId() +
                                            ", exists: " + doc.exists() +
                                            ", data: " + doc.getData());
                                }
                            } catch (RuntimeException e) {
                                Log.e("Firestore", "Parse error docId=" + doc.getId(), e);
                            }
                        }
                    }

                    // 4. Post value với check null
                    productsLiveData.postValue(products.isEmpty() ? null : products);
                });
    }

    public void stopListening() {
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }

    public LiveData<List<Product>> getProductsLiveData() {
        return productsLiveData;
    }

    public interface ProductCallback{
        public void onProductLoaded(Product product);
        public void onProductNotFound();
        public void onError(Exception e);
    }
}
