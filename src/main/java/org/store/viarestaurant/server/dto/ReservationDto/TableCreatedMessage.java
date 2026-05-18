package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.RestaurantTable;

public class TableCreatedMessage
{
  private RestaurantTable table;

  public TableCreatedMessage(RestaurantTable table){
    this.table = table;
  }
  public RestaurantTable getTable()
  {
    return table;
  }
}
