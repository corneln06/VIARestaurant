package org.store.viarestaurant.model.state;

import org.store.viarestaurant.model.entities.Table;

public interface TableState
{
  void setAvailable(Table table);
  void setSeated(Table table);
  void setReserved(Table table);

}
