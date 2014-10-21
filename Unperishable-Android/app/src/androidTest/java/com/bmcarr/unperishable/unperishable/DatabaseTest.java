package com.bmcarr.unperishable.unperishable;

import com.bmcarr.unperishable.unperishable.data.Config;
import com.bmcarr.unperishable.unperishable.data.DataAccess;
import com.bmcarr.unperishable.unperishable.data.Item;

import junit.framework.TestCase;

public class DatabaseTest extends TestCase {

    public void testStoringItems() {

        Config.deleteDatabase("testing");

        DataAccess dataAccess = new DataAccess(Config.getContext(), "testing");

        Item i1 = new Item("Ketchup", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED);

        if ( ! dataAccess.saveItem(i1) ) fail();

        Item i1again = dataAccess.queryForAllItems().get(0);

        if ( i1again == null ) fail();

        assertEquals( "Item name did not match", "Ketchup", i1again.getName() );

        Config.deleteDatabase("testing");

    }

}