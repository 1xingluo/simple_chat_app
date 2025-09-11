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
    private Button btnBack;
    private List<Contact> contactList = new ArrayList<>();
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.list_view);
        btnBack = findViewById(R.id.btn_back);

        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        // 添加联系人数据
        contactList.add(new Contact("张三", "13800000000", R.drawable.photo1));
        contactList.add(new Contact("李四", "13900000000", R.drawable.photo2));
        contactList.add(new Contact("王五", "13700000000", R.drawable.photo3));

        adapter = new ContactAdapter(this, contactList);
        listView.setAdapter(adapter);
        Button btnMe = findViewById(R.id.btn_me);

        btnMe.setOnClickListener(v -> {
            // 假设当前登录用户名存储在 intent 或 sharedPreference 中
            String currentUsername = getIntent().getStringExtra("username");
            Intent intent = new Intent(ListActivity.this, ProfileActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });

        // 点击联系人跳转聊天界面，并传递联系人名字
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Contact contact = contactList.get(position);
            Intent intent = new Intent(ListActivity.this, ChatActivity.class);
            intent.putExtra("name", contact.getName()); // ✅ 传递联系人名字
            startActivity(intent);
        });
    }
}

