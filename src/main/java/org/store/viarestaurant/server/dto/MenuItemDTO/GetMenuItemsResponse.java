package org.store.viarestaurant.server.dto.MenuItemDTO;

import org.store.viarestaurant.model.entities.MenuItems;

import java.io.Serializable;
import java.util.List;

public class GetMenuItemsResponse implements Serializable
{
  private final List<MenuItems> menuItems;

  public GetMenuItemsResponse(List<MenuItems> menuItems)
  {
    this.menuItems = menuItems;
  }

  public List<MenuItems> getMenuItems()
  {
    return menuItems;
  }
}