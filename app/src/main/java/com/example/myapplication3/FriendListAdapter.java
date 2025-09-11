package com.example.myapplication3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FriendListAdapter extends BaseAdapter {

    private Context context;
    private List<Object> items; // Contact 或 String（好友请求）
    private OnFriendRequestActionListener listener;

    public interface OnFriendRequestActionListener {
        void onAccept(String username);
        void onReject(String username);
    }

    public FriendListAdapter(Context context, List<Object> items, OnFriendRequestActionListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public int getItemViewType(int position) {
        Object obj = items.get(position);
        return (obj instanceof Contact) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() { return 2; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object obj = items.get(position);

        if (getItemViewType(position) == 0) {
            // 联系人
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
            }
            ImageView avatar = convertView.findViewById(R.id.img_avatar);
            TextView name = convertView.findViewById(R.id.tv_name);
            TextView phone = convertView.findViewById(R.id.tv_phone);

            Contact contact = (Contact) obj;
            avatar.setImageResource(contact.getAvatarResId());
            name.setText(contact.getName());
            phone.setText(contact.getPhone());

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

            // 移除旧监听器，防止 convertView 重用问题
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
