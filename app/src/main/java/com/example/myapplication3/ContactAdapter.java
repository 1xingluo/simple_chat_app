package com.example.myapplication3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends BaseAdapter {

    private Context context;
    private List<Contact> contactList;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvPhone = convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhone());

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvPhone;
    }
}
