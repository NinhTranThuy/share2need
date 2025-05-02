package com.example.share2need.firebase;
import static java.security.AccessController.getContext;

import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.share2need.models.Product;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private ListenerRegistration listener;

//    Bao gio co Login Firebase thi bo
    String userID = "u1";
    public void insertProrduct(String nameProduct, int quantity, String description,
                               String category, List<String> listImage, String address, GeoPoint location){
        db.collection("products")
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        int id = task.getResult().size();
                        String newID = "pr"+ (id+1);

                        Product newProduct = new Product(
                                newID,
                                userID,
                                nameProduct,
                                nameProduct.toLowerCase(),
                                description,
                                category,
                                listImage,
                                quantity,
                                "còn hàng",
                                address,
                                location,
                                Timestamp.now(),
                                Timestamp.now()
                        );

                        db.collection("products")
                                .document(newID)
                                .set(newProduct)
                                .addOnSuccessListener(task1 -> {
                                    Log.e("ProductRepository", "Add Proudct Success " + listImage.size());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ProductRepository", "Add Proudct Fail: " + e );
                                });
                    }
                });
    }
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
    public LiveData<List<Product>> getAllProductByUserID(String userID) {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();

        db.collection("products")
                .whereEqualTo("userId", userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId());
                        products.add(product);
                        Log.d("DocDebug", "Document: " + document.getId());
                    }
                    liveData.setValue(products);
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
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
    public LiveData<List<Product>> getCategoryProductsLive(String category) {
        category = category.trim();
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();
        db.collection("products")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                   List<Product> products = new ArrayList<>();
                    Log.d("DocDebugg", "Document: Check ");
                   for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                       Product product = document.toObject(Product.class);
                       product.setId(document.getId());
                       products.add(product);
                        Log.d("DocDebugg", "Document: " + document.getId());
                   }
                   liveData.setValue(products);
                })
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public void searchProduct(String keyword, int selectedDistance, String category, Location userLocation, SearchCallback callback){
        CollectionReference productRef = db.collection("products");
        Query query = productRef;

        if(!category.equals("Tất cả")){
            query = query.whereEqualTo("category", category);
        }

        String[] keywordParts = keyword.toLowerCase().trim().split("\\s+");

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<Product> products = new ArrayList<>();

                for(DocumentSnapshot snapshot : task.getResult()){
                    Product product = snapshot.toObject(Product.class);

                    String lowerName = snapshot.getString("lowercase_name");

                    boolean matched = true;

                    if (!keyword.isEmpty()) {
                        if (lowerName == null) {
                            matched = false;
                        } else {
                            for (String part : keywordParts) {
                                if (!lowerName.contains(part)) {
                                    matched = false;
                                    break;
                                }
                            }
                        }
                    }

                    if (matched) {
                        // ✅ Tính khoảng cách nếu cần
                        if (userLocation != null && product.getLocation() != null && selectedDistance > 0) {
                            double distance = getDistanceInKm(
                                    userLocation.getLatitude(), userLocation.getLongitude(),
                                    product.getLocation().getLatitude(), product.getLocation().getLongitude()
                            );

                            if (distance <= selectedDistance) {
                                products.add(product);
                            }
                        } else {
                            products.add(product);
                        }
                    }
                }

                callback.onSearchSuccess(products);
            } else {
                callback.onSearchError(task.getException());
            }
        });
    }

    private double getDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
        float[] result = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, result);
        return result[0] / 1000.0;
    }

    public interface SearchCallback{
        public void onSearchSuccess(List<Product> products);
        public void onSearchError(Exception e);
    }
    public interface ProductCallback{
        public void onProductLoaded(Product product);
        public void onProductNotFound();
        public void onError(Exception e);
    }
}
