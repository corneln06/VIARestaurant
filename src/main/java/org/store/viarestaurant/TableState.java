package org.store.viarestaurant;

public interface TableState
{
  void setAvailable(Table table);
  void setSeated(Table table);
  void setReserved(Table table);
  void setReservedSeated(Table table);
}
