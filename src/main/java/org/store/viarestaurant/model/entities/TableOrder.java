package org.store.viarestaurant.model.entities;

import java.util.ArrayList;

public class TableOrder {
    private final int id;
    private Table table;
    private Waiter waiter;
    private int partySize;
    private ArrayList<MenuItems> menuItems;
    private String notes;
    private double check;
    private boolean isReservation;

    public TableOrder(int id, Table table, Waiter waiter, int partySize, ArrayList<MenuItems> menuItems, String notes, double check) {
        this.id = id;
        this.table = table;
        this.waiter = waiter;
        this.partySize = partySize;
        this.menuItems = menuItems;
        this.notes = notes;
        this.check = check;
    }

    public int getId() {
        return id;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
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

    public ArrayList<MenuItems> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<MenuItems> menuItems) {
        this.menuItems = menuItems;
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
