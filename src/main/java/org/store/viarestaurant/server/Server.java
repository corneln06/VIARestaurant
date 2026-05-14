package org.store.viarestaurant.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server
{
  public static void main(String[] args){
    System.out.println("Starting server...");


    try
    {
      ServerSocket welcomeSocket = new ServerSocket(2910);
      ConnectionPool connectionPool = new ConnectionPool();

      while(true){
        Socket socket = welcomeSocket.accept();
        ServerConnection serverConnection = new ServerConnection(socket, connectionPool);
        connectionPool.add(serverConnection);
        System.out.println("Client connection");
        new Thread(serverConnection).start();
      }

    }
    catch (IOException | SQLException e)
    {
      throw new RuntimeException(e);
    }
  }

}
