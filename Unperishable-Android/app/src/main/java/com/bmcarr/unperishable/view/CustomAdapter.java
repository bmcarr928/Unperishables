package com.bmcarr.unperishable.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.Config;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<Item> items;

    CustomAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;

    }

    @Override
    public int getCount() {

        return items.size();
    }

    @Override
    public Object getItem(int position) {

        return items.get(position);
    }

    @Override
    public long getItemId(int position) {

        return items.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCategory = (TextView) convertView.findViewById(R.id.category);

        Item itemForPosition = items.get(position);
        // setting the image resource and title
//        imgIcon.setImageResource(row_pos.getIcon());
        txtTitle.setText(itemForPosition.getName());
        txtTitle.setTextColor(Color.BLACK);
        txtCategory.setText(itemForPosition.getCategory().getName());
        txtCategory.setTextColor(Color.BLACK);

        if ( itemForPosition.getQuantity() == Config.Quantity.OUT ) {
            convertView.setBackgroundColor(Color.RED);
        } else if ( itemForPosition.getQuantity() == Config.Quantity.LOW ) {
            convertView.setBackgroundColor(Color.YELLOW);
        } else {

            convertView.setBackgroundColor(Color.GREEN);
        }

        return convertView;

    }

}