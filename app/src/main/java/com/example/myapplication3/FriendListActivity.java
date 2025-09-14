package com.example.myapplication3;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    private ListView listView;
    private DBHelper dbHelper;
    private int currentUserId;
    private FriendListAdapter adapter;
    private List<Object> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        dbHelper = new DBHelper(this);
        currentUserId = getIntent().getIntExtra("currentUserId", -1);
        listView = findViewById(R.id.list_view);

        items = new ArrayList<>();

        adapter = new FriendListAdapter(this, items, new FriendListAdapter.OnFriendRequestActionListener() {
            @Override
            public void onAccept(String username) {
                int friendId = dbHelper.getUserId(username);
                if(dbHelper.acceptFriendRequest(currentUserId, friendId)){
                    Toast.makeText(FriendListActivity.this, "已同意 " + username, Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(FriendListActivity.this, "同意失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onReject(String username) {
                int friendId = dbHelper.getUserId(username);
                if(dbHelper.rejectFriendRequest(currentUserId, friendId)){
                    Toast.makeText(FriendListActivity.this, "已拒绝 " + username, Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(FriendListActivity.this, "拒绝失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setAdapter(adapter);

        loadData(); // 初始化加载
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // 页面显示时刷新
    }

    private void loadData() {
        items.clear();

        // 接收的好友请求
        List<String> requests = dbHelper.getFriendRequests(currentUserId);
        items.addAll(requests);

        // 已同意好友
        List<String> friends = dbHelper.getFriends(currentUserId);
        for(String f : friends){
            File file = new File(getFilesDir(), f + "_avatar.png");
            String avatarPath = file.exists() ? file.getAbsolutePath() : null;
            items.add(new Contact(f, avatarPath));
        }

        adapter.notifyDataSetChanged();
    }
}
