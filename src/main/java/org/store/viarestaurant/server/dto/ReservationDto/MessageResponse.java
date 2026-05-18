package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public class MessageResponse implements Serializable
{
  private boolean success;
  private String message;

  public MessageResponse(boolean success, String message){
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
