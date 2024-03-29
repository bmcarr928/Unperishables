package com.bmcarr.unperishable.util;

import com.bmcarr.unperishable.data.DataAccess;

/**
 * Configuration class that holds constants and enums that will be used in more than one file
 */
public class Config {

    private static String TAG = "Config";
    public static DataAccess currentDataAccess;

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

        public String getName() {
            if ( id == Category.REFRIGERATOR.getId() ) return "REFRIGERATOR";
            if ( id == Category.PANTRY.getId() ) return "PANTRY";
            if ( id == Category.SPICE.getId() ) return "SPICE";
            return "";
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

}
