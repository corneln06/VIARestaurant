package org.store.viarestaurant;

public class AvailableState implements TableState
{
  public void setAvailable(Table table) {
  }
  public void setSeated(Table table){
    table.setState(new SeatedState());
  }
  public void setReserved(Table table){
    table.setState(new ReservedState());
  }
}
