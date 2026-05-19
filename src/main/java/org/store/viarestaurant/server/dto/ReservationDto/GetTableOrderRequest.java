package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.Workers;

import java.io.Serializable;
import java.util.ArrayList;

public class GetTableOrderRequest implements Serializable
{
  private int tableId;

  public GetTableOrderRequest(int tableId)
  {
    this.tableId = tableId;
  }

  public int getTableId()
  {
    return tableId;
  }
}
