package com.bmcarr.unperishable.unperishable.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.sql.Date;
import java.util.ArrayList;

public class DataAccess {

    private static final int VERSION = 1;

    private String loggedInUser;

    private DataSource dataSource;
    private SQLiteDatabase db;

    public DataAccess(Context context, String username) {
        this.dataSource = new DataSource(context, username, VERSION);
        this.db = dataSource.getWritableDatabase();
        this.loggedInUser = username;
    }

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

    public ArrayList<Item> queryForAllItems() {
        db.beginTransaction();
        String query = "SELECT * FROM " + Config.ITEM_TABLE_NAME;
        Cursor queryResult = db.rawQuery(query,null);
        db.endTransaction();

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
                queryResult.moveToNext();
            } while (queryResult.moveToNext());
        }

        return itemList;
    }

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
}
