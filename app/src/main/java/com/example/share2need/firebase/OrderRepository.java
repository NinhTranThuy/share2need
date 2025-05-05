package com.example.share2need.firebase;

import android.util.Log;

import com.example.share2need.models.Order;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class OrderRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID = "u1";
    public void getAllOrder(){
        db.collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d("OrderRepository", document.getId() + " => " + document.getData());

                                }
                            }
                        })
                .addOnFailureListener(e -> {
                    Log.e("OrderRepository", "Get order fail: " + e);
                });
    }

    public void getOrderByOrderId(String orderId, getProductByOrderIdCallback callback){
        db.collection("orders")
                .document(userID)
                .collection("user_orders")
                .document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            Order order = document.toObject(Order.class);
                            callback.onProductLoaded(order);
                            Log.e("testtest", order.toString());
                        }else{
                            Log.e("testtest", "Order not found");
                            callback.onProductNotFound();
                        };
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }
    public void createOrder( String receiverId, String giverId, String productId, String chatId,
                            createOrderCallback callback){
        String bookingId = "BOOK_" + receiverId + "_" + String.valueOf(System.currentTimeMillis());
        db.collection("products")
                .document(productId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Order newOrder = new Order(
                                bookingId,
                                "đang giao",
                                productId,
                                receiverId,
                                giverId,
                                task.getResult().getString("name"),
                                task.getResult().getLong("quantity").intValue(),
                                System.currentTimeMillis(),
                                chatId);

                        db.collection("orders")
                                .document(receiverId) // userID
                                .collection("user_orders")
                                .document(newOrder.getOrderId()) // orderID
                                .set(newOrder)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onCreateSuccess(bookingId);
                                })
                                .addOnFailureListener(e -> {
                                    callback.onCreateError(e);
                                });
                    }
                });
    }

    public void refuseOrder(String orderId) {
        db.collection("orders")
                .document(userID)
                .collection("user_orders")
                .document(orderId)
                .update("orderState", "hủy"
                ,"cancelTime", System.currentTimeMillis());

        db.collection("orders")
                .document(userID)
                .collection("user_orders")
                .document(orderId)
                .get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       DocumentSnapshot document = task.getResult();
                       String productId = document.getString("productId");

                       db.collection("products")
                               .document(productId)
                               .update("status", "còn hàng");
                   }
                });
    }

    public void acceptOrder(String orderId) {
        db.collection("orders")
                .document(userID)
                .collection("user_orders")
                .document(orderId)
                .update("orderState", "đã nhận",
                        "receiveTime", System.currentTimeMillis());
    }

    public interface createOrderCallback{
        public void onCreateSuccess(String bookingId);
        public void onCreateError(Exception e);
    }

    public interface getProductByOrderIdCallback{
        public void onProductLoaded(Order order);
        public void onProductNotFound();
        public void onError(Exception e);
    }
}
