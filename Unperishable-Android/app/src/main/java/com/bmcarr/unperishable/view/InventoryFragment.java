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
import com.bmcarr.unperishable.data.DataAccess;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.Config;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    private static final String USERNAME = "username";

    private DataAccess dataAccess;

    public static InventoryFragment getInstance(String username) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String username = this.getArguments().getString(USERNAME);

        this.dataAccess = new DataAccess(this.getActivity().getApplicationContext(), username);

        dataAccess.saveItem(new Item("Ketchup", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED));
        dataAccess.saveItem(new Item("Mustard", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED));
        dataAccess.saveItem(new Item("Bacon", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED));

        View view = inflater.inflate(R.layout.inventory_fragment_layout, container, false);

        ListView theListView = (ListView) view.findViewById(R.id.invlistView);
        List<Item> rowItems = dataAccess.queryForAllItems();

        CustomAdapter adapter = new CustomAdapter(getActivity(), rowItems);
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


