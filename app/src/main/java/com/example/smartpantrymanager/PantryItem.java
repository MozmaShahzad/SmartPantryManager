package com.example.smartpantrymanager;

//a simple data model which represents one pantry ingredient
//it is just a Java object we use
//to carry oantry data around the app

public class PantryItem {
    public int id;
    public String name;
    public double quantity;
    public String unit;
    public String expiryDate;

    //constructor - runs when we create a new Pantry object
    public PantryItem(int id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
}

