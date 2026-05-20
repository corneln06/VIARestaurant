package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import java.io.Serializable;

public class SetTableReservedRequest implements Serializable
{
  private int tableId;

  public SetTableReservedRequest(int tableId)
  {
    this.tableId = tableId;
  }

  public int getTableId()
  {
    return tableId;
  }
}
