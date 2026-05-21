package org.store.viarestaurant.server.dto.WorkersDTO;

import java.io.Serializable;

public class DeleteWorkerRequest implements Serializable
{
  private final int id;

  public DeleteWorkerRequest(int id)
  {
    this.id = id;
  }

  public int getId()
  {
    return id;
  }
}