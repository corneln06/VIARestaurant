package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;

public class TableCreatedMessage implements Serializable
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
