package com.example.myapplication3;

import java.io.File;

public class Contact {
    private String name;
    private String avatarPath; // 头像路径

    public Contact(String name, String avatarPath) {
        this.name = name;
        this.avatarPath = avatarPath;
    }

    public String getName() { return name; }

    public String getAvatarPath() { return avatarPath; }

    public boolean hasAvatar() {
        return avatarPath != null && new File(avatarPath).exists();
    }
}
