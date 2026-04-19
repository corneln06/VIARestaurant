package org.store.viarestaurant;

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
  public void setReservedSeated(Table table){
    table.setState(new ReservedSeatedState());
  }
}
