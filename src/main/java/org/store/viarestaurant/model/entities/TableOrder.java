package org.store.viarestaurant.model.entities;

// import java.util.ArrayList;

import javafx.concurrent.Worker;

public class TableOrder {
    private final int id;
    private RestaurantTable restaurantTable;
    private Workers waiter;
    private String notes;
    private double bill;
    private boolean isReservation;
    private boolean isPaid;

    public TableOrder(int id, RestaurantTable restaurantTable, Workers waiter, String notes, double bill, boolean isReservation) {
        this.id = id;
        this.restaurantTable = restaurantTable;
        this.waiter = waiter;
        this.notes = notes;
        this.bill = bill;
        this.isReservation = isReservation;
        this.isPaid = false;
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

    public Workers getWaiter() {
        return waiter;
    }

    public void setWaiter(Workers waiter) {
        this.waiter = waiter;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double check) {
        this.bill = check;
    }

    public boolean isReservation() {
        return isReservation;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
