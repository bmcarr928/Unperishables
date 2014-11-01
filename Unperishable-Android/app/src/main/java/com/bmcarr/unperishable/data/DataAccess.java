package com.bmcarr.unperishable.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.bmcarr.unperishable.util.Config;

import java.sql.Date;
import java.util.ArrayList;

/**
 * This class provides methods to create, insert, update and delete objects to and from the
 * database.
 */
public class DataAccess {

    // Used by the DataSource to determine whether an update is necessary
    private static final int VERSION = 1;

    private String loggedInUser;

    private DataSource dataSource;
    private SQLiteDatabase db;

    public DataAccess(Context context, String username) {
        this.dataSource = new DataSource(context, username, VERSION);
        this.db = dataSource.getWritableDatabase();
        this.loggedInUser = username;
    }

    /**
     * Saves the given item to the database. It will either insert or update, based on the item's
     * previous existence in the database
     *
     * @param item Item to be saved
     * @return true, if item was saved successfully, false otherwise
     */
    public boolean saveItem (Item item){
        db.beginTransaction();
        String query = String.format("SELECT " + Config.ITEM_NAME + " FROM " + Config.ITEM_TABLE_NAME + " WHERE " +
                Config.ITEM_NAME + " = \"%s\"", item.getName());
        Cursor queryResult = db.rawQuery(query, null);
        db.endTransaction();
        if (queryResult.getCount()==0) {
            return insertItem(item);
        } else {
            return updateItem(item);
        }
    }

    public boolean deleteItem(Item item){
        db.beginTransaction();
        String query = String.format("DELETE" +" FROM " + Config.ITEM_TABLE_NAME + " WHERE " +
                Config.ITEM_NAME + " = \"%s\"", item.getName());
        Cursor queryResult = db.rawQuery(query, null);
        db.endTransaction();
        if (queryResult.getCount()==0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Returns all items in the database
     *
     * @return ArrayList consisting of all items that have been saved to the user's database
     */
    public ArrayList<Item> queryForAllItems() {
        db.beginTransaction();
        String query = "SELECT * FROM " + Config.ITEM_TABLE_NAME;
        Cursor queryResult = db.rawQuery(query, null);
        db.endTransaction();

        ArrayList<Item> itemList = getItemsFromCursor(queryResult);

        return itemList;
    }

    /**
     * Returns all items in the database that conform to a certain constraint, and substitutes
     * the ordered varargs list of substitutions into the query where the '?' character exists
     *
     * I.e. queryForItemsWithConstraints("owner = ?", person1.getName()) will return all Items
     * whose owner's name is the same as the result of the method call person1.getName()
     *
     * @param constraint statement that reduces to boolean value to be tested against items in the
     * database
     * @param substitutions varargs list of substitutions to replace '?' characters in the
     * constraint with
     * @return ArrayList of items that fit the constraint
     */
    public ArrayList<Item> queryForItemsWithConstraints(String constraint, String... substitutions) {
        db.beginTransaction();
        String query = "SELECT * FROM " + Config.ITEM_TABLE_NAME + " WHERE " + constraint;
        Cursor queryResult = db.rawQuery(query, substitutions);
        db.endTransaction();

        return getItemsFromCursor(queryResult);
    }

    /**
     * Returns all items from a given Category
     *
     * @param category Category of items to retrieve from the database
     * @return ArrayList of items from the given category
     */
    public ArrayList<Item> queryForItemsOfCategory(Config.Category category) {
        db.beginTransaction();
        String query = "SELECT * FROM " + Config.ITEM_TABLE_NAME + " WHERE " + Config.ITEM_CATEGORY +
                " = " + category.getId();
        Cursor queryResult = db.rawQuery(query, null);
        db.endTransaction();

        ArrayList<Item> itemList = getItemsFromCursor(queryResult);

        return itemList;
    }

    /**
     * Returns an items from the database
     *
     * @param String string name of  item to retrieve from the database
     * @return Item(object) from the database
     */
    public Item queryForItemOfName(String itemName) {
        db.beginTransaction();
        String query = "SELECT * FROM " + Config.ITEM_TABLE_NAME + " WHERE " + Config.ITEM_NAME +
                " = \"" + itemName + "\"";
        Cursor queryResult = db.rawQuery(query, null);
        db.endTransaction();

        ArrayList<Item> itemList = getItemsFromCursor(queryResult);
        if(itemList.size() == 0){
            return null;
        } else {
            return itemList.get(0);
        }
    }

    /**
     * Returns all items from a given Quantity
     *
     * @param quantity Quantity of items to retrieve from the database
     * @return ArrayList of items from the given quantity
     */
    public ArrayList<Item> queryForItemsOfQuantity(Config.Quantity quantity) {
        db.beginTransaction();
        String query = "SELECT * FROM " + Config.ITEM_TABLE_NAME + " WHERE " + Config.ITEM_QUANTITY +
                " = " + quantity.getId();
        Cursor queryResult = db.rawQuery(query, null);
        db.endTransaction();

        ArrayList<Item> itemList = getItemsFromCursor(queryResult);

        return itemList;
    }

    /**
     * Retrieves the items from the Cursor result of a database query
     *
     * @param queryResult Cursor object returned from a database query
     * @return ArrayList of items whose information is contained in the Cursor object
     */
    private ArrayList<Item> getItemsFromCursor(Cursor queryResult) {
        ArrayList<Item> itemList = new ArrayList<Item>();

        if ( queryResult.moveToFirst() && queryResult.getCount() >= 1) {
            do {
                String name = queryResult.getString(queryResult.getColumnIndex(Config.ITEM_NAME));
                int quantity = queryResult.getInt(queryResult.getColumnIndex(Config.ITEM_QUANTITY));
                int category = queryResult.getInt(queryResult.getColumnIndex(Config.ITEM_CATEGORY));

                long inputDate = queryResult.getLong(queryResult.getColumnIndex(Config.ITEM_INPUT_DATE));
                long expirationDate = queryResult.getLong(queryResult.getColumnIndex(Config.ITEM_EXPIRATION_DATE));
                String owner = queryResult.getString(queryResult.getColumnIndex(Config.ITEM_OWNER));

                Item item = new Item(name, Config.Category.getCategory(category), Config.Quantity.
                        getQuantity(quantity)).withInputDate(new Date(inputDate));

                if (expirationDate != 0) {
                    item = item.withExpirationDate(new Date(expirationDate));
                }

                if (owner != null) {
                    item = item.withOwner(owner);
                }

                itemList.add(item);
            } while (queryResult.moveToNext());
        }
        return itemList;
    }

    /**
     * Inserts an item into the database
     *
     * @param item Item to be inserted
     * @return true, if successful, false otherwise
     */
    private boolean insertItem(Item item) {
        db.beginTransaction();
        ContentValues cv = getContentValues(item);

        try {
            this.db.insert(Config.ITEM_TABLE_NAME, null, cv);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            return false;
        } finally {
            db.endTransaction();
        }

        return true;
    }

    /**
     * Updates an Item record in the database
     *
     * @param item Item to be updated
     * @return true, if successful, false otherwise
     */
    private boolean updateItem(Item item) {
        db.beginTransaction();
        try {
            String[] whereArgs = {item.getName()};
            ContentValues cv = getContentValues(item);
            if(db.update(Config.ITEM_TABLE_NAME, cv, "name=?", whereArgs) == 0){
                return false;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return true;
    }

    /**
     * Translates an Item's fields into a ContentValues object to be used in a SQL query
     *
     * @param item Item to be translated into a ContentValues object
     * @return ContentValues representation of an Item
     */
    private ContentValues getContentValues(Item item) {
        ContentValues cv = new ContentValues();

        cv.put(Config.ITEM_NAME, item.getName());
        cv.put(Config.ITEM_CATEGORY, item.getCategory().getId());
        cv.put(Config.ITEM_QUANTITY, item.getQuantity().getId());

        if ( item.getInputDate() == null ) {
            cv.put(Config.ITEM_INPUT_DATE, System.currentTimeMillis());
        } else {
            cv.put(Config.ITEM_INPUT_DATE, item.getInputDate().getTime());
        }

        if ( item.getExpirationDate() != null ) {
            cv.put(Config.ITEM_EXPIRATION_DATE, item.getExpirationDate().getTime());
        } else {
            cv.putNull(Config.ITEM_EXPIRATION_DATE);
        }

        if ( item.getOwner() != null ) {
            cv.put(Config.ITEM_OWNER, item.getOwner());
        } else {
            cv.putNull(Config.ITEM_OWNER);
        }
        return cv;
    }

    /**
     * @return Username of the logged in user
     */
    public String getLoggedInUser() {
        return this.loggedInUser;
    }

}
