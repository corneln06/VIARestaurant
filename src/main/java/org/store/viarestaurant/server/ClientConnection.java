package org.store.viarestaurant.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection
{
  private final ObjectOutputStream outputStream;
  private final ObjectInputStream inputStream;

  public ClientConnection(Socket socket) throws IOException
  {
    outputStream = new ObjectOutputStream(socket.getOutputStream());
    inputStream = new ObjectInputStream(socket.getInputStream());
  }

}
