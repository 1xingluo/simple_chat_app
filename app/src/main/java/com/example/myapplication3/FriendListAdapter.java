package com.example.myapplication3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class FriendListAdapter extends BaseAdapter {

    private Context context;
    private List<Object> items; // Contact 或 String（好友请求）
    private OnFriendRequestActionListener listener;
    private int avatarSize; // dp -> px

    public interface OnFriendRequestActionListener {
        void onAccept(String username);
        void onReject(String username);
    }

    public FriendListAdapter(Context context, List<Object> items, OnFriendRequestActionListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;

        // 设置头像显示大小 48dp
        float scale = context.getResources().getDisplayMetrics().density;
        avatarSize = (int) (48 * scale + 0.5f);
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof Contact) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() { return 2; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object obj = items.get(position);

        if (getItemViewType(position) == 0) {
            // 好友
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
            }

            ImageView avatar = convertView.findViewById(R.id.img_avatar);
            TextView name = convertView.findViewById(R.id.tv_name);

            Contact contact = (Contact) obj;
            name.setText(contact.getName());

            // 加载头像
            if (contact.hasAvatar()) {
                File file = new File(contact.getAvatarPath());
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, avatarSize, avatarSize, true);
                    avatar.setImageBitmap(scaled);
                } else {
                    avatar.setImageResource(R.drawable.photo1);
                }
            } else {
                avatar.setImageResource(R.drawable.photo1);
            }

        } else {
            // 好友请求
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_request, parent, false);
            }

            TextView tvName = convertView.findViewById(R.id.tv_request_name);
            Button btnAccept = convertView.findViewById(R.id.btn_accept);
            Button btnReject = convertView.findViewById(R.id.btn_reject);

            String requester = (String) obj;
            tvName.setText("好友请求: " + requester);

            // 移除旧监听器，防止重用问题
            btnAccept.setOnClickListener(null);
            btnReject.setOnClickListener(null);

            btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(requester);
            });

            btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(requester);
            });
        }

        return convertView;
    }
}
