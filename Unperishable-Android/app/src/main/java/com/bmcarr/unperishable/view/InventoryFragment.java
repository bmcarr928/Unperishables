package com.bmcarr.unperishable.view;

import android.app.Activity;
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
import com.bmcarr.unperishable.util.AddItemTask;
import com.bmcarr.unperishable.util.Config;
import com.bmcarr.unperishable.util.DeleteItemTask;
import com.bmcarr.unperishable.util.SyncDbTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by jason on 12/2/2014.
 */

public class InventoryFragment extends Fragment implements Observer {

    private static final String TAG = "InventoryFragment";
    private static final String ITEMLIST = "itemList";
    private ArrayList<Item> itemList;
    private int prevGroup = -1;
    private Thread syncThread;

    public static InventoryFragment getInstance(ArrayList<Item> itemList){
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();

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

        final ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.inventory_list);
        // create groups

        listView.setAdapter(new CustomExpandableListAdapter(getActivity(),createGroups()));
        listView.setGroupIndicator(null);

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(prevGroup!= -1){
                    listView.collapseGroup(prevGroup);
                }
                prevGroup=groupPosition;
            }
        });


        return view;
    }

    private ArrayList<Parent> createGroups(){
        ArrayList<Parent> toReturn = new ArrayList<Parent>();
        for (int i = 0; i < itemList.size(); i++) {
            toReturn.add(new Parent(itemList.get(i), new Child(itemList.get(i))));
        }

        return toReturn;

    }
    @Override
    public void onDetach() {
        syncThread.interrupt();
        super.onDetach();
    }


    private class CustomExpandableListAdapter extends BaseExpandableListAdapter {
        private ArrayList<Parent> parents;
        private Activity activity;

        public CustomExpandableListAdapter(Activity activity, ArrayList<Parent> parents){
            this.parents = parents;
            this.activity = activity;
        }

        @Override
        public int getGroupCount() {
            return parents.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return parents.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return parents.get(groupPosition).getChild();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                convertView = inflater.inflate(R.layout.list_item_parent, parent, false);
                Drawable d;
                if(itemList.get(groupPosition).getQuantity() == Config.Quantity.LOW){
                    d = getResources().getDrawable(R.drawable.yellow_horizontal_gradient);
                }else if (itemList.get(groupPosition).getQuantity() == Config.Quantity.OUT){
                    d = getResources().getDrawable(R.drawable.red_horizontal_gradient);
                } else {
                    d = getResources().getDrawable(R.drawable.green_horizontal_gradient);
                }
                convertView.setBackground(d);
                TextView textView = (TextView) convertView.findViewById(R.id.item_name);
                textView.setText(parents.get(groupPosition).getName());


            }
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
            if (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(activity);
                convertView = inflater.inflate(R.layout.list_item_child, parent, false);

            }
                TextView childView = (TextView) convertView.findViewById(R.id.childTextView);

                childView.setText(parents.get(groupPosition).getChild().getInfo());

            Button updateButton = (Button) convertView.findViewById(R.id.update_button);
            updateButton.setFocusable(false);

        updateButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
            InventoryFragment.this.getFragmentManager().beginTransaction().replace(R.id.main_panel, EditItem.newInstance(parents.get(groupPosition).getItem())).commit();

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
                    DeleteItemTask deleteItemTask = new DeleteItemTask(Config.currentDataAccess.getLoggedInUser(), parents.get(groupPosition).getItem());
                    Thread t = new Thread(deleteItemTask);
                    t.start();
                    ((MainActivity) getActivity()).getDataAccess().deleteItem(parents.get(groupPosition).getItem());
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
            .show();   }
            });

            return convertView;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
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
