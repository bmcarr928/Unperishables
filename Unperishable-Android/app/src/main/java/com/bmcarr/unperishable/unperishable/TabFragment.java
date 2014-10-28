package com.bmcarr.unperishable.unperishable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by jason on 10/27/2014.
 */
public class TabFragment extends Fragment {

    Fragment invFrag;
    FragmentTransaction fragTrans;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_layout, container, false);


        Button invButton = (Button) view.findViewById(R.id.inventory_button);

        invButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invFrag = new InventoryFragment();

                fragTrans = getFragmentManager().beginTransaction().replace(R.id.main_panel, invFrag);
                fragTrans.commit();
            }
        });

        Button shopButton = (Button) view.findViewById(R.id.shopping_list_button);

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invFrag = new ShoppingListFragment();
                fragTrans = getFragmentManager().beginTransaction().replace(R.id.main_panel, invFrag);
                fragTrans.commit();
            }
        });
        return view;
    }

}
