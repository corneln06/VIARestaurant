package org.store.viarestaurant;

public class ReservedSeatedState implements TableState
{
  public void setAvailable(Table table) {
    table.setState(new AvailableState());
  }
  public void setSeated(Table table){
    table.setState(new SeatedState());
  }
  public void setReserved(Table table){
    table.setState(new ReservedState());
  }
  public void setReservedSeated(Table table){
    System.out.println("Already Reserved and Seated");
  }
}
