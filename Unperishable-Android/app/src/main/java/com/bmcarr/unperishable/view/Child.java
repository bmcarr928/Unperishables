package com.bmcarr.unperishable.view;

import com.bmcarr.unperishable.data.Item;

/**
 * Created by jason on 12/2/2014.
 */
public class Child {
    private String info;
    public Child(Item item){
        info = "";
        if(!item.getOwner().equals("")){
            info = "Owner: " + item.getOwner();
        }
        info = info + "\nCategory " + item.getCategory().toString() + "\nInput Date: " + item.getInputDate().toString();
        if(item.getExpirationDate() != null){
            info = info + " Expiration Date: " + item.getExpirationDate().toString();
        }

    }

    public String getInfo(){
        return info;
    }
}
