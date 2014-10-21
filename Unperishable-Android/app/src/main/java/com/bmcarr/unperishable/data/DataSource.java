package com.bmcarr.unperishable.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bmcarr.unperishable.util.Config;

/**
 * Created by bmc on 10/20/14.
 */
public class DataSource extends SQLiteOpenHelper {

    public DataSource(Context context, String username, int version) {
        super(context, username, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + Config.ITEM_TABLE_NAME + " (" + Config.ITEM_NAME +
                " TEXT PRIMARY KEY, " + Config.ITEM_QUANTITY + " INT, " + Config.ITEM_CATEGORY +
                " INT, " + Config.ITEM_INPUT_DATE + " INT, " + Config.ITEM_EXPIRATION_DATE + " INT," +
                Config.ITEM_OWNER + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
