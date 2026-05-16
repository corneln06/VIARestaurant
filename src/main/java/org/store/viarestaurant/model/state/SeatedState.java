package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.RestaurantTable;

import java.io.Serializable;

public class SeatedState implements TableState, Serializable
{
  public void setAvailable(RestaurantTable restaurantTable) {
    restaurantTable.setState(new AvailableState());
  }
  public void setSeated(RestaurantTable restaurantTable){
    System.out.println("already seated");
  }
  public void setReserved(RestaurantTable restaurantTable){
    restaurantTable.setState(new ReservedState());
  }

  @Override
  public String getName() {
      return "Seated";
  }
}
