package com.example.myapplication3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnBack;
    private String[] data = {"A", "B", "C", "D", "E"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.list_view);
        btnBack = findViewById(R.id.btn_back);

        // 返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 结束当前 Activity 返回上一个界面
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = data[position];
                Toast.makeText(ListActivity.this, "点击了：" + item, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

