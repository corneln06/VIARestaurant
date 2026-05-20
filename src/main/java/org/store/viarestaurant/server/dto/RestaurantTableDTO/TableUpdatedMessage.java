package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import java.io.Serializable;

public class TableUpdatedMessage implements Serializable
{
  private int tableId;

  public TableUpdatedMessage(int tableId)
  {
    this.tableId = tableId;
  }

  public int getTableId()
  {
    return tableId;
  }
}
