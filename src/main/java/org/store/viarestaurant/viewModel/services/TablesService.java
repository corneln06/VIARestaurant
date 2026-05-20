package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.RestaurantTableDTO.*;

import java.io.IOException;
import java.util.function.Consumer;

public class TablesService
{
  private final Client client;

  public TablesService(Client client){
    this.client = client;
  }
  public void loadTables()
  {
    try {
      client.send(new GetTablesRequest());
    } catch (IOException e)
    {
      System.out.println("Something Wrong happened");
    }
  }
  public void createTable(TableCreateRequest request) throws IOException{
    client.send(request);
  }

  public void onTablesLoaded(Consumer<GetTablesResponse> listener){
    client.setTablesPageListener(listener);
  }

  public void onTablesCreated(Consumer<TableCreateResponse> listener) {
    client.setCreateTableListener(listener);
  }

  public void onTablesCreatedResponse(Consumer<TableCreatedMessage> listener) {
    client.setTablesCreateListener(listener);
  }
}
