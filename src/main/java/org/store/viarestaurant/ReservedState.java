package org.store.viarestaurant;

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
