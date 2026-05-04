package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.RestaurantTable;

public class ReservedState implements TableState
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
}
