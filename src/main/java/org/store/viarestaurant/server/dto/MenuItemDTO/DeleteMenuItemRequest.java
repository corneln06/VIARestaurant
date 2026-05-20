package org.store.viarestaurant.server.dto.MenuItemDTO;

import java.io.Serializable;

public class DeleteMenuItemRequest implements Serializable
{
  private final int id;

  public DeleteMenuItemRequest(int id)
  {
    this.id = id;
  }

  public int getId() { return id; }
}