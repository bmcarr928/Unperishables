package com.bmcarr.unperishable.view;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.DataAccess;


public class MainActivity extends Activity implements AddItem.OnFragmentInteractionListener {

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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


}
