package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.Table;

public class AvailableState implements TableState
{
  public void setAvailable(Table table) {
    System.out.print("already available");
  }
  public void setSeated(Table table){
    table.setState(new SeatedState());
  }
  public void setReserved(Table table){
    table.setState(new ReservedState());
  }
}
