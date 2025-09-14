package com.example.myapplication3;

import java.io.File;

public class Contact {
    private String name;
    private String phone;
    private String avatarPath; // 本地头像路径

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.avatarPath = null;
    }

    public Contact(String name, String phone, String avatarPath) {
        this.name = name;
        this.phone = phone;
        this.avatarPath = avatarPath;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAvatarPath() { return avatarPath; }

    /** 获取头像文件是否存在 */
    public boolean hasAvatar() {
        return avatarPath != null && new File(avatarPath).exists();
    }
}
