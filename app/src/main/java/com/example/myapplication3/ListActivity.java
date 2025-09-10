package com.example.myapplication3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

        // 返回按钮点击监听
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 返回登录界面
            }
        });

        // 模拟联系人数据
        contactList.add(new Contact("张三", "13800000000"));
        contactList.add(new Contact("李四", "13900000000"));
        contactList.add(new Contact("王五", "13700000000"));

        // 设置适配器
        adapter = new ContactAdapter(this, contactList);
        listView.setAdapter(adapter);

        // 点击每个联系人显示Toast
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactList.get(position);
                Toast.makeText(ListActivity.this,
                        "点击了：" + contact.getName() + "\n电话：" + contact.getPhone(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

