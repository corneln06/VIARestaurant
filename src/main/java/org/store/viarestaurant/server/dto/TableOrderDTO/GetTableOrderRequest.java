package org.store.viarestaurant.server.dto.TableOrderDTO;

import java.io.Serializable;

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
