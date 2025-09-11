package com.example.myapplication3;

public class Contact {
    private String name;
    private String phone;
    private int avatarResId;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.avatarResId = android.R.drawable.sym_def_app_icon;
    }

    public Contact(String name, String phone, int avatarResId) {
        this.name = name;
        this.phone = phone;
        this.avatarResId = avatarResId;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public int getAvatarResId() { return avatarResId; }
}