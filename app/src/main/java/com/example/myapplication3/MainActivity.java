package com.example.myapplication3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister, btnCanvas;
    private ImageView ivAvatar;
    private ProgressBar progressBar;  // Add ProgressBar
    private DBHelper dbHelper;

    // ActivityResultLauncher 用于选择图片
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivAvatar = findViewById(R.id.iv_avatar);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnCanvas = findViewById(R.id.btn_canvas);
        progressBar = findViewById(R.id.progressBar);  // Initialize ProgressBar

        dbHelper = new DBHelper(this);

        // 注册图片选择回调
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        Uri uri = result.getData().getData();
                        if(uri != null){
                            saveAvatar(uri, etUsername.getText().toString().trim());
                        }
                    }
                }
        );

        // 点击头像换头像
        ivAvatar.setOnClickListener(v -> {
            if(etUsername.getText().toString().trim().isEmpty()){
                Toast.makeText(MainActivity.this, "请先输入用户名", Toast.LENGTH_SHORT).show();
                return;
            }
            // 打开图库选择图片
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // 用户名输入变化时更新头像
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                loadAvatar(s.toString().trim());
            }
        });

        // 登录按钮点击
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(MainActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(ProgressBar.VISIBLE);  // Show loading indicator

            etUsername.postDelayed(() -> {
                if(dbHelper.checkUser(username, password)){
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    loadAvatar(username);

                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(ProgressBar.GONE);  // Hide loading indicator
            }, 2000);
        });

        // 注册按钮点击
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 画布按钮点击事件
        btnCanvas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CanvasActivity.class);  // CanvasActivity
            startActivity(intent);
        });
    }

    /** 加载用户头像 */
    private void loadAvatar(String username){
        if(username.isEmpty()){
            ivAvatar.setImageResource(R.drawable.photo1);
            return;
        }
        File file = new File(getFilesDir(), username + "_avatar.png");
        if(file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivAvatar.setImageBitmap(bitmap);
        } else {
            ivAvatar.setImageResource(R.drawable.photo1);
        }
    }

    /** 保存头像到本地 */
    private void saveAvatar(Uri uri, String username){
        if(username.isEmpty()) return;
        try{
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            File file = new File(getFilesDir(), username + "_avatar.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            ivAvatar.setImageBitmap(bitmap);
            Toast.makeText(MainActivity.this, "头像已保存", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "头像保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}
