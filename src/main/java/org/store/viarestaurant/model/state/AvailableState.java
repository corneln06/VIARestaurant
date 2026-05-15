package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;

public class AvailableState implements TableState, Serializable
{
  public void setAvailable(RestaurantTable restaurantTable) {
    System.out.print("already available");
  }
  public void setSeated(RestaurantTable restaurantTable){
    restaurantTable.setState(new SeatedState());
  }
  public void setReserved(RestaurantTable restaurantTable){
    restaurantTable.setState(new ReservedState());
  }

  @Override
  public String getName() {
    return "Available";
  }
}
