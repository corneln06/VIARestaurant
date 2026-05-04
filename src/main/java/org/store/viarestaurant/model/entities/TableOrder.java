package org.store.viarestaurant.model.entities;

// import java.util.ArrayList;

public class TableOrder {
    private final int id;
    private RestaurantTable restaurantTable;
    private Waiter waiter;
    private int partySize;
    // To Be Created, put into constructor, and create the methods
    // private ArrayList<> menuItemsRelationship;
    private String notes;
    private double check;
    private boolean isReservation;

    public TableOrder(int id, RestaurantTable restaurantTable, Waiter waiter, int partySize, String notes, double check) {
        this.id = id;
        this.restaurantTable = restaurantTable;
        this.waiter = waiter;
        this.partySize = partySize;
        this.notes = notes;
        this.check = check;
    }

    public int getId() {
        return id;
    }

    public RestaurantTable getTable() {
        return restaurantTable;
    }

    public void setTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public void setWaiter(Waiter waiter) {
        this.waiter = waiter;
    }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getCheck() {
        return check;
    }

    public void setCheck(double check) {
        this.check = check;
    }

    public boolean isReservation() {
        return isReservation;
    }
}
