package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public class TableOrderUpdatedMessage implements Serializable
{
  private int tableId;

  public TableOrderUpdatedMessage(int tableId){
    this.tableId = tableId;
  }

  public int getTableId()
  {
    return tableId;
  }
}
