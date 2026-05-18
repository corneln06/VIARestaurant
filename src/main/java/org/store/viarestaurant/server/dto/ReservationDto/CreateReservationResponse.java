package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public class CreateReservationResponse implements Serializable
{
  private boolean success;
  private String message;


  public CreateReservationResponse(boolean success, String message){
    this.success = success;
    this.message = message;
  }

  public boolean isSuccess()
  {
    return success;
  }

  public String getMessage()
  {
    return message;
  }
}
