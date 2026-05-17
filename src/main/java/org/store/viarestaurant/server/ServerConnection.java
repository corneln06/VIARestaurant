package org.store.viarestaurant.server;

import org.store.viarestaurant.dao.*;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.server.dto.LoginRequest;
import org.store.viarestaurant.server.dto.LoginResponse;
import org.store.viarestaurant.server.dto.ReservationDto.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerConnection implements Runnable
{
  private final Socket socket;
  private final ObjectOutputStream outToClient;
  private final ObjectInputStream inFromClient;
  private final ConnectionPool connectionPool;
  private final WorkersDAO workersDAO;
  private final RestaurantTableDAO restaurantTableDAO;
  private final ReservationDAO reservationDAO;

  public ServerConnection(
      Socket connectionSocket,
      ConnectionPool connectionPool)
      throws IOException, SQLException
  {
    this.socket = connectionSocket;
    this.connectionPool = connectionPool;
    try
    {
      this.workersDAO = WorkersDAOImpl.getInstance();
      this.reservationDAO = ReservationDAOImpl.getInstance();
      this.restaurantTableDAO = RestaurantTableDAOImpl.getInstance();
    }
    catch(SQLException e)
    {
      throw new IOException("Could not initialize ReservationDAO", e);
    }

    System.out.println(
        "[SERVER] Creating streams...");

    outToClient =
        new ObjectOutputStream(
            connectionSocket.getOutputStream());

    outToClient.flush();

    inFromClient =
        new ObjectInputStream(
            connectionSocket.getInputStream());

    System.out.println(
        "[SERVER] Client connected: "
            + socket.getInetAddress());
  }

  @Override
  public void run()
  {
    try
    {
      while(true)
      {
        System.out.println(
            "[SERVER] Waiting for object...");

        Object object =
            inFromClient.readObject();

        System.out.println(
            "[SERVER] Object received: "
                + object.getClass().getSimpleName());

        if(object instanceof LoginRequest request)
        {
          System.out.println(
              "[SERVER] LoginRequest detected");

          handleLogin(request);
        } else if (object instanceof TableBookingRequest request)
        {
          System.out.println("[SERVER] TableBookingRequest detected");
          handleTableBooking(request);
        } else if(object instanceof GetTablesRequest)
        {
          handleGetTables();
        }
        else if(object instanceof GetReservationsRequest)
        {
          handleGetReservations();
        }
        else if(object instanceof UpdateReservationRequest request)
        {
          System.out.println("[SERVER] Update Reservation detected");
          handleUpdateReservation(request);
        }
        else if(object instanceof DeleteReservationRequest request)
        {
          System.out.println("[SERVER] DeleteReservationRequest detected");
          handleDeleteReservation(request);
        }
        else
        {
          System.out.println(
              "[SERVER] Unknown object received");
        }

        }

    }
    catch(IOException e)
    {
      System.out.println(
          "[SERVER] Client disconnected");

      e.printStackTrace();
    }
    catch(ClassNotFoundException e)
    {
      System.out.println(
          "[SERVER] Class not found");

      e.printStackTrace();
    }
    finally
    {
      try
      {
        connectionPool.remove(this);
        System.out.println(
            "[SERVER] Closing socket...");

        socket.close();

        System.out.println(
            "[SERVER] Socket closed");
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  private void handleGetTables() throws IOException{
    try
    {
      send(new GetTablesResponse(restaurantTableDAO.getAllRestaurantTables()));
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      send(new GetTablesResponse((new ArrayList<>())));
    }
  }
  private void handleGetReservations() throws IOException
  {
    try
    {
      send(new GetReservationsResponse(
          reservationDAO.getAllReservationsForToday()
      ));
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      send(new GetReservationsResponse(new ArrayList<>()));
    }
  }
  private void handleLogin(LoginRequest request)
      throws IOException
  {
    try
    {
      System.out.println(
          "[SERVER] Processing login...");

      String email =
          request.getUsername();

      String password =
          request.getPassword();

      System.out.println(
          "[SERVER] Email entered: "
              + email);

      if(email == null || email.isBlank())
      {
        System.out.println(
            "[SERVER] Email is empty");

        send(new LoginResponse(false, null));

        return;
      }

      if(password == null || password.isBlank())
      {
        System.out.println(
            "[SERVER] Password is empty");

        send(new LoginResponse(false, null));

        return;
      }

      System.out.println(
          "[SERVER] Searching worker in database...");

      Workers worker =
          workersDAO.getWorkerByEmail(email);

      if(worker == null)
      {
        System.out.println(
            "[SERVER] Worker not found");

        send(new LoginResponse(false, null));

        return;
      }

      System.out.println(
          "[SERVER] Worker found: "
              + worker.getEmail());

      boolean success =
          worker.verifyPassword(password);

      System.out.println(
          "[SERVER] Password correct: "
              + success);

      LoginResponse response;

      if(success)
      {
        System.out.println(
            "[SERVER] Login successful");

        response =
            new LoginResponse(
                true,
                worker);
      }
      else
      {
        System.out.println(
            "[SERVER] Invalid password");

        response =
            new LoginResponse(
                false,
                null);
      }

      send(response);

      System.out.println(
          "[SERVER] Response sent");
    }
    catch(SQLException e)
    {
      System.out.println(
          "[SERVER] Database error");

      e.printStackTrace();

      send(new LoginResponse(false, null));
    }
  }
  public void handleTableBooking(TableBookingRequest request)
      throws IOException
  {
    try
    {
      Reservation reservation =
          reservationDAO.createReservation(
              request.getName(),
              request.getDateTime(),
              request.getPartySize(),
              request.getRestaurantTable()
          );

      if(reservation == null)
      {
        send(new MessageResponse(
            false,
            "Could not create reservation"
        ));

        return;
      }

      send(new MessageResponse(
          true,
          "Reservation successfully created"
      ));

      connectionPool.broadcast(
          new ReservationCreatedMessage(
              request.getName(),
              request.getDateTime(),
              request.getPartySize(),
              request.getRestaurantTable()
          )
      );
    }
    catch(SQLException e)
    {
      e.printStackTrace();

      send(new MessageResponse(
          false,
          "Database error while creating reservation"
      ));
    }
  }
  public void handleCreateTable(TableCreateRequest request) throws IOException
  {
    try
    {
      RestaurantTable restaurantTable =
          restaurantTableDAO.createRestaurantTable(
              request.getMaxSitting());
      if(restaurantTable == null)
      {
        send(new MessageResponse(false, "Cannot create a table"));
        return;
      }
      send(new MessageResponse(true, "Table created successfully"));

      connectionPool.broadcast(
          new TableCreatedMessage(restaurantTable)
      );
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      send(new MessageResponse(false, "Database error while creating table"));
    }
  }

  public void send(Object object)
      throws IOException
  {
    System.out.println(
        "[SERVER] Sending object to client...");

    outToClient.writeObject(object);

    outToClient.flush();

    System.out.println(
        "[SERVER] Object sent");
  }


  private void handleDeleteReservation(DeleteReservationRequest request) throws IOException {
    try
    {
      reservationDAO.deleteById(request.getReservationId());

      send(new DeleteReservationResponse(
              true,
              "Reservation deleted successfully"
      ));

      connectionPool.broadcast(
              new ReservationDeletedMessage(request.getReservationId())
      );
      connectionPool.broadcast(
              new GetReservationsResponse(
                      reservationDAO.getAllReservationsForToday()
              )
      );
    }
    catch (SQLException e)
    {
      send(new DeleteReservationResponse(
              false,
              e.getMessage()
      ));
    }
  }

  private void handleUpdateReservation(UpdateReservationRequest request) throws IOException {
    try {
      Reservation reservation = new Reservation(
              request.getId(),
              request.getName(),
              request.getDateTime(),
              request.getPartySize(),
              request.getTable()
      );

      reservationDAO.updateReservation(reservation);

      send(new UpdateReservationResponse(
              true,
              "Reservation updated successfully"
      ));

      connectionPool.broadcast(
              new GetReservationsResponse(
                      reservationDAO.getAllReservationsForToday()
              )
      );


    } catch (SQLException e) {
      send(new UpdateReservationResponse(
              false,
              e.getMessage()
      ));
    }
  }

}