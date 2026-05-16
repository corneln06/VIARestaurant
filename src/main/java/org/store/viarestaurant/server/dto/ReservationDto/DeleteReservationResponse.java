package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public class DeleteReservationResponse implements Serializable {

    private final boolean success;
    private final String message;

    public DeleteReservationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}