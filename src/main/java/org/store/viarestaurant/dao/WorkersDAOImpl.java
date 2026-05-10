package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.Host;
import org.store.viarestaurant.model.entities.Manager;
import org.store.viarestaurant.model.entities.Waiter;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;

import java.sql.*;
import java.util.ArrayList;

import static org.store.viarestaurant.config.DatabaseConnection.getConnection;

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
    return DatabaseConnection.getConnection();
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
    ArrayList<Workers> workers = new ArrayList<>();
    try(Connection connection = getConnection()){
      PreparedStatement statement = connection.prepareStatement(
          "SELECT id, firstName, lastName, email, role from workers"
      );
      ResultSet rs = statement.executeQuery();
      while(rs.next()){

        int id = rs.getInt("id");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String email = rs.getString("email");
        String rawPassword = rs.getString("rawPassword");

        WorkerRole workerRole = WorkerRole.valueOf(rs.getString("role"));
        Workers worker;
        switch (workerRole){
          case Waiter -> worker = new Waiter(id, firstName, lastName, email, rawPassword);
          case Host -> worker = new Host(id, firstName, lastName, email, rawPassword);
          case Manager -> worker = new Manager(id, firstName, lastName, email, rawPassword);
          default -> throw new SQLException("Unknown role: " + workerRole);
        }
        workers.add(worker);
      }
    }
    return workers;
  }

  @Override public Workers getWorkerById(Integer id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT * FROM workers where id = ? ");
      preparedStatement.setInt(1, id);

      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next())
      {
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String email = rs.getString("email");
        String rawPassword = rs.getString("rawPassword");

        WorkerRole workerRole = WorkerRole.valueOf(rs.getString("role"));
        Workers worker;

        switch (workerRole)
        {
          case Waiter ->
              worker = new Waiter(id, firstName, lastName, email, rawPassword);
          case Host ->
              worker = new Host(id, firstName, lastName, email, rawPassword);
          case Manager ->
              worker = new Manager(id, firstName, lastName, email, rawPassword);
          default -> throw new SQLException("Unknown role: " + workerRole);
        }
        return worker;
      }
    }
    throw new SQLException("Worker with id " + id + " not found");
  }

  @Override public Workers getWorkerByEmail(String email) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT * FROM workers where email = ? ");
      preparedStatement.setString(1, email);

      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next())
      {
        int id = rs.getInt("id");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String rawPassword = rs.getString("rawPassword");

        WorkerRole workerRole = WorkerRole.valueOf(rs.getString("role"));
        Workers worker;

        switch (workerRole)
        {
          case Waiter ->
              worker = new Waiter(id, firstName, lastName, email, rawPassword);
          case Host ->
              worker = new Host(id, firstName, lastName, email, rawPassword);
          case Manager ->
              worker = new Manager(id, firstName, lastName, email, rawPassword);
          default -> throw new SQLException("Unknown role: " + workerRole);
        }
        return worker;
      }
    }
    throw new SQLException("Worker with email " + email + " not found");
  }


  @Override public void deleteWorkerById(Integer id) throws SQLException
  {
    try (Connection connection = getConnection()) {

      PreparedStatement preparedStatement = connection.prepareStatement(
          "DELETE FROM workers WHERE id = ?"
      );

      preparedStatement.setInt(1, id);

      int rowsAffected = preparedStatement.executeUpdate();

      if (rowsAffected == 0) {
        throw new SQLException("Worker with id " + id + " not found");
      }
    }
  }
}
