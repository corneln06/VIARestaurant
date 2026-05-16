package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public class TableBookingResponse implements Serializable
{
  private boolean success;
  private String message;

  public TableBookingResponse(boolean success, String message){
    this.success = success;
    this.message = message;
  }

  public String getMessage()
  {
    return message;
  }

  public boolean isSuccess()
  {
    return success;
  }
}
