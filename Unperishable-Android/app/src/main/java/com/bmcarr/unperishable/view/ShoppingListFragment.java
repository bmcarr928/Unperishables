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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 10/27/2014.
 */
public class ShoppingListFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.shopping_list_fragment_layout, container, false);

        ListView theListView = (ListView) view.findViewById(R.id.shoppingListView);
        String [] items = {"invcarrot","dog","cat","carrot1","dog1","cat1","carrot2","dog2","cat2","carrot3","dog3","cat3"};
        List<RowItem> rowItems = new ArrayList<RowItem>();
        for (int i =0; i < items.length; i++){
            rowItems.add(new RowItem(items[i], R.drawable.ic_launcher));
        }
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