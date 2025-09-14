package com.example.myapplication3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ContactAdapter extends BaseAdapter {

    private Context context;
    private List<Contact> contactList;
    private int avatarSize; // dp 转 px 的头像大小

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        // 设置头像显示大小 72dp
        float scale = context.getResources().getDisplayMetrics().density;
        avatarSize = (int) (72 * scale + 0.5f);
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView phone;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
            holder = new ViewHolder();
            holder.avatar = convertView.findViewById(R.id.img_avatar);
            holder.name = convertView.findViewById(R.id.tv_name);
            holder.phone = convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());

        // 根据本地文件路径加载头像
        if (contact.hasAvatar()) {
            File file = new File(contact.getAvatarPath());
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, avatarSize, avatarSize, true);
                holder.avatar.setImageBitmap(scaledBitmap);
            } else {
                holder.avatar.setImageResource(R.drawable.photo1);
            }
        } else {
            holder.avatar.setImageResource(R.drawable.photo1);
        }

        return convertView;
    }
}
