package com.bmcarr.unperishable.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.DataAccess;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.Config;

public class MainActivity extends Activity implements AddItem.OnFragmentInteractionListener {

    private String[] mDrawerArray;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DataAccess dataAccess;

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerArray = getResources().getStringArray(R.array.option_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerArray));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        getFragmentManager().beginTransaction().add(R.id.main_panel, new LoginFragment()).commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.addItem) {

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.main_panel, new AddItem()).commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//        // update seleOnFragmentInteractionListenercted item and title, then close the drawer
//        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);


        Fragment frag = InventoryFragment.getInstance(this.dataAccess.queryForAllItems());
        /*Bundle args = new Bundle();*/
        /*args.putInt(InventoryFragment.ARG_LIST_NUMBER, position);
        frag.setArguments(args);*/
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().add(R.id.main_panel, frag).commit();
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerArray[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void loginUser(String username) {
        this.dataAccess = new DataAccess(this.getApplicationContext(), username);

        this.dataAccess.saveItem(new Item("Ketchup", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED)
                .withOwner("Steve"));

        InventoryFragment inventoryFragment = InventoryFragment.getInstance(this.dataAccess.queryForAllItems());
        getFragmentManager().beginTransaction().replace(R.id.main_panel, inventoryFragment,"inventoryFragment").commit();
    }

}