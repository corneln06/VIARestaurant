package org.store.viarestaurant.model.entities;

import java.time.LocalDateTime;

public class Reservation {
    private final int id;
    private String name;
    private LocalDateTime dateTime;
    private int partySize;
    private Table table;

    public Reservation(int id, String name, LocalDateTime dateTime, int partySize, Table table) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.partySize = partySize;
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
}
