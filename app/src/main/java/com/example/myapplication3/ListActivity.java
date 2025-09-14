package com.example.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
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

        adapter = new FriendListAdapter(this, items, new FriendListAdapter.OnFriendRequestActionListener() {
            @Override
            public void onAccept(String username) {
                int friendId = dbHelper.getUserId(username);
                if(dbHelper.acceptFriendRequest(currentUserId, friendId)){
                    commonLayoutView.showToast("已同意好友请求: " + username);
                    loadData();
                } else {
                    commonLayoutView.showToast("同意失败，请重试");
                }
            }

            @Override
            public void onReject(String username) {
                int friendId = dbHelper.getUserId(username);
                if(dbHelper.rejectFriendRequest(currentUserId, friendId)){
                    commonLayoutView.showToast("已拒绝好友请求: " + username);
                    loadData();
                } else {
                    commonLayoutView.showToast("拒绝失败，请重试");
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
            Object item = items.get(position);
            if(item instanceof Contact){
                Contact contact = (Contact) item;
                int friendId = dbHelper.getUserId(contact.getName());
                Intent intent = new Intent(ListActivity.this, ChatActivity.class);
                intent.putExtra("currentUserId", currentUserId);
                intent.putExtra("currentUsername", currentUsername);
                intent.putExtra("friendId", friendId);
                intent.putExtra("friendName", contact.getName());
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

    private void loadData(){
        items.clear();

        // 好友请求
        List<String> requests = dbHelper.getFriendRequests(currentUserId);
        items.addAll(requests);

        // 好友列表
        List<String> friends = dbHelper.getFriends(currentUserId);
        for(String friendName : friends){
            File file = new File(getFilesDir(), friendName + "_avatar.png");
            String avatarPath = file.exists() ? file.getAbsolutePath() : null;
            items.add(new Contact(friendName, "暂无电话", avatarPath));
        }

        adapter.notifyDataSetChanged();
    }
}
