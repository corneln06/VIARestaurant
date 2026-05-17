package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public class DeleteReservationRequest implements Serializable {

    private int reservationId;

    public DeleteReservationRequest(int reservationId) {
        this.reservationId = reservationId;

    }

    public int getReservationId() {
        return reservationId;
    }
}