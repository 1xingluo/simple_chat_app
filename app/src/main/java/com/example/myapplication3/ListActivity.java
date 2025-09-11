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
    private List<Contact> contactList = new ArrayList<>();
    private ContactAdapter adapter;
    private DBHelper dbHelper;
    private String currentUsername;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.list_view);
        btnBack = findViewById(R.id.btn_back);
        btnMe = findViewById(R.id.btn_me);
        btnAddFriend = findViewById(R.id.btn_add_friend);

        dbHelper = new DBHelper(this);

        // 获取当前登录用户名
        currentUsername = getIntent().getStringExtra("username");
        currentUserId = dbHelper.getUserId(currentUsername);

        adapter = new ContactAdapter(this, contactList);
        listView.setAdapter(adapter);

        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        // 我的按钮
        btnMe.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, ProfileActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });

        // 添加好友按钮
        btnAddFriend.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, AddFriendActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });

        // 点击联系人跳转聊天界面
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Contact contact = contactList.get(position);
            Intent intent = new Intent(ListActivity.this, ChatActivity.class);
            intent.putExtra("name", contact.getName());
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFriends(); // 每次回到界面刷新好友列表
    }

    private void loadFriends() {
        contactList.clear();
        List<String> friends = dbHelper.getFriends(currentUserId);
        for (String friendName : friends) {
            // 这里头像可以默认，或者以后从Profile获取
            contactList.add(new Contact(friendName, "", R.drawable.photo1));
        }
        adapter.notifyDataSetChanged();
    }
}
