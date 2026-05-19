package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.GetWorkersRequest;
import org.store.viarestaurant.server.dto.ReservationDto.GetWorkersResponse;

import java.io.IOException;
import java.util.function.Consumer;

public class WorkersService
{
  private final Client client;

  public WorkersService(Client client){
    this.client = client;
  }
  public void loadWorkers() throws IOException
  {
    client.send(new GetWorkersRequest());
  }

  public void onWorkersLoaded(
      Consumer<GetWorkersResponse> listener)
  {
    client.setWorkersListener(listener);
  }
}
