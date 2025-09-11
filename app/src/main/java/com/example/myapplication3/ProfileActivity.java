package com.example.myapplication3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    private ImageView ivAvatar;
    private TextView tvUsername;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);

        // 获取用户名
        username = getIntent().getStringExtra("username");
        tvUsername.setText(username);

        // 加载本地头像
        loadAvatarFromInternalStorage(username);

        // 点击头像选择新图片
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();
            if(imageUri != null){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ivAvatar.setImageBitmap(bitmap);
                    saveAvatarToInternalStorage(bitmap, username);
                    Toast.makeText(this, "头像已更新", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveAvatarToInternalStorage(Bitmap bitmap, String username) {
        try {
            File file = new File(getFilesDir(), username + "_avatar.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "保存头像失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAvatarFromInternalStorage(String username) {
        File file = new File(getFilesDir(), username + "_avatar.png");
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivAvatar.setImageBitmap(bitmap);
        } else {
            ivAvatar.setImageResource(R.drawable.photo1); // 默认头像
        }
    }
}
