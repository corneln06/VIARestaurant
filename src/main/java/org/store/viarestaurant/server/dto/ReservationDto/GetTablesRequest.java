package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;
import java.util.ArrayList;

public class GetTablesRequest implements Serializable
{
  private ArrayList<RestaurantTable> tables;

  public GetTablesRequest(ArrayList<RestaurantTable> tables){
    this.tables = tables;
  }

  public ArrayList<RestaurantTable> getTables()
  {
    return tables;
  }
}
