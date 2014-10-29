package com.bmcarr.unperishable;

import com.bmcarr.unperishable.util.Config;
import com.bmcarr.unperishable.data.DataAccess;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.Utilities;

import junit.framework.TestCase;

import java.util.ArrayList;

public class DatabaseTest extends TestCase {

    public void testRetrievingAllItems() {
        DataAccess dataAccess = getDataAccessWithData();

        ArrayList<Item> retrievedItems = dataAccess.queryForAllItems();
        assertEquals( "Size did not match", 6, retrievedItems.size() );

        assertEquals( "Item name did not match", "Ketchup", retrievedItems.get(0).getName() );
        assertEquals( "Item name did not match", "Mustard", retrievedItems.get(1).getName() );
        assertEquals( "Item name did not match", "Mayonnaise", retrievedItems.get(2).getName() );
        assertEquals( "Item name did not match", "Salt", retrievedItems.get(3).getName() );
        assertEquals( "Item name did not match", "Pepper", retrievedItems.get(4).getName() );
        assertEquals( "Item name did not match", "Green Beans", retrievedItems.get(5).getName() );

        cleanUpDataAccess(dataAccess);
    }

    public void testRetrievingByCategory() {
        DataAccess dataAccess = getDataAccessWithData();

        ArrayList<Item> refrigeratorItems = dataAccess.queryForItemsOfCategory(Config.Category.REFRIGERATOR);
        ArrayList<Item> pantryItems = dataAccess.queryForItemsOfCategory(Config.Category.PANTRY);
        ArrayList<Item> spiceItems = dataAccess.queryForItemsOfCategory(Config.Category.SPICE);

        assertEquals( "Quantity of refrigerator items did not match", 3, refrigeratorItems.size());
        assertEquals( "Quantity of pantry items did not match", 1, pantryItems.size());
        assertEquals( "Quantity of spice items did not match", 2, spiceItems.size());

        assertEquals( "Item name did not match", "Ketchup", refrigeratorItems.get(0).getName() );
        assertEquals( "Item name did not match", "Mustard", refrigeratorItems.get(1).getName() );
        assertEquals( "Item name did not match", "Mayonnaise", refrigeratorItems.get(2).getName() );
        assertEquals( "Item name did not match", "Green Beans", pantryItems.get(0).getName() );
        assertEquals( "Item name did not match", "Salt", spiceItems.get(0).getName() );
        assertEquals( "Item name did not match", "Pepper", spiceItems.get(1).getName() );

        cleanUpDataAccess(dataAccess);
    }

    public void testRetrievingByQuantity() {
        DataAccess dataAccess = getDataAccessWithData();

        ArrayList<Item> stockedItems = dataAccess.queryForItemsOfQuantity(Config.Quantity.STOCKED);
        ArrayList<Item> lowItems = dataAccess.queryForItemsOfQuantity(Config.Quantity.LOW);
        ArrayList<Item> outItems = dataAccess.queryForItemsOfQuantity(Config.Quantity.OUT);

        assertEquals( "Quantity of stocked items did not match", 4, stockedItems.size());
        assertEquals( "Quantity of low items did not match", 1, lowItems.size());
        assertEquals( "Quantity of out items did not match", 1, outItems.size());

        assertEquals( "Item name did not match", "Ketchup", stockedItems.get(0).getName() );
        assertEquals( "Item name did not match", "Mustard", stockedItems.get(1).getName() );
        assertEquals( "Item name did not match", "Mayonnaise", stockedItems.get(2).getName() );
        assertEquals( "Item name did not match", "Green Beans", stockedItems.get(3).getName() );
        assertEquals( "Item name did not match", "Salt", lowItems.get(0).getName() );
        assertEquals( "Item name did not match", "Pepper", outItems.get(0).getName() );

        cleanUpDataAccess(dataAccess);
    }

    public void testRetrievingWithConstraints() {
        DataAccess dataAccess = getDataAccessWithData();

        String steve = "Steve";

        ArrayList<Item> itemsNamedKetchup = dataAccess.queryForItemsWithConstraints(
                "name = ?", "Ketchup");
        ArrayList<Item> itemsInsertedBeforeRightNow = dataAccess.queryForItemsWithConstraints(
                "input_date < ?", String.valueOf(System.currentTimeMillis()));
        ArrayList<Item> itemsOwnedBySteve = dataAccess.queryForItemsWithConstraints(
                "owner = ?", steve);

        assertEquals( "Quantity of ketchup-named items did not match", 1, itemsNamedKetchup.size());
        assertEquals( "Quantity of items inserted before right now items did not match", 6, itemsInsertedBeforeRightNow.size());
        assertEquals( "Quantity of Steve's items did not match", 3, itemsOwnedBySteve.size());

        assertEquals( "Item name did not match", "Ketchup", itemsNamedKetchup.get(0).getName() );
        assertEquals( "Item name did not match", "Ketchup", itemsInsertedBeforeRightNow.get(0).getName() );
        assertEquals( "Item name did not match", "Mustard", itemsInsertedBeforeRightNow.get(1).getName() );
        assertEquals( "Item name did not match", "Mayonnaise", itemsInsertedBeforeRightNow.get(2).getName() );
        assertEquals( "Item name did not match", "Salt", itemsInsertedBeforeRightNow.get(3).getName() );
        assertEquals( "Item name did not match", "Pepper", itemsInsertedBeforeRightNow.get(4).getName() );
        assertEquals( "Item name did not match", "Green Beans", itemsInsertedBeforeRightNow.get(5).getName() );
        assertEquals( "Item name did not match", "Ketchup", itemsOwnedBySteve.get(0).getName() );
        assertEquals( "Item name did not match", "Salt", itemsOwnedBySteve.get(1).getName() );
        assertEquals( "Item name did not match", "Green Beans", itemsOwnedBySteve.get(2).getName() );

        cleanUpDataAccess(dataAccess);
    }

    private DataAccess getDataAccessWithData() {
        DataAccess dataAccess = new DataAccess(Utilities.getContext(), "testing");

        Item i0 = new Item("Ketchup", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED)
                .withOwner("Steve");
        Item i1 = new Item("Mustard", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED);
        Item i2 = new Item("Mayonnaise", Config.Category.REFRIGERATOR, Config.Quantity.STOCKED);
        Item i3 = new Item("Salt", Config.Category.SPICE, Config.Quantity.LOW)
                .withOwner("Steve");
        Item i4 = new Item("Pepper", Config.Category.SPICE, Config.Quantity.OUT);
        Item i5 = new Item("Green Beans", Config.Category.PANTRY, Config.Quantity.STOCKED)
                .withOwner("Steve");

        if ( ! dataAccess.saveItem(i0) ) fail();
        if ( ! dataAccess.saveItem(i1) ) fail();
        if ( ! dataAccess.saveItem(i2) ) fail();
        if ( ! dataAccess.saveItem(i3) ) fail();
        if ( ! dataAccess.saveItem(i4) ) fail();
        if ( ! dataAccess.saveItem(i5) ) fail();
        return dataAccess;
    }

    private void cleanUpDataAccess(DataAccess dataAccess) {
        String databaseName = dataAccess.getLoggedInUser();

        Utilities.deleteDatabase(databaseName);
    }

}