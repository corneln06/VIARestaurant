package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import java.io.Serializable;

public class SetTableAvailableRequest implements Serializable
{
  private int tableId;

  public SetTableAvailableRequest(int tableId)
  {
    this.tableId = tableId;
  }

  public int getTableId()
  {
    return tableId;
  }
}
