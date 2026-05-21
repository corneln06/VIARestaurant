package org.store.viarestaurant.server.dto.MenuItemDTO;

import java.io.Serializable;
import java.util.List;

import org.store.viarestaurant.model.entities.MenuItems;



public class GetMenuItemsResponse implements Serializable{
  private static final long serialVersionUID = 1L;
  private final List<MenuItems> menuItems;

  public GetMenuItemsResponse(List<MenuItems> menuItems){
    this.menuItems = menuItems;
  }

  public List<MenuItems> getMenuItems(){
    return menuItems;
  }


}
