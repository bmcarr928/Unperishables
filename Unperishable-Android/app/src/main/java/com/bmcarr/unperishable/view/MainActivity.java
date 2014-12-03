package com.bmcarr.unperishable.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.DataAccess;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.Config;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity implements AddItem.OnFragmentInteractionListener, EditItem.OnFragmentInteractionListener {

    private String[] mDrawerArray;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DataAccess dataAccess;
    private int currentPosition;

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO change this when merging with drawer stuff
        this.currentPosition = 2;

        mDrawerArray = getResources().getStringArray(R.array.option_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ListAdapter(this,
                R.layout.drawer_list_item, mDrawerArray));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());



        getFragmentManager().beginTransaction().add(R.id.main_panel, new LoginFragment()).commit();
        mDrawerLayout.setDrawerLockMode(1);


    }




    public DataAccess getDataAccess() {
        return dataAccess;
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity.this.currentPosition = position;
            selectItem(position);
        }
    }

    /*
    * selectItem (int position) is a private method called by the DrawerItemClickListener class.
    * This method handles selections placed on the Drawer. A switch statement is used to determine
    * which drawer was selected and the view is changed to display appropriately.
    * There is a warning about getActionBar.setTitle(mDrawerArray[position]) possibly producing an
    * error. This array is specific for the drawerlayout and as long as not changed no exceptions
    * should be thrown. Suppressed warnings for this reason.
    * */

     @SuppressWarnings("ConstantConditions")
     public void selectItem(int position) {
        FragmentManager fragmentManager;
        this.currentPosition = position;

        switch (position){


             case 0: fragmentManager = getFragmentManager();  //Grocery List
                     fragmentManager.beginTransaction().replace(R.id.main_panel, InventoryFragment.getInstance(generateGroceryList())).commit();
                     drawerListSelect(position);
                      break;
             case 1: fragmentManager = getFragmentManager();  //All Items
                     ArrayList<Item> itemList = this.dataAccess.queryForAllItems();
                     Collections.sort(itemList);
                     fragmentManager.beginTransaction().replace(R.id.main_panel, InventoryFragment.getInstance(itemList)).commit();
                     drawerListSelect(position);
                     break;
             case 2: fragmentManager = getFragmentManager();  //Refrigerator
                     fragmentManager.beginTransaction().replace(R.id.main_panel,
                             InventoryFragment.getInstance(this.dataAccess.queryForItemsOfCategory(Config.Category.REFRIGERATOR))).commit();
                     drawerListSelect(position);
                     break;
             case 3: fragmentManager = getFragmentManager();  //Pantry
                     fragmentManager.beginTransaction().replace(R.id.main_panel,
                             InventoryFragment.getInstance(this.dataAccess.queryForItemsOfCategory(Config.Category.PANTRY))).commit();
                     drawerListSelect(position);
                     break;
             case 4: fragmentManager = getFragmentManager();  //Spices
                     fragmentManager.beginTransaction().replace(R.id.main_panel,
                             InventoryFragment.getInstance(this.dataAccess.queryForItemsOfCategory(Config.Category.SPICE))).commit();
                     drawerListSelect(position);
                     break;
             case 5: fragmentManager = getFragmentManager();  //Out
                     fragmentManager.beginTransaction().replace(R.id.main_panel,
                             InventoryFragment.getInstance(this.dataAccess.queryForItemsOfQuantity(Config.Quantity.OUT))).commit();
                     drawerListSelect(position);
                     break;
             case 6: fragmentManager = getFragmentManager();  //Low
                     fragmentManager.beginTransaction().replace(R.id.main_panel,
                             InventoryFragment.getInstance(this.dataAccess.queryForItemsOfQuantity(Config.Quantity.LOW))).commit();
                     drawerListSelect(position);
                     break;
             case 7: fragmentManager = getFragmentManager();  //Stocked
                     fragmentManager.beginTransaction().replace(R.id.main_panel,
                             InventoryFragment.getInstance(this.dataAccess.queryForItemsOfQuantity(Config.Quantity.STOCKED))).commit();
                     drawerListSelect(position);
                     break;
            case 9: fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_panel, new AddItem()).commit();
                    drawerListSelect(position);
                    break;
            case 10: fragmentManager = getFragmentManager();
                     fragmentManager.beginTransaction().replace(R.id.main_panel, new LoginFragment()).commit();
                     getActionBar().setTitle(R.string.app_name);
                     mDrawerLayout.setDrawerLockMode(1);
        }
    }
    /*
    * drawerListSelect method is the action method for when an item is selected in the drawer. This method takes one argument
    * the position where the item was selected and performs the action of setting the item as selected, resetting the title and
    * closing the drawer. This is private method called by selectItem.
    * */
    private void drawerListSelect(int position) {
        mDrawerList.setItemChecked(position,true);
        mDrawerList.setSelection(position);
        getActionBar().setTitle(mDrawerArray[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /*
    * Thought that grocery list was supposed to display items that are both out and low. Didn't think we had
    * a query that did this. if we do just delete this, if we don't I think we should. Didn't want to mess with data base queries.
    * so created this method to generate my grocery list using two queryForItemsOfQuantity queries.
    * */
    private ArrayList<Item> generateGroceryList() {
        ArrayList<Item> groceryList = new ArrayList<Item>();
        ArrayList<Item> temp1 = this.dataAccess.queryForItemsOfQuantity(Config.Quantity.LOW);
        ArrayList<Item> temp2 = this.dataAccess.queryForItemsOfQuantity(Config.Quantity.OUT);
        for (Item x : temp1) {
            groceryList.add(x);
        }
        for (Item x : temp2) {
            groceryList.add(x);
        }
        return groceryList;
    }

    public void loginUser(String username) {

        this.dataAccess = new DataAccess(this.getApplicationContext(), username);
        Config.currentDataAccess = this.dataAccess;

        InventoryFragment inventoryFragment = InventoryFragment.getInstance(this.dataAccess.queryForAllItems());
        getFragmentManager().beginTransaction().replace(R.id.main_panel, inventoryFragment,"inventoryFragment").commit();
        getActionBar().setTitle(R.string.all_items);
        mDrawerLayout.setDrawerLockMode(0);
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

}