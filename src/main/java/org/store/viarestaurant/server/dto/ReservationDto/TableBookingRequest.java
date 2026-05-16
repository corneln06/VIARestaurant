package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TableBookingRequest implements Serializable
{
  private final String name;
  private final LocalDateTime dateTime;
  private final int partySize;
  private final RestaurantTable restaurantTable;

  public TableBookingRequest(String name, LocalDateTime dateTime ,int partySize, RestaurantTable restaurantTable){
    this.name = name;
    this.dateTime = dateTime;
    this.partySize = partySize;
    this.restaurantTable = restaurantTable;
  }

  public String getName()
  {
    return name;
  }

  public LocalDateTime getDateTime()
  {
    return dateTime;
  }

  public int getPartySize()
  {
    return partySize;
  }

  public RestaurantTable getRestaurantTable()
  {
    return restaurantTable;
  }
}
