package com.example.share2need.activities.ui.main.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.share2need.R;
import com.example.share2need.adapters.OrderAdapter;
import com.example.share2need.models.Order;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private OrderAdapter adapter;
    private String orderState;
    private RecyclerView recyclerView;
    private List<Order> orderList = new ArrayList<>();

    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderState = getArguments().getString(ARG_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OrderAdapter(getContext(), orderList); // DÙNG LIST CHUNG
        recyclerView.setAdapter(adapter);

        loadOrdersFromFirebase();

        return view;
    }

    private List<Order> getOrdersByStatus(String status) {
        // Gắn Firebase hoặc trả danh sách mẫu
        return new ArrayList<>(); // TODO: Load real data
    }
    public void reloadData() {
        // Xóa dữ liệu hiện tại
        orderList.clear();
        // Tải lại dữ liệu từ Firebase
        loadOrdersFromFirebase();
    }
    private void loadOrdersFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = "u1"; // Hoặc dùng FirebaseAuth.getInstance().getCurrentUser().getUid()

        db.collection("orders")
                .document(userId)
                .collection("user_orders")
                .whereEqualTo("orderState", orderState)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear(); // Xoá danh sách cũ

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long receiveTime = doc.getLong("receiveTime");
                        Long cancelTime = doc.getLong("cancelTime");

                        Order order = doc.toObject(Order.class);

                        // Gán thời gian nếu có
                        order.setReceiveTime(receiveTime != null ? receiveTime : 0);
                        order.setCancelTime(cancelTime != null ? cancelTime : 0);

                        orderList.add(order);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderListFragment", "Error getting orders: " + e.getMessage(), e);
                });
    }


}

//db.collection("orders")
//                .whereEqualTo("userId", user.getUid())
//                .whereEqualTo("status", status)
//                .orderBy("createdAt", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    orderList.clear();
//                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                        Order order = doc.toObject(Order.class);
//                        orderList.add(order);
//                    }
//                    adapter.notifyDataSetChanged();
//                });
