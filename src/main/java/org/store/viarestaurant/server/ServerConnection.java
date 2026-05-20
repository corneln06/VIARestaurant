package org.store.viarestaurant.server;

import org.store.viarestaurant.dao.*;
import org.store.viarestaurant.model.entities.*;
import org.store.viarestaurant.model.enums.WorkerRole;
import org.store.viarestaurant.model.state.AvailableState;
import org.store.viarestaurant.model.state.ReservedState;
import org.store.viarestaurant.model.state.SeatedState;
import org.store.viarestaurant.server.dto.AllergyDTO.*;
import org.store.viarestaurant.server.dto.LoginDTO.LoginRequest;
import org.store.viarestaurant.server.dto.LoginDTO.LoginResponse;
import org.store.viarestaurant.server.dto.MessageResponse;
import org.store.viarestaurant.server.dto.ReservationDto.*;
import org.store.viarestaurant.server.dto.RestaurantTableDTO.*;
import org.store.viarestaurant.server.dto.TableOrderDTO.GetTableOrderRequest;
import org.store.viarestaurant.server.dto.TableOrderDTO.GetTableOrderResponse;
import org.store.viarestaurant.server.dto.WorkersDTO.GetWorkersRequest;
import org.store.viarestaurant.server.dto.WorkersDTO.GetWorkersResponse;
import org.store.viarestaurant.server.dto.MenuItemDTO.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerConnection implements Runnable {
  private final Socket socket;
  private final ObjectOutputStream outToClient;
  private final ObjectInputStream inFromClient;
  private final ConnectionPool connectionPool;
  private final WorkersDAO workersDAO;
  private final RestaurantTableDAO restaurantTableDAO;
  private final ReservationDAO reservationDAO;
  private final TableOrderDAO tableOrderDAO;
  private final AllergyDAO allergyDAO;
  private final MenuItemDAO menuItemDAO;

  private Workers currentUser;

  public ServerConnection(
      Socket connectionSocket,
      ConnectionPool connectionPool)
      throws IOException, SQLException {
    this.socket = connectionSocket;
    this.connectionPool = connectionPool;
    try {
      this.workersDAO = WorkersDAOImpl.getInstance();
      this.reservationDAO = ReservationDAOImpl.getInstance();
      this.restaurantTableDAO = RestaurantTableDAOImpl.getInstance();
      this.tableOrderDAO = TableOrderDAOImpl.getInstance();
      this.allergyDAO = AllergyDAOImpl.getInstance();
      this.menuItemDAO = MenuItemDAOImpl.getInstance();
    }
    catch(SQLException e)
    {
      throw new IOException("Could not initialize ReservationDAO", e);
    }

    System.out.println(
        "[SERVER] Creating streams...");

    outToClient = new ObjectOutputStream(
        connectionSocket.getOutputStream());

    outToClient.flush();

    inFromClient = new ObjectInputStream(
        connectionSocket.getInputStream());

    System.out.println(
        "[SERVER] Client connected: "
            + socket.getInetAddress());
  }

  @Override
  public void run() {
    try {
      while (true) {
        System.out.println(
            "[SERVER] Waiting for object...");

        Object object = inFromClient.readObject();

        System.out.println(
            "[SERVER] Object received: "
                + object.getClass().getSimpleName());

        if (object instanceof LoginRequest request) {
          System.out.println(
              "[SERVER] LoginRequest detected");

          handleLogin(request);
        } else if (object instanceof TableBookingRequest request) {
          System.out.println("[SERVER] TableBookingRequest detected");
          handleTableBooking(request);
        } else if (object instanceof GetTablesRequest) {
          System.out.println("[SERVER] Get Tables detected");
          handleGetTables();
        } else if (object instanceof GetReservationsRequest) {
          System.out.println("[SERVER] Get Reservation detected");
          handleGetReservations();
        } else if (object instanceof UpdateReservationRequest request) {
          System.out.println("[SERVER] Update Reservation detected");
          handleUpdateReservation(request);
        } else if (object instanceof GetWorkersRequest) {
          System.out.println("[SERVER] Get Workers detected ");
          handleGetWorkers();
        } else if (object instanceof DeleteReservationRequest request) {
          System.out.println("[SERVER] DeleteReservationRequest detected");
          handleDeleteReservation(request);
        } else if (object instanceof SeatTableRequest request) {
          handleSeatTable(request);
        } else if (object instanceof SetTableAvailableRequest request) {
          handleSetTableAvailable(request);
        } else if (object instanceof SetTableReservedRequest request) {
          handleSetTableReserved(request);
        } else if (object instanceof TableCreateRequest request) {
          System.out.println("[SERVER] CreateTableRequest detected");
          handleCreateTable(request);
        }
        else if (object instanceof GetTableOrderRequest request)
        {
          System.out.println("[SERVER] GetTableOrderRequest detected");
          populateComboBoxForEveryone(request);

        }
        else if (object instanceof CreateAllergyRequest request)
        {
          System.out.println("[SERVER] CreateAllergyRequest detected");
          handleCreateAllergy(request);
        }
        else if (object instanceof GetAllergiesRequest)
        {
          System.out.println("[SERVER] GetAllergiesRequest detected");
          handleGetAllergies();
        }
        else if (object instanceof UpdateAllergyRequest request)
        {
          System.out.println("[SERVER] UpdateAllergyRequest detected");
          handleUpdateAllergy(request);
        }
        else if (object instanceof DeleteAllergyRequest request)
        {
          System.out.println("[SERVER] DeleteAllergyRequest detected");
          handleDeleteAllergy(request);
        } else if (object instanceof GetClosestReservationRequest request) {
          System.out.println("[SERVER] GetClosestReservationRequest detected");
          handleGetClosestReservationRequest(request);
        }
        else if (object instanceof GetMenuItemsRequest)
        {
          System.out.println("[SERVER] GetMenuItemsRequest detected");
          handleGetMenuItems();
        }
        else if (object instanceof CreateMenuItemRequest request)
        {
          System.out.println("[SERVER] CreateMenuItemRequest detected");
          handleCreateMenuItem(request);
        }
        else if (object instanceof UpdateMenuItemRequest request)
        {
          System.out.println("[SERVER] UpdateMenuItemRequest detected");
          handleUpdateMenuItem(request);
        }
        else if (object instanceof DeleteMenuItemRequest request)
        {
          System.out.println("[SERVER] DeleteMenuItemRequest detected");
          handleDeleteMenuItem(request);
        }
        else
        {
          System.out.println(
              "[SERVER] Unknown object received");
        }

      }

    } catch (IOException e) {
      System.out.println(
          "[SERVER] Client disconnected");

      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      System.out.println(
          "[SERVER] Class not found");

      e.printStackTrace();
    } finally {
      try {
        connectionPool.remove(this);
        System.out.println(
            "[SERVER] Closing socket...");

        socket.close();

        System.out.println(
            "[SERVER] Socket closed");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void handleDeleteAllergy(DeleteAllergyRequest request) throws IOException
  {
    try
    {
      allergyDAO.delete(request.getId());

      send(new MessageResponse(true, "Allergy deleted successfully"));

      connectionPool.broadcast(
              new GetAllergiesResponse(
                      allergyDAO.getAllAllergies()
              )
      );
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      send(new MessageResponse(false, "Database error while deleting allergy"));
    }
  }
  private void handleUpdateAllergy(UpdateAllergyRequest request) throws IOException
  {
    try
    {
      Allergy allergy = new Allergy(
              request.getId(),
              request.getName()
      );

      allergyDAO.updateAllergy(allergy);

      send(new MessageResponse(true, "Allergy updated successfully"));

      connectionPool.broadcast(
              new GetAllergiesResponse(
                      allergyDAO.getAllAllergies()
              )
      );
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      send(new MessageResponse(false, "Database error while updating allergy"));
    }
  }

  private void handleCreateAllergy(CreateAllergyRequest request) throws IOException {
    try {
      Allergy allergy = allergyDAO.createAllergy(request.getName());

      if (allergy == null)
      {
        send(new MessageResponse(false, "Cannot create an Allergy"));
        return;
      }
      send(new MessageResponse(true, "Allergy created successfully"));

      connectionPool.broadcast(
              new CreateAllergyResponse(allergy)
      );
      connectionPool.broadcast(
              new GetAllergiesResponse(
                      allergyDAO.getAllAllergies()
              ));
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      send(new MessageResponse(false, "Database error while creating allergy"));
    }
  }

  private void handleGetMenuItems() throws IOException
  {
    try
    {
      send(new GetMenuItemsResponse(menuItemDAO.getAllMenuItems()));
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      send(new GetMenuItemsResponse(new ArrayList<>()));
    }
  }

  private void handleCreateMenuItem(CreateMenuItemRequest request) throws IOException
  {
    try
    {
      MenuItems menuItem = menuItemDAO.createMenuItem(
          request.getName(), request.getType(), request.getPrice(),
          request.isVegetarian(), request.getAllergies()
      );
      send(new MessageResponse(true, "Menu item created successfully"));
      connectionPool.broadcast(new CreateMenuItemResponse(menuItem));
      connectionPool.broadcast(new GetMenuItemsResponse(menuItemDAO.getAllMenuItems()));
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      send(new MessageResponse(false, "Database error while creating menu item"));
    }
  }

  private void handleUpdateMenuItem(UpdateMenuItemRequest request) throws IOException
  {
    try
    {
      menuItemDAO.updateMenuItem(new org.store.viarestaurant.model.entities.MenuItems(
          request.getId(), request.getName(), request.getType(),
          request.getPrice(), request.isVegetarian(), request.getAllergies()
      ));
      send(new MessageResponse(true, "Menu item updated successfully"));
      connectionPool.broadcast(
          new GetMenuItemsResponse(menuItemDAO.getAllMenuItems())
      );
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      send(new MessageResponse(false, "Database error while updating menu item"));
    }
  }

  private void handleDeleteMenuItem(DeleteMenuItemRequest request) throws IOException
  {
    try
    {
      menuItemDAO.delete(request.getId());
      send(new MessageResponse(true, "Menu item deleted successfully"));
      connectionPool.broadcast(
          new GetMenuItemsResponse(menuItemDAO.getAllMenuItems())
      );
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      send(new MessageResponse(false, "Database error while deleting menu item"));
    }
  }

  private void handleGetWorkers() throws IOException {
    try {
      send(new GetWorkersResponse(
          workersDAO.getAllWorkers()));
    } catch (SQLException e) {
      e.printStackTrace();
      send(new GetWorkersResponse(new ArrayList<>()));
    }
  }

  private void handleGetTables() throws IOException {
    try {
      if (currentUser.getRole() == WorkerRole.Waiter) {
        send(new GetTablesResponse(restaurantTableDAO.getAllRestaurantTablesByWaiter(currentUser.getId())));
      } else {
        send(new GetTablesResponse(restaurantTableDAO.getAllRestaurantTables()));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      send(new GetTablesResponse((new ArrayList<>())));
    }
  }

  private void handleGetReservations() throws IOException {
    try {
      send(new GetReservationsResponse(
          reservationDAO.getAllReservationsForToday()));
    } catch (SQLException e) {
      e.printStackTrace();
      send(new GetReservationsResponse(new ArrayList<>()));
    }
  }

  private void handleLogin(LoginRequest request)
      throws IOException {
    try {
      System.out.println(
          "[SERVER] Processing login...");

      String email = request.getUsername();

      String password = request.getPassword();

      System.out.println(
          "[SERVER] Email entered: "
              + email);

      if (email == null || email.isBlank()) {
        System.out.println(
            "[SERVER] Email is empty");

        send(new LoginResponse(false, null));

        return;
      }

      if (password == null || password.isBlank()) {
        System.out.println(
            "[SERVER] Password is empty");

        send(new LoginResponse(false, null));

        return;
      }

      System.out.println(
          "[SERVER] Searching worker in database...");

      Workers worker = workersDAO.getWorkerByEmail(email);

      if (worker == null) {
        System.out.println(
            "[SERVER] Worker not found");

        send(new LoginResponse(false, null));

        return;
      }

      System.out.println(
          "[SERVER] Worker found: "
              + worker.getEmail());

      boolean success = worker.verifyPassword(password);

      System.out.println(
          "[SERVER] Password correct: "
              + success);

      LoginResponse response;

      if (success) {
        System.out.println(
            "[SERVER] Login successful");

        response = new LoginResponse(
            true,
            worker);
        currentUser = worker;
      } else {
        System.out.println(
            "[SERVER] Invalid password");

        response = new LoginResponse(
            false,
            null);
      }

      send(response);

      System.out.println(
          "[SERVER] Response sent");
    } catch (SQLException e) {
      System.out.println(
          "[SERVER] Database error");

      e.printStackTrace();

      send(new LoginResponse(false, null));
    }
  }

  public void handleTableBooking(TableBookingRequest request)
      throws IOException {
    try {
      Reservation reservation = reservationDAO.createReservation(
          request.getName(),
          request.getDateTime(),
          request.getPartySize(),
          request.getRestaurantTable());

      if (reservation == null) {
        send(new MessageResponse(
            false,
            "Could not create reservation"));

        return;
      }

      send(new MessageResponse(
          true,
          "Reservation successfully created"));

      connectionPool.broadcast(
          new ReservationCreatedMessage(
              request.getName(),
              request.getDateTime(),
              request.getPartySize(),
              request.getRestaurantTable()));
    } catch (SQLException e) {
      e.printStackTrace();

      send(new MessageResponse(
          false,
          "Database error while creating reservation"));
    }
  }

  public void handleCreateTable(TableCreateRequest request) throws IOException {
    try {
      RestaurantTable restaurantTable = restaurantTableDAO.createRestaurantTable(
          request.getMaxSitting());
      if (restaurantTable == null) {
        send(new TableCreateResponse(false, "Cannot create a table"));
        return;
      }
      send(new TableCreateResponse(true, "Table created successfully"));

      connectionPool.broadcast(
          new TableCreatedMessage(restaurantTable));
    } catch (SQLException e) {
      e.printStackTrace();
      send(new TableCreateResponse(false, "Database error while creating table"));
    }
  }

  public void send(Object object)
      throws IOException {
    System.out.println(
        "[SERVER] Sending object to client...");

    outToClient.writeObject(object);

    outToClient.flush();

    System.out.println(
        "[SERVER] Object sent");
  }

  private void handleDeleteReservation(DeleteReservationRequest request) throws IOException {
    try {
      reservationDAO.deleteById(request.getReservationId());

      send(new DeleteReservationResponse(
          true,
          "Reservation deleted successfully"));

      connectionPool.broadcast(
          new ReservationDeletedMessage(request.getReservationId()));
      connectionPool.broadcast(
          new GetReservationsResponse(
              reservationDAO.getAllReservationsForToday()));
    } catch (SQLException e) {
      send(new DeleteReservationResponse(
          false,
          e.getMessage()));
    }
  }

  private void handleUpdateReservation(UpdateReservationRequest request) throws IOException {
    try {
      Reservation reservation = new Reservation(
          request.getId(),
          request.getName(),
          request.getDateTime(),
          request.getPartySize(),
          request.getTable());

      reservationDAO.updateReservation(reservation);

      send(new UpdateReservationResponse(
          true,
          "Reservation updated successfully"));

      connectionPool.broadcast(
          new GetReservationsResponse(
              reservationDAO.getAllReservationsForToday()));

    } catch (SQLException e) {
      send(new UpdateReservationResponse(
          false,
          e.getMessage()));
    }
  }

  private void handleSetTableAvailable(SetTableAvailableRequest request)
      throws IOException {
    try {

      TableOrder currentOrder = tableOrderDAO.getTableOrdersByTableId(request.getTableId()).stream()
          .filter(t -> !t.isPaid()).findFirst().orElse(null);

      if (currentOrder != null) {
        currentOrder.setPaid(true);
        tableOrderDAO.updateTableOrder(currentOrder);
      }

      restaurantTableDAO.updateTableState(
          request.getTableId(),
          new AvailableState());

      connectionPool.broadcast(
          new TableUpdatedMessage(request.getTableId()));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void handleSeatTable(SeatTableRequest request) throws IOException {
    try {
      tableOrderDAO.createTableOrder(
          request.getTableId(),
          request.getWaiterId(),
          "",
          0.0,
          new ArrayList<>(),
          false);
      restaurantTableDAO.updateTableState(
          request.getTableId(),
          new SeatedState());

      connectionPool.broadcast(
          new TableUpdatedMessage(request.getTableId()));
    } catch (SQLException e) {
      e.printStackTrace();
      send(new MessageResponse(false, "Could not seat table"));
    }
  }
  private void handleGetAllergies() throws IOException
  {
    try
    {
      send(new GetAllergiesResponse(
              allergyDAO.getAllAllergies()
      ));
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      send(new GetAllergiesResponse(new ArrayList<>()));
    }
  }

  private void handleSetTableReserved(SetTableReservedRequest request)
      throws IOException {
    try {
      restaurantTableDAO.updateTableState(
          request.getTableId(),
          new ReservedState());

      connectionPool.broadcast(
          new TableUpdatedMessage(request.getTableId()));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void handleGetClosestReservationRequest(
      GetClosestReservationRequest request) throws IOException {
    try {
      Reservation reservation = reservationDAO.getClosestReservationInTimeForTable(
          request.getTableId());

      send(new GetClosestReservationResponse(reservation));
    } catch (SQLException e) {
      e.printStackTrace();
      send(new GetClosestReservationResponse(null));
    }
  }
  private void populateComboBoxForEveryone(GetTableOrderRequest request){
    try
    {
      TableOrder tableOrder = tableOrderDAO.getCurrentTableOrderByTableId(request.getTableId());

      send(new GetTableOrderResponse(tableOrder));
      connectionPool.broadcast(new TableOrderUpdatedMessage(request.getTableId()));
    }
    catch (SQLException | IOException e)
    {
      throw new RuntimeException(e);
    }
  }


}