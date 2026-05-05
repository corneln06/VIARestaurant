package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.*;
import org.store.viarestaurant.model.enums.WorkerRole;

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

  @Override public ArrayList<Reservation> getAllReservations()
      throws SQLException
  {
    String sql = "SELECT id, customer, date, partySize, restauranttable.id FROM reservations, restauranttable ";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      ResultSet rs = statement.executeQuery();
      ArrayList<Reservation> reservations = new ArrayList<>();

      while (rs.next())
      {
        reservations.add(mapRow(rs));
      }

      return reservations;
    }
  }

  @Override public Reservation getReservationById(Integer id)
      throws SQLException
  {
    String sql = "SELECT id, customer, date, partySize, tableId FROM reservations WHERE id = ?";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();

      if (rs.next())
      {
        return mapRow(rs);
      }
      else
      {
        throw new SQLException("No reservation found with id: " + id);
      }
    }
  }

  @Override public Reservation getReservationByCustomerName(String name)
      throws SQLException
  {
    String sql = "SELECT id, customer, date, partySize, tableId FROM reservations WHERE customer = ?";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setString(1, name);
      ResultSet rs = statement.executeQuery();

      if (rs.next())
      {
        return mapRow(rs);
      }
      else
      {
        throw new SQLException("No reservation found with name: " + name);
      }
    }
  }

  @Override
  public Reservation deleteById(Integer id) throws SQLException
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
