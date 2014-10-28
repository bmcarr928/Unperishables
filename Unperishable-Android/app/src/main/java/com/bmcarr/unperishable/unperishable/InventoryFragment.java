package com.bmcarr.unperishable.unperishable;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by jason on 10/27/2014.
 */
public class InventoryFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.invintory_fragment_layout, container, false);

        String [] items = {"carrot","dog","cat","carrot1","dog1","cat1","carrot2","dog2","cat2","carrot3","dog3","cat3"};
        ArrayAdapter<String> theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        ListView theListView = (ListView) view.findViewById(R.id.invlistView);

        theListView.setAdapter(theAdapter);
//
//
//        // probably change this from on click to on swipe?
//        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick( AdapterView<?> adapterView, View view, int i, long l){
//                // do something
//                String itemPicked = "you selected " + String.valueOf(adapterView.getItemAtPosition(i));
//                Toast.makeText(getActivity(), itemPicked, Toast.LENGTH_SHORT).show();
//            }
//        });



        return view;
    }
}
