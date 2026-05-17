package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UpdateReservationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private LocalDateTime dateTime;
    private int partySize;
    private RestaurantTable table;

    public UpdateReservationRequest(int id,
                                    String name,
                                    LocalDateTime dateTime,
                                    int partySize,
                                    RestaurantTable table) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.partySize = partySize;
        this.table = table;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getPartySize() {
        return partySize;
    }

    public RestaurantTable getTable() {
        return table;
    }
}