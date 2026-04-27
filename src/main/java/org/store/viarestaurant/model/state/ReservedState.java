package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.Table;

public class ReservedState implements TableState
{
  public void setAvailable(Table table) {
    table.setState(new AvailableState());
  }
  public void setSeated(Table table){
    table.setState(new SeatedState());
  }
  public void setReserved(Table table){
    System.out.println("Already reserved");
  }
}
