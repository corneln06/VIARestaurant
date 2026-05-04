package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.RestaurantTable;

public class AvailableState implements TableState
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
}
