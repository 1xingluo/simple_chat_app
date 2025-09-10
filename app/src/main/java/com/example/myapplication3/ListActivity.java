package com.example.myapplication3;

import android.os.Bundle;
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

        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        // 添加联系人数据，使用大图资源
        contactList.add(new Contact("张三", "13800000000", R.drawable.photo1));
        contactList.add(new Contact("李四", "13900000000", R.drawable.photo2));
        contactList.add(new Contact("王五", "13700000000", R.drawable.photo3));

        adapter = new ContactAdapter(this, contactList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Contact contact = contactList.get(position);
            Toast.makeText(ListActivity.this,
                    "点击了：" + contact.getName() + "\n电话：" + contact.getPhone(),
                    Toast.LENGTH_SHORT).show();
        });
    }
}
