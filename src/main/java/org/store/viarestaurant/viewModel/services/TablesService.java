package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.GetTablesRequest;
import org.store.viarestaurant.server.dto.ReservationDto.GetTablesResponse;
import org.store.viarestaurant.server.dto.ReservationDto.TableCreateRequest;

import java.io.IOException;
import java.util.function.Consumer;

public class TablesService
{
  private final Client client;

  public TablesService(Client client){
    this.client = client;
  }
  public void loadTables() throws IOException
  {
    client.send(new GetTablesRequest());
  }
  public void createTable(TableCreateRequest request) throws IOException{
    client.send(request);
  }
  public void onTablesLoaded(Consumer<GetTablesResponse> listener){
    client.setTablesPageListener(listener);;
  }

}
