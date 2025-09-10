package com.example.myapplication3;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TitleLayout extends LinearLayout {

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title, this);

        Button btnBack = findViewById(R.id.btn_back);
        Button btnSetting = findViewById(R.id.btn_setting);

        // 返回按钮逻辑
        btnBack.setOnClickListener(v -> {
            Toast.makeText(context, "点击了返回", Toast.LENGTH_SHORT).show();
            // 如果想直接退出当前 Activity，可以这样写
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });

        // 设置按钮逻辑
        btnSetting.setOnClickListener(v -> {
            Toast.makeText(context, "点击了设置", Toast.LENGTH_SHORT).show();
        });
    }
}
