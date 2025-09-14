package com.example.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

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

    private CommonLayoutView commonLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        commonLayoutView = findViewById(R.id.common_layout_view);

        listView = findViewById(R.id.list_view);
        btnBack = findViewById(R.id.btn_back);
        btnMe = findViewById(R.id.btn_me);
        btnAddFriend = findViewById(R.id.btn_add_friend);

        dbHelper = new DBHelper(this);

        currentUsername = getIntent().getStringExtra("username");
        currentUserId = dbHelper.getUserId(currentUsername);

        items = new ArrayList<>();

        // 初始化好友适配器
        adapter = new FriendListAdapter(this, items, new FriendListAdapter.OnFriendRequestActionListener() {
            @Override
            public void onAccept(String username) {
                int friendId = dbHelper.getUserId(username);
                commonLayoutView.showLoading(true);
                if (dbHelper.acceptFriendRequest(currentUserId, friendId)) {
                    commonLayoutView.showToast("已同意好友请求: " + username);
                    loadData();
                } else {
                    commonLayoutView.showToast("同意失败，请重试");
                }
                commonLayoutView.showLoading(false);
            }

            @Override
            public void onReject(String username) {
                int friendId = dbHelper.getUserId(username);
                commonLayoutView.showLoading(true);
                if (dbHelper.rejectFriendRequest(currentUserId, friendId)) {
                    commonLayoutView.showToast("已拒绝好友请求: " + username);
                    loadData();
                } else {
                    commonLayoutView.showToast("拒绝失败，请重试");
                }
                commonLayoutView.showLoading(false);
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

        // 点击好友进入聊天
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object item = items.get(position);
            if (item instanceof Contact) {
                Contact contact = (Contact) item;
                int friendId = dbHelper.getUserId(contact.getName());
                String friendName = contact.getName();
                Intent intent = new Intent(ListActivity.this, ChatActivity.class);
                intent.putExtra("currentUserId", currentUserId); // 当前用户ID
                intent.putExtra("currentUsername", currentUsername); // 当前用户名
                intent.putExtra("friendId", friendId); // 好友ID
                intent.putExtra("friendName", friendName); // 好友用户名
                startActivity(intent);
            }
        });

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        items.clear();
        commonLayoutView.showLoading(true);

        // 好友请求
        List<String> requests = dbHelper.getFriendRequests(currentUserId);
        items.addAll(requests);  // 保留字符串形式显示请求

        // 好友列表
        List<String> friends = dbHelper.getFriends(currentUserId);
        for (String friendName : friends) {
            items.add(new Contact(friendName, "", R.drawable.photo1));
        }

        adapter.notifyDataSetChanged();
        commonLayoutView.showLoading(false);
    }
}
