package org.store.viarestaurant.server.dto.ReservationDto;

import org.store.viarestaurant.model.entities.TableOrder;

import java.io.Serializable;

public class GetTableOrderResponse implements Serializable
{
  private TableOrder tableOrder;

  public GetTableOrderResponse(TableOrder tableOrder)
  {
    this.tableOrder = tableOrder;
  }

  public TableOrder getTableOrder()
  {
    return tableOrder;
  }
}
