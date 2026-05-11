package org.store.viarestaurant.model.entities;

import org.store.viarestaurant.model.state.AvailableState;
import org.store.viarestaurant.model.state.TableState;

public class RestaurantTable
{
  private Integer id;
  private int maxSitting;
  private TableState status;
  private InternalSchedule internalSchedule;


  public RestaurantTable(Integer id, int maxSitting){
    this.id = id;
    this.status = new AvailableState();
    this.maxSitting = maxSitting;
  }
  public void setState(TableState status){
    this.status = status;
  }
  public Integer getId()
  {
    return id;
  }

  public int getMaxSitting()
  {
    return maxSitting;
  }

  public TableState getStatus()
  {
    return status;
  }
}
