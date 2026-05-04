package org.store.viarestaurant.model.entities;

import java.security.Timestamp;

public class InternalSchedule
{
  private Timestamp timestamp;

  public InternalSchedule(Timestamp timestamp){
    this.timestamp = timestamp;
  }

  public Timestamp getTimestamp()
  {
    return timestamp;
  }
}
