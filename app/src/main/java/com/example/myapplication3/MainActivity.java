package com.example.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private ImageView bgImage;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        bgImage = findViewById(R.id.bg);

        bgImage.setAlpha(0.5f);
        dbHelper = new DBHelper(this);

        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
                return;
            }
            if(dbHelper.checkUser(username,password)){
                Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("username", username); // 传递用户名
                startActivity(intent);
            } else {
                Toast.makeText(this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
