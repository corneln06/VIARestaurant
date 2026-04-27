package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.Table;

public class SeatedState implements TableState
{
  public void setAvailable(Table table) {
    table.setState(new AvailableState());
  }
  public void setSeated(Table table){
    System.out.println("already seated");
  }
  public void setReserved(Table table){
    table.setState(new ReservedState());
  }
}
