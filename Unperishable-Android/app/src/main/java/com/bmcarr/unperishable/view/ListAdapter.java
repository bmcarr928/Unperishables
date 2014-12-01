package com.bmcarr.unperishable.view;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by fav on 11/30/14.
 */
public class ListAdapter extends ArrayAdapter<String> {
    private static int SEPERATOR = 8;
    private String [] str;
    public ListAdapter(Context context, int resource, String [] items) {
        super(context, resource, items);
        str = items;
    }


    @Override
    public boolean areAllItemsEnabled (){
        return false;
    }

    @Override
    public boolean isEnabled (int position){
        if (position == SEPERATOR)
            return false;

        return true;
    }
}
