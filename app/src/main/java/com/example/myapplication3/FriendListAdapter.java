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

    public interface OnFriendDeleteListener {
        void onDelete(String friendName);
    }

    public interface OnItemClickListener {
        void onItemClick(Contact contact);
    }

    private OnFriendDeleteListener deleteListener;
    private OnItemClickListener itemClickListener;

    public void setOnFriendDeleteListener(OnFriendDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public FriendListAdapter(Context context, List<Object> items, OnFriendRequestActionListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;

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
        Object obj = getItem(position);

        if (getItemViewType(position) == 0) {
            // 好友项
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
            }

            ImageView avatar = convertView.findViewById(R.id.img_avatar);
            TextView name = convertView.findViewById(R.id.tv_name);
            Button btnDelete = convertView.findViewById(R.id.btn_delete);

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

            // 删除按钮事件
            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) deleteListener.onDelete(contact.getName());
            });

            // 关键：让按钮不阻塞条目点击
            btnDelete.setFocusable(false);
            btnDelete.setFocusableInTouchMode(false);

            // 整个条目点击事件
            convertView.setOnClickListener(v -> {
                if (itemClickListener != null) itemClickListener.onItemClick(contact);
            });

        } else {
            // 好友请求项
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_request, parent, false);
            }

            TextView tvName = convertView.findViewById(R.id.tv_request_name);
            Button btnAccept = convertView.findViewById(R.id.btn_accept);
            Button btnReject = convertView.findViewById(R.id.btn_reject);

            String requester = (String) obj;
            tvName.setText("好友请求: " + requester);

            // 清除旧监听器
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
