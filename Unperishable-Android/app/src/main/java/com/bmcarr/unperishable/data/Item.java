package com.bmcarr.unperishable.data;

import com.bmcarr.unperishable.util.Config;

import java.io.Serializable;
import java.sql.Date;

/**
 * This class represents the items associated with the kitchen inventory and grocery lists. Provides
 * a clean and simple API that employs a builder pattern for adding non-required attributes to
 * an Item
 */
public class Item implements Serializable{

    private String name;
    private Config.Category category;
    private Config.Quantity quantity;

    private Date inputDate = null;
    private String owner = null;
    private Date expirationDate = null;

    /**
     * Basic constructor that includes the three required attributes of an Item
     *
     * @param name name of the Item
     * @param category category of the Item
     * @param quantity quantity of the Item
     */
    public Item(String name, Config.Category category, Config.Quantity quantity) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
    }

    /**
     * Builds on an object by adding an 'owner' attribute
     *
     * @param owner String name of the owner of an object
     * @return this item with an 'owner' attribute
     *
     * new Item(name, category, quantity).withOwner(owner);
     */
    public Item withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Builds on an object by adding an 'expirationDate' attribute
     *
     * @param date Date object representing the time that an object expires
     * @return this item with an 'expirationDate' attribute
     *
     * new Item(name, category, quantity).withExpirationDate(date)
     */
    public Item withExpirationDate(Date date) {
        this.expirationDate = date;
        return this;
    }

    /**
     * Builds on an object by adding an 'inputDate' attribute
     *
     * @param date Date object representing the time that an object was inserted/purchased
     * @return this item with an 'inputDate' attribute
     */
    public Item withInputDate(Date date) {
        this.inputDate = date;
        return this;
    }

    /**
     * @return String name of an Item
     */
    public String getName() {
        return name;
    }

    /**
     * @return Category of an Item
     */

    public Config.Category getCategory() {
        return category;
    }

    /**
     * @return Quantity of an Item
     */

    public Config.Quantity getQuantity() {
        return quantity;
    }

    /**
     * @return String owner of an Item
     */

    public String getOwner() {
        return owner;
    }

    /**
     * @return Date object representing the input date of an Item
     */

    public Date getInputDate() {
        return inputDate;
    }

    /**
     * @return Date object representing the expiration date of an Item
     */

    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
