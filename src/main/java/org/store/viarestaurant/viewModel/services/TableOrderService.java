package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.GetTableOrderRequest;
import org.store.viarestaurant.server.dto.ReservationDto.GetTableOrderResponse;

import java.io.IOException;
import java.util.function.Consumer;

public class TableOrderService
{
  private final Client client;

  public TableOrderService(Client client){
    this.client = client;
  }
  public void loadTableOrder(int tableId) throws IOException
  {
    client.send(new GetTableOrderRequest(tableId));
  }
  public void populateComboBoxForTables(Consumer<GetTableOrderResponse> listener){
    client.setTableOrderListener(listener);
  }
}
