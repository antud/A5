package com.example.a5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListItemAdapter extends ArrayAdapter<ListItem> {
    private ArrayList<ListItem> dataList;

    ListItemAdapter(Context context, int resource, ArrayList<ListItem> objects) {
        super(context, resource, objects);
        dataList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ListItem currentItem = getItem(position);
        ImageView currentImage = convertView.findViewById(R.id.image_in_list);
        TextView currentName = convertView.findViewById(R.id.text_in_list);

        if (currentItem.getImageResource() != 0) {
            //old way
            currentImage.setImageResource(currentItem.getImageResource());
        } else if (currentItem.getImageBitmap() != null) {
            // new way
            currentImage.setImageBitmap(currentItem.getImageBitmap());
        }

        currentName.setText(currentItem.getTagText());
        return convertView;
    }

    public void updateData(ArrayList<ListItem> newData) {
        dataList.clear();
        dataList.addAll(newData);
        notifyDataSetChanged();
    }
}
