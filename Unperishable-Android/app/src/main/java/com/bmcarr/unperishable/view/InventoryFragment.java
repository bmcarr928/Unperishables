package com.bmcarr.unperishable.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.Config;
import com.bmcarr.unperishable.util.SyncDbTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class InventoryFragment extends Fragment implements Observer {

    private static final String TAG = "InventoryFragment";
    private static final String ITEMLIST = "itemList";
    private ArrayList<Item> itemList;
    private int prevGroup = -1;
    private Thread syncThread;
    private Handler handler;

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

        SyncDbTask sdt = new SyncDbTask(((MainActivity)getActivity()).getDataAccess().getLoggedInUser(),
                ((MainActivity)this.getActivity()).getDataAccess());
        sdt.addObserver(this);
        syncThread = new Thread(sdt);
        syncThread.start();
        Log.d(TAG, "Thread started");

        Bundle args = this.getArguments();
        this.itemList = (ArrayList<Item>) args.getSerializable(ITEMLIST);

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        final ExpandableListView theListView = (ExpandableListView) view.findViewById(R.id.inventory_list);
        List<Item> items = this.itemList;
        theListView.setAdapter(new ExpandableListAdapter());

        theListView.setGroupIndicator(null);


        theListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(prevGroup!= -1){
                    theListView.collapseGroup(prevGroup);
                }
                prevGroup=groupPosition;

            }
        });



        return view;
    }

    @Override
    public void onDetach() {
        syncThread.interrupt();
        super.onDetach();
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        public void testFix(){

        }

        private final LayoutInflater inf;

        public ExpandableListAdapter() {
            inf = LayoutInflater.from(getActivity());
        }

        @Override
        public int getGroupCount() {
            return itemList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return itemList.get(groupPosition);
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
//                TextView  childView = (TextView) convertView.findViewById(R.id.childTextView);
//                childView.setText(stringParceItem(itemList.get(groupPosition)));
                updateButton.setFocusable(false);

                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InventoryFragment.this.getFragmentManager().beginTransaction().replace(R.id.main_panel, EditItem.newInstance((Item) getGroup(groupPosition))).commit();

                    }
                });

                Button deleteButton = (Button) convertView.findViewById(R.id.delete_button);

                deleteButton.setFocusable(false);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Delete")
                                .setMessage("Are you sure you want to delete this entry?")
                                .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        ((MainActivity) getActivity()).getDataAccess().deleteItem((Item)getGroup(groupPosition));
                                        InventoryFragment.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,
                                                InventoryFragment.getInstance(((MainActivity) getActivity()).getDataAccess().queryForAllItems())).commit();
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();




                    }
                });

            }

            return convertView;
        }

        private String stringParceItem(Item item){
            String toReturn = "";
            if(!item.getOwner().equals("")){
                toReturn = "Owner: " + item.getOwner();
            }
            toReturn = toReturn + "\nCategory " + item.getCategory().toString() + "\nInput Date: " + item.getInputDate().toString();
            if(item.getExpirationDate() != null){
                toReturn = toReturn + "Expiration Date " + item.getExpirationDate().toString();
            }
            return toReturn;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_item_parent, parent, false);
                Drawable d;
                if(itemList.get(groupPosition).getQuantity() == Config.Quantity.LOW){
                    d = getResources().getDrawable(R.drawable.yellow_horizontal_gradient);
                }else if (itemList.get(groupPosition).getQuantity() == Config.Quantity.OUT){
                    d = getResources().getDrawable(R.drawable.red_horizontal_gradient);
                } else {
                     d = getResources().getDrawable(R.drawable.green_horizontal_gradient);
                }
                convertView.setBackground(d);
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

    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "Notified");
        MainActivity mainActivity = (MainActivity)this.getActivity();
        mainActivity.runOnUiThread(new viewRefresher(mainActivity));
    }

    private class viewRefresher implements Runnable {

        MainActivity mainActivity;

        viewRefresher(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void run() {
            mainActivity.selectItem(mainActivity.getCurrentPosition());
        }
    }

}


