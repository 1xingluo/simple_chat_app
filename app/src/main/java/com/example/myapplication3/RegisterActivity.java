package com.example.myapplication3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnRegister;
    private ImageView ivAvatar;
    private CommonLayoutView commonLayoutView;
    private DBHelper dbHelper;
    private Bitmap selectedAvatar;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        ivAvatar = findViewById(R.id.iv_avatar);
        commonLayoutView = findViewById(R.id.common_layout_view);

        dbHelper = new DBHelper(this);

        // 点击头像选择图片
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if(uri != null){
                        try {
                            InputStream is = getContentResolver().openInputStream(uri);
                            selectedAvatar = BitmapFactory.decodeStream(is);
                            ivAvatar.setImageBitmap(selectedAvatar);
                            if(is != null) is.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        ivAvatar.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(username.isEmpty() || password.isEmpty()){
                commonLayoutView.showToast("请输入用户名和密码");
                return;
            }

            if(dbHelper.isUserExist(username)){
                commonLayoutView.showToast("用户名已存在");
                return;
            }

            // 添加用户
            boolean success = dbHelper.addUser(username, password);
            if(success){
                // 保存头像
                if(selectedAvatar != null){
                    saveAvatar(username, selectedAvatar);
                }
                commonLayoutView.showToast("注册成功");
                finish();
            } else {
                commonLayoutView.showToast("注册失败");
            }
        });
    }

    /** 保存头像到私有文件夹 */
    private void saveAvatar(String username, Bitmap avatar){
        try {
            File file = new File(getFilesDir(), username + "_avatar.png");
            FileOutputStream fos = new FileOutputStream(file);
            avatar.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
