package com.bmcarr.unperishable.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

        final ListView theListView = (ListView) view.findViewById(R.id.inventory_list);
        List<Item> items = this.itemList;

        CustomAdapter adapter = new CustomAdapter(getActivity(), items);
        theListView.setAdapter(adapter);



        // probably change this from on click to on swipe?
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int position, long l){
                // currently only opens EditItem fragment
                InventoryFragment.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,
                        EditItem.newInstance((Item) adapterView.getItemAtPosition(position))).commit();
                getActivity().getActionBar().setTitle(R.string.edit_item);
            }
        });



        return view;
    }
}


