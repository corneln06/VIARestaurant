package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public class ReservationDeletedMessage implements Serializable {

    private final int reservationId;

    public ReservationDeletedMessage(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getReservationId() {
        return reservationId;
    }
}