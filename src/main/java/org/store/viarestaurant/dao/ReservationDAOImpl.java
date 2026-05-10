package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.*;
import org.store.viarestaurant.model.enums.WorkerRole;
import org.store.viarestaurant.model.state.AvailableState;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReservationDAOImpl implements ReservationDAO
{
  private static ReservationDAOImpl instance;

  private ReservationDAOImpl() throws SQLException{
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized ReservationDAOImpl getInstance() throws SQLException{
    if (instance == null){
      instance = new ReservationDAOImpl();
    }
    return instance;
  }

  private Connection getConnection() throws SQLException{
    return DatabaseConnection.getConnection();
  }

  @Override
  public Reservation createReservation(String name, LocalDateTime dateTime,
      int partySize, RestaurantTable restaurantTable) throws SQLException
  {
    String sql = "INSERT INTO reservations (customer, date, partySize, tableId) " +
        "VALUES (?, ?, ?, ?) RETURNING id";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setString(1, name);
      statement.setTimestamp(2, Timestamp.valueOf(dateTime));
      statement.setInt(3, partySize);
      statement.setInt(4, restaurantTable.getId());

      ResultSet rs = statement.executeQuery();

      if (rs.next())
      {
        int id = rs.getInt("id");
        return new Reservation(id, name, dateTime, partySize, restaurantTable);
      }
      else
      {
        throw new SQLException("No ID returned from reservation insert");
      }
    }
  }

  @Override
  public ArrayList<Reservation> getAllReservations() throws SQLException
  {
    String sql = "SELECT r.id, r.customer, r.date, r.partySize, t.id AS tableId, t.maxSitting " +
        "FROM reservations r JOIN restauranttable t ON r.tableId = t.id";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      ResultSet rs = statement.executeQuery();
      ArrayList<Reservation> reservations = new ArrayList<>();

      while (rs.next())
      {
        RestaurantTable table = new RestaurantTable(
            rs.getInt("tableId"),
            rs.getInt("maxSitting"),
            new AvailableState()
        );
        reservations.add(new Reservation(
            rs.getInt("id"),
            rs.getString("customer"),
            rs.getTimestamp("date").toLocalDateTime(),
            rs.getInt("partySize"),
            table
        ));
      }

      return reservations;
    }
  }
  @Override
  public Reservation getReservationById(int id) throws SQLException
  {
    String sql = "SELECT r.id, r.customer, r.date, r.partySize, t.id AS tableId, t.maxSitting " +
        "FROM reservations r JOIN restauranttable t ON r.tableId = t.id " +
        "WHERE r.id = ?";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();

      if (rs.next())
      {
        RestaurantTable table = new RestaurantTable(
            rs.getInt("tableId"),
            rs.getInt("maxSitting"),
            new AvailableState()
        );
        return new Reservation(
            rs.getInt("id"),
            rs.getString("customer"),
            rs.getTimestamp("date").toLocalDateTime(),
            rs.getInt("partySize"),
            table
        );
      }
      else
      {
        throw new SQLException("No reservation found with id: " + id);
      }
    }
  }

  @Override
  public Reservation getReservationByCustomerName(String name) throws SQLException
  {
    String sql = "SELECT r.id, r.customer, r.date, r.partySize, t.id AS tableId, t.maxSitting " +
        "FROM reservations r JOIN restauranttable t ON r.tableId = t.id " +
        "WHERE r.customer = ?";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setString(1, name);
      ResultSet rs = statement.executeQuery();

      if (rs.next())
      {
        RestaurantTable table = new RestaurantTable(
            rs.getInt("tableId"),
            rs.getInt("maxSitting"),
            new AvailableState()
        );
        return new Reservation(
            rs.getInt("id"),
            rs.getString("customer"),
            rs.getTimestamp("date").toLocalDateTime(),
            rs.getInt("partySize"),
            table
        );
      }
      else
      {
        throw new SQLException("No reservation found with name: " + name);
      }
    }
  }

  @Override
  public ArrayList<Reservation> getReservationByDate(LocalDateTime dateTime) throws SQLException
  {
    String sql = "SELECT r.id, r.customer, r.date, r.partySize, t.id AS tableId, t.maxSitting " +
        "FROM reservations r JOIN restauranttable t ON r.tableId = t.id " +
        "WHERE CAST(r.date AS DATE) = CAST(? AS DATE)";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setTimestamp(1, Timestamp.valueOf(dateTime));
      ResultSet rs = statement.executeQuery();
      ArrayList<Reservation> reservations = new ArrayList<>();

      while (rs.next())
      {
        RestaurantTable table = new RestaurantTable(
            rs.getInt("tableId"),
            rs.getInt("maxSitting"),
            new AvailableState()
        );
        reservations.add(new Reservation(
            rs.getInt("id"),
            rs.getString("customer"),
            rs.getTimestamp("date").toLocalDateTime(),
            rs.getInt("partySize"),
            table
        ));
      }

      return reservations;
    }
  }

  @Override
  public Reservation deleteById(int id) throws SQLException
  {
    Reservation reservation = getReservationById(id);

    String sql = "DELETE FROM reservations WHERE id = ?";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setInt(1, id);
      int affected = statement.executeUpdate();

      if (affected == 0)
      {
        throw new SQLException("Delete failed, no reservation found with id: " + id);
      }

      return reservation;
    }
  }
}
