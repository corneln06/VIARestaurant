package org.store.viarestaurant.server.dto.WorkersDTO;

import org.store.viarestaurant.model.entities.Workers;

import java.io.Serializable;
import java.util.ArrayList;

public class GetWorkersResponse implements Serializable
{
  private ArrayList<Workers> workers;

  public GetWorkersResponse(ArrayList<Workers> workers)
  {
    this.workers = workers;
  }

  public ArrayList<Workers> getWorkers()
  {
    return workers;
  }
}
