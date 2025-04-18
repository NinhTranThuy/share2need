package com.example.share2need.firebase;

import android.util.Log;

import com.example.share2need.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getUserInfo(String userID, UserCallback callback ){
        db.collection("users")
                .document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null) {
                            callback.onUserLoaded(user);
                        } else {
                            callback.onUserNotFound();
                        }
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    public void updateUserAddress(String userID, String address){
        db.collection("users")
                .document(userID)
                .update("address",address)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UserRepository", "Address updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("UserRepository", "Error updating address", e);
                });
    }
    public interface UserCallback{
        public void onUserLoaded(User user);
        public void onUserNotFound();
        public void onError(Exception e);
    }
}
