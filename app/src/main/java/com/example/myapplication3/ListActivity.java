package com.example.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnBack, btnMe, btnAddFriend, btnGame, btnCanvas;
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
        btnGame = findViewById(R.id.btn_game);

        dbHelper = new DBHelper(this);
        currentUsername = getIntent().getStringExtra("username");
        currentUserId = dbHelper.getUserId(currentUsername);

        items = new ArrayList<>();

        // 初始化适配器
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

        // 删除好友回调
        adapter.setOnFriendDeleteListener(friendName -> {
            int friendId = dbHelper.getUserId(friendName);
            if (dbHelper.deleteFriend(currentUserId, friendId)) {
                Toast.makeText(ListActivity.this, "已删除好友: " + friendName, Toast.LENGTH_SHORT).show();
                loadData();
            } else {
                Toast.makeText(ListActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });

        // 点击好友进入聊天
        adapter.setOnItemClickListener(contact -> {
            int friendId = dbHelper.getUserId(contact.getName());
            Intent intent = new Intent(ListActivity.this, ChatActivity.class);
            intent.putExtra("currentUserId", currentUserId);
            intent.putExtra("currentUsername", currentUsername);
            intent.putExtra("friendId", friendId);
            intent.putExtra("friendName", contact.getName());
            startActivity(intent);
        });

        listView.setAdapter(adapter);

        // 顶部按钮
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
        btnGame.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, WhackAMoleActivity.class);
            startActivity(intent);
        });

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // 页面显示时刷新数据
    }

    /** 加载好友请求和好友列表 */
    private void loadData() {
        items.clear();

        // 好友请求
        List<String> requests = dbHelper.getFriendRequests(currentUserId);
        items.addAll(requests);

        // 好友列表
        List<String> friends = dbHelper.getFriends(currentUserId);
        for (String friendName : friends) {
            File file = new File(getFilesDir(), friendName + "_avatar.png");
            String avatarPath = file.exists() ? file.getAbsolutePath() : null;
            items.add(new Contact(friendName, avatarPath));
        }

        adapter.notifyDataSetChanged();
    }
}
