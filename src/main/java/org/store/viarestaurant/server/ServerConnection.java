package org.store.viarestaurant.server;

import org.store.viarestaurant.dao.WorkersDAO;
import org.store.viarestaurant.dao.WorkersDAOImpl;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.server.dto.LoginRequest;
import org.store.viarestaurant.server.dto.LoginResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class ServerConnection implements Runnable
{
  private final Socket socket;
  private final ObjectOutputStream outToClient;
  private final ObjectInputStream inFromClient;
  private final ConnectionPool connectionPool;
  private final WorkersDAO workersDAO;

  public ServerConnection(
      Socket connectionSocket,
      ConnectionPool connectionPool)
      throws IOException, SQLException
  {
    this.socket = connectionSocket;
    this.connectionPool = connectionPool;

    this.workersDAO =
        WorkersDAOImpl.getInstance();

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
}