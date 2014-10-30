package com.bmcarr.unperishable.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.Item;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {
    public final static String ARG_LIST_NUMBER = "list_number";
    private static final String ITEMLIST = "itemList";
    private ArrayList<Item> itemList;

    public static InventoryFragment getInstance(ArrayList<Item>itemList) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ITEMLIST, itemList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = this.getArguments();
        this.itemList = (ArrayList<Item>) args.getSerializable(ITEMLIST);

        View view = inflater.inflate(R.layout.inventory_fragment_layout, container, false);

        ListView theListView = (ListView) view.findViewById(R.id.inventory_list);
        List<Item> items = this.itemList;

        CustomAdapter adapter = new CustomAdapter(getActivity(), items);
        theListView.setAdapter(adapter);



        // probably change this from on click to on swipe?
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int i, long l){
                // do something
                String itemPicked = "you selected " + String.valueOf(adapterView.getItemAtPosition(i));
                Toast.makeText(getActivity(), itemPicked, Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }
}


