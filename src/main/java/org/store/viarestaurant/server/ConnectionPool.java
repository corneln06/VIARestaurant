package org.store.viarestaurant.server;

import java.io.IOException;
import java.util.List;

public class ConnectionPool
{
  private final List<ServerConnection> connections;

  public ConnectionPool(List<ServerConnection> connections)
  {
    this.connections = connections;
  }
  public void add(ServerConnection serverConnection){
    connections.add(serverConnection);
  }
  public void broadcast(String message) throws IOException
  {
    for (ServerConnection connection : connections){
      connections.add(connection);
    }
  }
}
