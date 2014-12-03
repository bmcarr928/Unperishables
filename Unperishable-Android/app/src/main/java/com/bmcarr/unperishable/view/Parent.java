package com.bmcarr.unperishable.view;

import com.bmcarr.unperishable.data.Item;

import java.io.Serializable;

/**
 * Created by jason on 12/2/2014.
 */
public class Parent implements Serializable{
    private Item item;
    private Child child;
    public Parent(Item item, Child child){
        this.item = item;
        this.child = child;
    }

    public String getName(){
        return item.getName();
    }
    public Child getChild(){
        return child;
    }
    public Item getItem(){return item;}
}
