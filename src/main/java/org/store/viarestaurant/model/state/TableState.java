package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.RestaurantTable;

public interface TableState
{
  void setAvailable(RestaurantTable restaurantTable);
  void setSeated(RestaurantTable restaurantTable);
  void setReserved(RestaurantTable restaurantTable);

}
