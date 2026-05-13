package org.store.viarestaurant.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServerConnection implements Runnable
{
  private ObjectOutputStream outFromServer;
  private ObjectInputStream inputStream;

  @Override public void run()
  {
    try
    {
      while (true){

      }
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    public void send(String message){

  }
  }
}
