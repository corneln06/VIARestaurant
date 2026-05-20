package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import java.io.Serializable;

public class SeatTableRequest implements Serializable
{
  private int tableId;
  private int waiterId;

  public SeatTableRequest(int tableId, int waiterId){
    this.tableId = tableId;
    this.waiterId = waiterId;
  }

  public int getTableId()
  {
    return tableId;
  }

  public int getWaiterId()
  {
    return waiterId;
  }
}
