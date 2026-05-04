package org.store.viarestaurant;

public class SeatedState implements TableState
{
  public void setAvailable(Table table) {
    table.setState(new AvailableState());
  }
  public void setSeated(Table table){
  }
  public void setReserved(Table table){
    table.setState(new ReservedState());
  }
}
