package org.store.viarestaurant.server;

import javafx.application.Platform;
import org.store.viarestaurant.server.dto.LoginResponse;
import org.store.viarestaurant.server.dto.ReservationDto.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class Client
{
  private Socket socket;
  private ObjectOutputStream outToServer;
  private ObjectInputStream inFromServer;

  private Consumer<LoginResponse> loginListener;
  private Consumer<GetTablesResponse> tablesListener;
  private Consumer<GetReservationsResponse> reservationsListener;
  private Consumer<CreateReservationResponse> createReservationListener;
  private Consumer<ReservationCreatedMessage> reservationCreatedListener;

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
            else if(object instanceof GetTablesResponse response && tablesListener != null)
            {
              tablesListener.accept(response);
            }
            else if(object instanceof GetReservationsResponse response && reservationsListener != null)
            {
              reservationsListener.accept(response);
            }
            else if(object instanceof CreateReservationResponse response && createReservationListener != null)
            {
              createReservationListener.accept(response);
            }
            else if(object instanceof ReservationCreatedMessage message && reservationCreatedListener != null)
            {
              reservationCreatedListener.accept(message);
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

  public void setTablesListener(Consumer<GetTablesResponse> listener)
  {
    this.tablesListener = listener;
  }

  public void setReservationsListener(Consumer<GetReservationsResponse> listener)
  {
    this.reservationsListener = listener;
  }

  public void setCreateReservationListener(Consumer<CreateReservationResponse> listener)
  {
    this.createReservationListener = listener;
  }

  public void setReservationCreatedListener(Consumer<ReservationCreatedMessage> listener)
  {
    this.reservationCreatedListener = listener;
  }
}