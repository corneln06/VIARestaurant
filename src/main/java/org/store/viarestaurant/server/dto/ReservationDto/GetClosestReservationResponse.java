package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.Reservation;

import java.io.Serializable;

public class GetClosestReservationResponse implements Serializable {

    private Reservation reservation;

    public GetClosestReservationResponse(Reservation reservation){
        this.reservation = reservation;
    }

    public Reservation getReservation(){
        return reservation;
    }
}
