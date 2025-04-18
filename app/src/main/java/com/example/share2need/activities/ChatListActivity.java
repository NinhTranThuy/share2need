package com.example.share2need.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.R;
import com.example.share2need.adapters.ChatAdapter;
import com.example.share2need.adapters.UserAdapter;
import com.example.share2need.models.Chat;
import com.example.share2need.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity implements ChatAdapter.OnChatClickListener{
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> chatList;
    private DatabaseReference chatSummariesRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.recyclerViewChatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, this);
        recyclerView.setAdapter(adapter);

        // Kết nối với Firebase để lấy danh sách cuộc trò chuyện
        chatSummariesRef = FirebaseDatabase.getInstance().getReference("chatSummaries");
        chatSummariesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userName = snapshot.child("userName").getValue(String.class);
                    String lastMessage = snapshot.child("lastMessage").getValue(String.class);
                    String timestamp = snapshot.child("timestamp").getValue(String.class);
                    if (userName != null && lastMessage != null && timestamp != null) {
                        chatList.add(new Chat(userName, lastMessage, timestamp));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatListActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(this, ChatDetailActivity.class);
        intent.putExtra("userName", chat.getUserName());
        startActivity(intent);
    }
    public void backActivity_onClick(View view) {
        finish();
    }
}