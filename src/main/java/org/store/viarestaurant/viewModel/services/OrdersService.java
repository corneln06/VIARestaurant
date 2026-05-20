package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.MenuItemsDTO.GetMenuItemsRequest;
import org.store.viarestaurant.server.dto.MenuItemsDTO.GetMenuItemsResponse;
import org.store.viarestaurant.server.dto.TableOrderDTO.AddItemsToOrderRequest;
import org.store.viarestaurant.server.dto.TableOrderDTO.AddItemsToOrderResponse;
import org.store.viarestaurant.server.dto.TableOrderDTO.GetOrdersByWaiterRequest;
import org.store.viarestaurant.server.dto.TableOrderDTO.GetOrdersByWaiterResponse;
import org.store.viarestaurant.server.dto.TableOrderDTO.PayOrderRequest;
import org.store.viarestaurant.server.dto.TableOrderDTO.PayOrderResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class OrdersService
{
  private final Client client;

  public OrdersService(Client client)
  {
    this.client = client;
  }

  public void loadMenuItems()
  {
    try
    {
      client.send(new GetMenuItemsRequest());
    }
    catch (IOException e)
    {
      System.out.println("OrdersService: failed to load menu items");
    }
  }

  public void loadOrdersByWaiter(int waiterId)
  {
    try
    {
      client.send(new GetOrdersByWaiterRequest(waiterId));
    }
    catch (IOException e)
    {
      System.out.println("OrdersService: failed to load orders");
    }
  }

  public void addItemsToOrder(int tableOrderId, ArrayList<String> itemNames)
  {
    try
    {
      client.send(new AddItemsToOrderRequest(tableOrderId, itemNames));
    }
    catch (IOException e)
    {
      System.out.println("OrdersService: failed to add items to order");
    }
  }

  public void onMenuItemsLoaded(Consumer<GetMenuItemsResponse> listener)
  {
    client.setMenuItemsListener(listener);
  }

  public void onOrdersByWaiterLoaded(Consumer<GetOrdersByWaiterResponse> listener)
  {
    client.setWaiterOrdersListener(listener);
  }

  public void onItemsAdded(Consumer<AddItemsToOrderResponse> listener)
  {
    client.setAddItemsListener(listener);
  }

  public void payOrder(int tableOrderId)
  {
    try
    {
      client.send(new PayOrderRequest(tableOrderId));
    }
    catch (IOException e)
    {
      System.out.println("OrdersService: failed to pay order");
    }
  }

  public void onOrderPaid(Consumer<PayOrderResponse> listener)
  {
    client.setPayOrderListener(listener);
  }
}
