package com.bmcarr.unperishable.unperishable.data;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

public class Config {

    private static String TAG = "Config";
    private static Context context;

    // Table name
    public static final String ITEM_TABLE_NAME = "kitchen_store";

    // Attribute names
    public static final String ITEM_NAME = "name";
    public static final String ITEM_QUANTITY = "quantity";
    public static final String ITEM_CATEGORY = "category";
    public static final String ITEM_INPUT_DATE = "input_date";
    public static final String ITEM_EXPIRATION_DATE = "expiration_date";
    public static final String ITEM_OWNER = "owner";

    public enum Category {
        REFRIGERATOR(0),
        PANTRY(1),
        SPICE(2);

        int id;

        Category(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Category getCategory(int id) {
            for ( Category c : Category.values() ) {
                if ( c.getId() == id ) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Integer " + id + " does not map to a Category");
        }
    }

    public enum Quantity {
        OUT(0),
        LOW(1),
        STOCKED(2);

        int id;

        Quantity(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Quantity getQuantity(int id) {
            for ( Quantity q : Quantity.values() ) {
                if ( q.getId() == id ) {
                    return q;
                }
            }
            throw new IllegalArgumentException("Integer " + id + " does not map to a Quantity");
        }
    }

    /**
     * Returns the android storage directory
     * @return A file pointing to the location where the database files are stored
     */
    public static Context getContext(Context context) {
        if( context == null ) {
            try {
                final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                final Method method = activityThreadClass.getMethod("currentApplication");
                context = (Context) method.invoke(null, (Object[]) null);
                return context;
            } catch( Exception e ) {
                Log.e(TAG, "Error while attempting to retrieve current application", e);
                return null;
            }
        } else {
            return context;
        }
    }

    public static Context getContext() {
        if( Config.context != null ) {
            return Config.context;
        } else {
            return getContext(null);
        }
    }

    public static void deleteDatabase(String dbName) {
        getContext().deleteDatabase(dbName);
    }


}
