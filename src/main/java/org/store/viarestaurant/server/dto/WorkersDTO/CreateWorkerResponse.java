package org.store.viarestaurant.server.dto.WorkersDTO;

import org.store.viarestaurant.model.entities.Workers;

import java.io.Serializable;

public class CreateWorkerResponse implements Serializable
{
  private final Workers worker;

  public CreateWorkerResponse(Workers worker)
  {
    this.worker = worker;
  }

  public Workers getWorker()
  {
    return worker;
  }
}