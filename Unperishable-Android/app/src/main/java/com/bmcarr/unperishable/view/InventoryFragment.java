package com.bmcarr.unperishable.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryFragment extends Fragment {
    private static final String ITEMLIST = "itemList";
    private ArrayList<Item> itemList;

    public static InventoryFragment getInstance(ArrayList<Item>itemList) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        // added sort
        Collections.sort(itemList);
        args.putSerializable(ITEMLIST, itemList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = this.getArguments();
        this.itemList = (ArrayList<Item>) args.getSerializable(ITEMLIST);

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        final ExpandableListView theListView = (ExpandableListView) view.findViewById(R.id.inventory_list);
        List<Item> items = this.itemList;

        theListView.setAdapter(new ExpandableListAdapter(items));
        theListView.setGroupIndicator(null);




        return view;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private List<Item> items;

        public ExpandableListAdapter(List<Item> items) {
            this.items = items;
            inf = LayoutInflater.from(getActivity());
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return "child";
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_item_child, parent, false);
                Button updateButton = (Button) convertView.findViewById(R.id.update_button);

                updateButton.setFocusable(false);

                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InventoryFragment.this.getFragmentManager().beginTransaction().replace(R.id.main_panel, EditItem.newInstance(items.get(groupPosition))).commit();

                    }
                });

            }

            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_item_parent, parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.item_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(getGroup(groupPosition).toString());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class ViewHolder {
            TextView text;
        }
    }

}


