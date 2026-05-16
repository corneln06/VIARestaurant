package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;

public class ReservedState implements TableState, Serializable
{
  public void setAvailable(RestaurantTable restaurantTable) {
    restaurantTable.setState(new AvailableState());
  }
  public void setSeated(RestaurantTable restaurantTable){
    restaurantTable.setState(new SeatedState());
  }
  public void setReserved(RestaurantTable restaurantTable){
    System.out.println("Already reserved");
  }

  @Override
  public String getName() {
    return "Reserved";
  }
}
