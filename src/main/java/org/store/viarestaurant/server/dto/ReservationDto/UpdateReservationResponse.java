package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public record UpdateReservationResponse(boolean success, String message) implements Serializable {

}
