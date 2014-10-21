package com.bmcarr.unperishable.unperishable.data;

import com.bmcarr.unperishable.unperishable.data.Config.Category;
import com.bmcarr.unperishable.unperishable.data.Config.Quantity;

import java.sql.Date;

public class Item {

    private String name;
    private Category category;
    private Quantity quantity;

    private Date inputDate = null;
    private String owner = null;
    private Date expirationDate = null;

    public Item(String name, Category category, Quantity quantity) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
    }

    public Item withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public Item withExpirationDate(Date date) {
        this.expirationDate = date;
        return this;
    }

    public Item withInputDate(Date date) {
        this.inputDate = date;
        return this;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public String getOwner() {
        return owner;
    }

    public Date getInputDate() {
        return inputDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}
