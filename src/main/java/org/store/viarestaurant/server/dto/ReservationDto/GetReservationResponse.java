package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.Reservation;

import java.io.Serializable;
import java.util.ArrayList;

public class GetReservationResponse implements Serializable
{
  private ArrayList<Reservation> reservations;

  public GetReservationResponse(ArrayList<Reservation> reservations){
    this.reservations = reservations;
  }

  public ArrayList<Reservation> getReservations()
  {
    return reservations;
  }
}
