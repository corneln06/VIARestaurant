package org.store.viarestaurant.server;

import javafx.application.Platform;
import org.store.viarestaurant.server.dto.AllergyDTO.CreateAllergyResponse;
import org.store.viarestaurant.server.dto.AllergyDTO.GetAllergiesResponse;
import org.store.viarestaurant.server.dto.LoginDTO.LoginResponse;
import org.store.viarestaurant.server.dto.MenuItemDTO.CreateMenuItemResponse;
import org.store.viarestaurant.server.dto.MenuItemDTO.GetMenuItemsResponse;
import org.store.viarestaurant.server.dto.MessageResponse;
import org.store.viarestaurant.server.dto.ReservationDto.*;
import org.store.viarestaurant.server.dto.RestaurantTableDTO.*;
import org.store.viarestaurant.server.dto.TableOrderDTO.AddItemsToOrderResponse;
import org.store.viarestaurant.server.dto.TableOrderDTO.GetOrdersByWaiterResponse;
import org.store.viarestaurant.server.dto.TableOrderDTO.GetTableOrderResponse;
import org.store.viarestaurant.server.dto.WorkersDTO.CreateWorkerResponse;
import org.store.viarestaurant.server.dto.TableOrderDTO.PayOrderResponse;
import org.store.viarestaurant.server.dto.ReservationDto.*;
import org.store.viarestaurant.server.dto.RestaurantTableDTO.*;
import org.store.viarestaurant.server.dto.WorkersDTO.GetWorkersResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Client
{
  private Consumer<TableCreateResponse> createTableListener;
  private Socket socket;
  private ObjectOutputStream outToServer;
  private ObjectInputStream inFromServer;

  private Consumer<LoginResponse> loginListener;

  private Consumer<GetTablesResponse> reservationTablesListener;
  private List<Consumer<GetReservationsResponse>> reservationsListener = new ArrayList<>();
  private Consumer<MessageResponse> createReservationListener;
  private Consumer<ReservationCreatedMessage> reservationCreatedListener;
  private Consumer<GetClosestReservationResponse> closestReservationListener;

  private Consumer<DeleteReservationResponse> deleteReservationListener;
  private Consumer<UpdateReservationResponse> updateReservationListener;
  private Consumer<ReservationDeletedMessage> reservationDeletedListener;

  private List<Consumer<GetTablesResponse>> tablesPageListener = new ArrayList<>();
  private Consumer<TableCreatedMessage> tableCreatedListener;
  private Consumer<TableUpdatedMessage> updatedMessageListener;
  private Consumer<UpdateTableResponse> updateTableListener;
  private Consumer<DeleteTableResponse> deleteTableListener;

  private Consumer<GetWorkersResponse> workersListener;

  private Consumer<GetTableOrderResponse> tableOrderResponseListener;
  private Consumer<CreateAllergyResponse> createdAllergyListener;
  private Consumer<GetAllergiesResponse> getAllergiesListener;
  private Consumer<MessageResponse> updateAllergyListener;
  private Consumer<MessageResponse> deleteAllergyListener;
  private Consumer<GetMenuItemsResponse> getMenuItemsListener;
  private Consumer<MessageResponse> updateMenuItemListener;
  private Consumer<MessageResponse> deleteMenuItemListener;
  private Consumer<CreateMenuItemResponse> createdMenuItemListener;
  private Consumer<CreateWorkerResponse> workerCreatedListener;
  private Consumer<MessageResponse> deleteWorkerListener;
  private Consumer<MessageResponse> updateWorkerListener;

  private List<Consumer<GetMenuItemsResponse>>      menuItemsListeners    = new ArrayList<>();
  private List<Consumer<GetOrdersByWaiterResponse>> waiterOrdersListeners = new ArrayList<>();
  private List<Consumer<AddItemsToOrderResponse>>   addItemsListeners     = new ArrayList<>();
  private Consumer<PayOrderResponse>                payOrderListener;

  public void connect() throws IOException
  {
    socket = new Socket("localhost", 2910);

    outToServer = new ObjectOutputStream(socket.getOutputStream());
    outToServer.flush();

    inFromServer = new ObjectInputStream(socket.getInputStream());

    startListening();
  }

  public void send(Object object) throws IOException
  {
    outToServer.writeObject(object);
    outToServer.flush();
  }

  public void startListening()
  {
    Thread listenerThread = new Thread(() ->
    {
      try
      {
        while(true)
        {
          Object object = inFromServer.readObject();

          Platform.runLater(() ->
          {
            if(object instanceof LoginResponse response && loginListener != null)
            {
              loginListener.accept(response);
            }
            else if(object instanceof GetTablesResponse response && tablesPageListener != null)
            {
              if(reservationTablesListener != null)
              {
                reservationTablesListener.accept(response);
              }

              for (Consumer<GetTablesResponse> responseConsumer : tablesPageListener){
                responseConsumer.accept(response);
              }
            }
            else if(object instanceof GetReservationsResponse response && reservationsListener != null)
            {

              for (Consumer<GetReservationsResponse> reservationsResponseConsumer : reservationsListener){
                reservationsResponseConsumer.accept(response);
              }
            }
            else if(object instanceof MessageResponse response && createReservationListener != null)
            {
              createReservationListener.accept(response);
            }
            else if(object instanceof ReservationCreatedMessage message && reservationCreatedListener != null)
            {
              reservationCreatedListener.accept(message);
            }
            else if (object instanceof TableCreateResponse message && createTableListener != null)
            {
              createTableListener.accept(message);
            } else if (object instanceof TableCreatedMessage message && tableCreatedListener != null)
            {
              tableCreatedListener.accept(message);
            }
            else if (object instanceof UpdateTableResponse response && updateTableListener != null)
            {
              updateTableListener.accept(response);
            }
            else if (object instanceof DeleteTableResponse response && deleteTableListener != null)
            {
              deleteTableListener.accept(response);
            }
            else if (object instanceof TableUpdatedMessage response && updatedMessageListener !=null)
            {
              updatedMessageListener.accept(response);
            }
            else if(object instanceof GetWorkersResponse response
                && workersListener != null)
            {
              workersListener.accept(response);
            }
            else if (object instanceof GetTableOrderResponse response && tableOrderResponseListener != null)
            {
              tableOrderResponseListener.accept(response);
            }
            else if (object instanceof DeleteReservationResponse response && deleteReservationListener != null)
            {
              deleteReservationListener.accept(response);
            }
            else if (object instanceof UpdateReservationResponse response && updateReservationListener != null)
            {
              updateReservationListener.accept(response);
            }
            else if (object instanceof ReservationDeletedMessage message && reservationDeletedListener != null)
            {
              reservationDeletedListener.accept(message);
            }
            else if (object instanceof GetClosestReservationResponse message && closestReservationListener != null)
            {
                closestReservationListener.accept(message);
            }
            else if (object instanceof CreateAllergyResponse response && createdAllergyListener != null)
            {
              createdAllergyListener.accept(response);
            }
            else if (object instanceof GetAllergiesResponse response && getAllergiesListener != null)
            {
              getAllergiesListener.accept(response);
            }
            else if (object instanceof MessageResponse response && updateAllergyListener != null)
            {
              updateAllergyListener.accept(response);
            }
            else if (object instanceof MessageResponse response && deleteAllergyListener != null)
            {
              deleteAllergyListener.accept(response);
            }
            else if (object instanceof GetMenuItemsResponse response && !menuItemsListeners.isEmpty())
            {
              for (var c : menuItemsListeners) c.accept(response);
            }
            else if (object instanceof GetOrdersByWaiterResponse response && !waiterOrdersListeners.isEmpty())
            {
              for (var c : waiterOrdersListeners) c.accept(response);
            }
            else if (object instanceof AddItemsToOrderResponse response && !addItemsListeners.isEmpty())
            {
              for (var c : addItemsListeners) c.accept(response);
            }
            else if (object instanceof PayOrderResponse response && payOrderListener != null)
            {
              payOrderListener.accept(response);
            }


            else if (object instanceof GetMenuItemsResponse response && getMenuItemsListener != null)
            {
              getMenuItemsListener.accept(response);
            }
            else if (object instanceof CreateMenuItemResponse response && createdMenuItemListener != null)
            {
              createdMenuItemListener.accept(response);
            }
            else if (object instanceof MessageResponse response && updateMenuItemListener != null)
            {
              updateMenuItemListener.accept(response);
            }
            else if (object instanceof MessageResponse response && deleteMenuItemListener != null)
            {
              deleteMenuItemListener.accept(response);
            }
            else if (object instanceof CreateWorkerResponse response && workerCreatedListener != null)
            {
              workerCreatedListener.accept(response);
            }
            else if (object instanceof MessageResponse response && deleteWorkerListener != null)
            {
              deleteWorkerListener.accept(response);
            }
            else if (object instanceof MessageResponse response && updateWorkerListener != null)
            {
              updateWorkerListener.accept(response);
            }
          });
        }
      }
      catch(Exception e)
      {
        System.out.println("Disconnected from server");
      }
    });

    listenerThread.setDaemon(true);
    listenerThread.start();
  }

  public void setLoginListener(Consumer<LoginResponse> listener)
  {
    this.loginListener = listener;
  }

  public void setWorkersListener(Consumer<GetWorkersResponse> listener){
    this.workersListener = listener;
  }
  public void setReservationTablesListener(Consumer<GetTablesResponse> listener)
  {
    this.reservationTablesListener = listener;
  }

  public void setReservationsListener(Consumer<GetReservationsResponse> listener)
  {
    reservationsListener.add(listener);
  }
  public void setTablesPageListener(Consumer<GetTablesResponse> listener){
    tablesPageListener.add(listener);
  }

  public void setCreateReservationListener(Consumer<MessageResponse> listener)
  {
    this.createReservationListener = listener;
  }

  public void setReservationCreatedListener(Consumer<ReservationCreatedMessage> listener)
  {
    this.reservationCreatedListener = listener;
  }
  public void setDeleteReservationListener(Consumer<DeleteReservationResponse> listener)
  {
    this.deleteReservationListener = listener;
  }

  public void setUpdateReservationListener(Consumer<UpdateReservationResponse> listener)
  {
    this.updateReservationListener = listener;
  }
  public void setUpdatedMessageListener(Consumer<TableUpdatedMessage> listener){
    this.updatedMessageListener = listener;
  }

  public void setCreateTableListener(Consumer<TableCreateResponse> listener) {
    this.createTableListener = listener;
  }

  public void setTablesCreateListener(Consumer<TableCreatedMessage> listener) {
    this.tableCreatedListener = listener;
  }

  public void setClosestReservationListener(Consumer<GetClosestReservationResponse> listener){this.closestReservationListener = listener;}

  
  public void setTableOrderListener(Consumer<GetTableOrderResponse> listener){this.tableOrderResponseListener = listener;}
  
  public void setAllergyCreatedListener(Consumer<CreateAllergyResponse> listener) { this.createdAllergyListener = listener; }

  public void setGetAllergiesListener(Consumer<GetAllergiesResponse> listener) { this.getAllergiesListener = listener; }

  public void setUpdateAllergyListener(Consumer<MessageResponse> listener) { this.updateAllergyListener = listener; }

  public void setDeleteAllergyListener(Consumer<MessageResponse> listener) { this.deleteAllergyListener = listener; }

  public void setMenuItemsListener(Consumer<GetMenuItemsResponse> listener) {
    menuItemsListeners.add(listener);
  }

  public void setWaiterOrdersListener(Consumer<GetOrdersByWaiterResponse> listener) {
    waiterOrdersListeners.add(listener);
  }

  public void setAddItemsListener(Consumer<AddItemsToOrderResponse> listener) {
    addItemsListeners.add(listener);
  }

  public void setPayOrderListener(Consumer<PayOrderResponse> listener) {
    this.payOrderListener = listener;
  }
    public void setUpdateTableListener(Consumer<UpdateTableResponse> updateTableListener) {
        this.updateTableListener = updateTableListener;
    }

    public void setDeleteTableListener(Consumer<DeleteTableResponse> deleteTableListener) {
        this.deleteTableListener = deleteTableListener;
    }
  public void setGetMenuItemsListener(Consumer<GetMenuItemsResponse> listener)
  {
    this.getMenuItemsListener = listener;
  }
  public void setMenuItemCreatedListener(Consumer<CreateMenuItemResponse> listener) { this.createdMenuItemListener = listener; }
  public void setUpdateMenuItemListener(Consumer<MessageResponse> listener) { this.updateMenuItemListener = listener; }
  public void setDeleteMenuItemListener(Consumer<MessageResponse> listener) { this.deleteMenuItemListener = listener; }

  public void setWorkerCreatedListener(Consumer<CreateWorkerResponse> listener)
  {
    this.workerCreatedListener = listener;
  }

  public void setDeleteWorkerListener(Consumer<MessageResponse> listener)
  {
    this.deleteWorkerListener = listener;
  }

  public void setUpdateWorkerListener(Consumer<MessageResponse> listener)
  {
    this.updateWorkerListener = listener;
  }
}