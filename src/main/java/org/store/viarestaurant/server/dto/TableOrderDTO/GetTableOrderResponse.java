package org.store.viarestaurant.server.dto.TableOrderDTO;

import java.io.Serializable;

public class GetTableOrderResponse implements Serializable
{
  private GetTableOrderRequest tableOrder;

  public GetTableOrderResponse(GetTableOrderRequest tableOrder)
  {
    this.tableOrder = tableOrder;
  }

  public GetTableOrderRequest getTableOrder()
  {
    return tableOrder;
  }
}
