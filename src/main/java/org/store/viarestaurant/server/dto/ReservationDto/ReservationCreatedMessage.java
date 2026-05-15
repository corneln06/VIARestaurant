package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationCreatedMessage implements Serializable
{
  private String name;
  private LocalDateTime dateTime;
  private int numberOfPeople;
  private RestaurantTable restaurantTable;

  public ReservationCreatedMessage(String customerName, LocalDateTime dateTime, int numberOfPeople, RestaurantTable restaurantTable)
  {
    this.name = customerName;
    this.dateTime = dateTime;
    this.numberOfPeople = numberOfPeople;
    this.restaurantTable = restaurantTable;
  }

  public RestaurantTable getRestaurantTable()
  {
    return restaurantTable;
  }

  public LocalDateTime getDateTime()
  {
    return dateTime;
  }

  public String getName()
  {
    return name;
  }

  public int getNumberOfPeople()
  {
    return numberOfPeople;
  }
}
