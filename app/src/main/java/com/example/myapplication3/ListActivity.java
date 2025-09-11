package com.example.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnBack, btnMe, btnAddFriend;
    private FriendListAdapter adapter;
    private DBHelper dbHelper;
    private String currentUsername;
    private int currentUserId;
    private List<Object> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.list_view);
        btnBack = findViewById(R.id.btn_back);
        btnMe = findViewById(R.id.btn_me);
        btnAddFriend = findViewById(R.id.btn_add_friend);

        dbHelper = new DBHelper(this);

        currentUsername = getIntent().getStringExtra("username");
        currentUserId = dbHelper.getUserId(currentUsername);

        items = new ArrayList<>();

        adapter = new FriendListAdapter(this, items, new FriendListAdapter.OnFriendRequestActionListener() {
            @Override
            public void onAccept(String username) {
                int friendId = dbHelper.getUserId(username);
                if (dbHelper.acceptFriendRequest(currentUserId, friendId)) {
                    Toast.makeText(ListActivity.this, "已同意好友请求: " + username, Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(ListActivity.this, "同意失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onReject(String username) {
                int friendId = dbHelper.getUserId(username);
                if (dbHelper.rejectFriendRequest(currentUserId, friendId)) {
                    Toast.makeText(ListActivity.this, "已拒绝好友请求: " + username, Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(ListActivity.this, "拒绝失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnMe.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, ProfileActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });

        btnAddFriend.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, AddFriendActivity.class);
            intent.putExtra("currentUserId", currentUserId);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object obj = items.get(position);
            if (obj instanceof Contact) {
                Contact contact = (Contact) obj;
                Intent intent = new Intent(ListActivity.this, ChatActivity.class);
                intent.putExtra("name", contact.getName());
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            }
        });

        loadData(); // 初始化数据
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // 页面重新显示时刷新列表
    }

    private void loadData() {
        items.clear();

        // 好友请求
        List<String> requests = dbHelper.getFriendRequests(currentUserId);
        items.addAll(requests);

        // 好友列表
        List<String> friends = dbHelper.getFriends(currentUserId);
        for (String friendName : friends) {
            items.add(new Contact(friendName, "", R.drawable.photo1));
        }

        adapter.notifyDataSetChanged(); // 刷新界面
    }
}
