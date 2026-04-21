package org.store.viarestaurant.model.entities;

import org.store.viarestaurant.model.state.AvailableState;
import org.store.viarestaurant.model.state.TableState;

public class Table
{
  private int id;
  private int maxSitting;
  private TableState status;
  private InternalSchedule internalSchedule;


  public Table(int id, int maxSitting ,TableState status){
    this.id = id;
    this.status = new AvailableState();
    this.maxSitting = maxSitting;
   
  }
  public void setState(TableState status){
    this.status = status;
  }
  public int getId()
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
  public void setAvailable(){
    status.setAvailable(this);
  }
  public void setSeated(){
    status.setSeated(this);
  }
  public void setReserved(){
    status.setReserved(this);
  }
}
