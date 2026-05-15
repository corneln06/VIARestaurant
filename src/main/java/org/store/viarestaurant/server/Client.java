package org.store.viarestaurant.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client
{
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public void connect() throws IOException
    {

      Socket socket = new Socket("localhost", 2910);

      outputStream = new ObjectOutputStream(socket.getOutputStream());
      outputStream.flush();
      inputStream = new ObjectInputStream(socket.getInputStream());
    }
    public void send(Object object) throws IOException{
      outputStream.writeObject(object);
      outputStream.flush();
    }
    public Object receive() throws IOException, ClassNotFoundException
    {
      return inputStream.readObject();
    }

}
