package org.store.viarestaurant.server;

import javafx.application.Platform;
import org.store.viarestaurant.server.dto.ReservationDto.ReservationCreatedMessage;

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

  private Consumer<ReservationCreatedMessage> reservationCreatedListener;

  public void connect() throws IOException
  {
    socket = new Socket("localhost", 5000);

    outToServer = new ObjectOutputStream(socket.getOutputStream());
    outToServer.flush();

    inFromServer = new ObjectInputStream(socket.getInputStream());
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

          if(object instanceof ReservationCreatedMessage message)
          {
            if(reservationCreatedListener != null)
            {
              Platform.runLater(() ->
                  reservationCreatedListener.accept(message)
              );
            }
          }
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
  public Object receive() throws IOException, ClassNotFoundException
  {
    return inFromServer.readObject();
  }

  public void setReservationCreatedListener(
      Consumer<ReservationCreatedMessage> listener)
  {
    this.reservationCreatedListener = listener;
  }

}
