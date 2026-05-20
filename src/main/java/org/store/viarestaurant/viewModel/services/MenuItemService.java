package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.model.enums.MenuTypes;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.MenuItemDTO.*;
import org.store.viarestaurant.server.dto.MessageResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MenuItemService
{
  private final Client client;

  public MenuItemService(Client client)
  {
    this.client = client;
  }

  public void loadMenuItems() throws IOException
  {
    client.send(new GetMenuItemsRequest());
  }

  public void onMenuItemsLoaded(Consumer<GetMenuItemsResponse> listener)
  {
    client.setGetMenuItemsListener(listener);
  }

  public void createMenuItem(String name, MenuTypes type, double price,
      boolean isVegetarian, ArrayList<String> allergies)
      throws IOException
  {
    client.send(new CreateMenuItemRequest(name, type, price, isVegetarian, allergies));
  }

  public void updateMenuItem(int id, String name, MenuTypes type, double price,
      boolean isVegetarian, ArrayList<String> allergies)
      throws IOException
  {
    client.send(new UpdateMenuItemRequest(id, name, type, price, isVegetarian, allergies));
  }

  public void deleteMenuItem(int id) throws IOException
  {
    client.send(new DeleteMenuItemRequest(id));
  }

  public void onMenuItemCreated(Consumer<CreateMenuItemResponse> listener)
  {
    client.setMenuItemCreatedListener(listener);
  }

  public void onMenuItemUpdated(Consumer<MessageResponse> listener)
  {
    client.setUpdateMenuItemListener(listener);
  }

  public void onMenuItemDeleted(Consumer<MessageResponse> listener)
  {
    client.setDeleteMenuItemListener(listener);
  }
}