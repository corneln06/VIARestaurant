package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;
import java.time.LocalDateTime;

public record UpdateReservationRequest(int id,
                                       String name,
                                       LocalDateTime dateTime,
                                       int partySize,
                                       RestaurantTable table) implements Serializable {

}
