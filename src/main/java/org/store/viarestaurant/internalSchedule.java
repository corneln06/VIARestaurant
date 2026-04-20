package org.store.viarestaurant;

import java.security.Timestamp;

public class internalSchedule
{
  private Timestamp timestamp;

  public internalSchedule(Timestamp timestamp){
    this.timestamp = timestamp;
  }

  public Timestamp getTimestamp()
  {
    return timestamp;
  }
}
