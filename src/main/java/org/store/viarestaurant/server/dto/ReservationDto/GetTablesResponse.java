package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;
import java.util.ArrayList;

public class GetTablesResponse implements Serializable
{
  private ArrayList<RestaurantTable> tables;

  public GetTablesResponse(ArrayList<RestaurantTable> tables)
  {
    this.tables = tables;
  }

  public ArrayList<RestaurantTable> getTables()
  {
    return tables;
  }
}
