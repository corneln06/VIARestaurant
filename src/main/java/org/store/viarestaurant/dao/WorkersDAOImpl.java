package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.Host;
import org.store.viarestaurant.model.entities.Manager;
import org.store.viarestaurant.model.entities.Waiter;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;

import java.sql.*;
import java.util.ArrayList;

public class WorkersDAOImpl implements WorkersDAO
{
  private static WorkersDAOImpl instance;
  private WorkersDAOImpl() throws SQLException{
    DriverManager.registerDriver(new org.postgresql.Driver());
  }
  public static synchronized WorkersDAOImpl getInstance() throws SQLException{
    if (instance == null){
      instance = new WorkersDAOImpl();
    }
    return instance;
  }
  private Connection getConnection() throws SQLException{
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "Qetuoadgjl123.");
  }

  @Override
  public Workers createWorkers( String firstName, String lastName, String email, String rawPassword, WorkerRole role) throws SQLException {
    try (Connection connection = getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "INSERT INTO workers (firstName, lastName, rol, email, password) " +
              "VALUES (?, ?, ?, ?, ?) RETURNING id"
      );

      statement.setString(1, firstName);
      statement.setString(2, lastName);
      statement.setString(3, String.valueOf(role));
      statement.setString(4, email);
      statement.setString(5, rawPassword);

      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        int id = rs.getInt("id");

        switch (role) {
          case Waiter:
            return new Waiter(id, firstName, lastName, email, rawPassword);
          case Manager:
            return new Manager(id, firstName, lastName, email, rawPassword);
          case Host:
            return new Host(id, firstName, lastName, email, rawPassword);
          default:
            throw new SQLException("Unknown worker type: " + role);
      }
      } else {
        throw new SQLException("No ID returned");
      }
    }
  }

  @Override public ArrayList<Workers> getAllWorkers() throws SQLException
  {
    return null;
  }

  @Override public Workers getWorkerById(int id) throws SQLException
  {
    return null;
  }

  @Override public Workers getWorkerByEmail(String email) throws SQLException
  {
    return null;
  }

  @Override public void deleteWorkerById(int id) throws SQLException
  {

  }
}
