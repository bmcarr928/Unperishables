package com.bmcarr.unperishable;

import com.bmcarr.unperishable.util.Config;
import com.bmcarr.unperishable.data.DataAccess;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.Utilities;

import junit.framework.TestCase;

public class DatabaseTest extends TestCase {

    public void testStoringItems() {

        DataAccess dataAccess = new DataAccess(Utilities.getContext(), "testing");

        Item i1 = new Item("Ketchup", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED);

        if ( ! dataAccess.saveItem(i1) ) fail();

        Item i1again = dataAccess.queryForAllItems().get(0);

        if ( i1again == null ) fail();

        assertEquals( "Item name did not match", "Ketchup", i1again.getName() );

        Utilities.deleteDatabase("testing");

    }

}