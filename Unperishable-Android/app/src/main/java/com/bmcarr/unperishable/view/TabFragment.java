package com.bmcarr.unperishable.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmcarr.unperishable.R;

/**
 * Created by jason on 10/27/2014.
 */
public class TabFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_layout, container, false);
//
//        Fragment invFrag = new InventoryFragment();
//
//        FragmentTransaction invFragTrans = getFragmentManager().beginTransaction().add(R.id.main_panel, invFrag);
//        invFragTrans.commit();
//
//        Button invButton = (Button) view.findViewById(R.id.inventory_button);
//
//        invButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment invFrag = new InventoryFragment();
//
//                FragmentTransaction invFragTrans = getFragmentManager().beginTransaction().replace(R.id.main_panel, invFrag);
//                invFragTrans.commit();
//            }
//        });
//
//        Button shopButton = (Button) view.findViewById(R.id.shopping_list_button);
//
//        shopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment shoppingFrag = new ShoppingListFragment();
//                FragmentTransaction shoppingFragTrans = getFragmentManager().beginTransaction().replace(R.id.main_panel, shoppingFrag);
//                shoppingFragTrans.commit();
//            }
//        });
        return view;
    }

}
