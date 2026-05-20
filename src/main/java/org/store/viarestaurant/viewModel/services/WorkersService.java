package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.model.enums.WorkerRole;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.MessageResponse;
import org.store.viarestaurant.server.dto.WorkersDTO.CreateWorkerRequest;
import org.store.viarestaurant.server.dto.WorkersDTO.CreateWorkerResponse;
import org.store.viarestaurant.server.dto.WorkersDTO.DeleteWorkerRequest;
import org.store.viarestaurant.server.dto.WorkersDTO.GetWorkersRequest;
import org.store.viarestaurant.server.dto.WorkersDTO.GetWorkersResponse;
import org.store.viarestaurant.server.dto.WorkersDTO.UpdateWorkerRequest;

import java.io.IOException;
import java.util.function.Consumer;

public class WorkersService
{
  private final Client client;

  public WorkersService(Client client)
  {
    this.client = client;
  }

  public void loadWorkers() throws IOException
  {
    client.send(new GetWorkersRequest());
  }

  public void createWorker(String firstName, String lastName, String email, String password, WorkerRole role) throws IOException
  {
    client.send(new CreateWorkerRequest(firstName, lastName, email, password, role));
  }

  public void deleteWorker(int id) throws IOException
  {
    client.send(new DeleteWorkerRequest(id));
  }

  public void updateWorker(int id, String firstName, String lastName, String email, WorkerRole role) throws IOException
  {
    client.send(new UpdateWorkerRequest(id, firstName, lastName, email, role));
  }

  public void onWorkersLoaded(Consumer<GetWorkersResponse> listener)
  {
    client.setWorkersListener(listener);
  }

  public void onWorkerCreated(Consumer<CreateWorkerResponse> listener)
  {
    client.setWorkerCreatedListener(listener);
  }

  public void onWorkerDeleted(Consumer<MessageResponse> listener)
  {
    client.setDeleteWorkerListener(listener);
  }

  public void onWorkerUpdated(Consumer<MessageResponse> listener)
  {
    client.setUpdateWorkerListener(listener);
  }
}