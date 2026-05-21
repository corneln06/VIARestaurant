package org.store.viarestaurant.server.dto.MenuItemDTO;

import org.store.viarestaurant.model.entities.MenuItems;
import java.io.Serializable;

public class CreateMenuItemResponse implements Serializable
{
  private final MenuItems menuItem;

  public CreateMenuItemResponse(MenuItems menuItem)
  {
    this.menuItem = menuItem;
  }

  public MenuItems getMenuItem() { return menuItem; }
}