package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.*;
import org.store.viarestaurant.model.enums.WorkerRole;
import org.store.viarestaurant.model.state.AvailableState;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

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
    if (dateTime.isBefore(LocalDateTime.now()))
    {
      throw new SQLException("Reservation cannot be created for the past!");
    }
    if (restaurantTable.getMaxSitting() < partySize)
    {
      throw new SQLException("The party size exceeds max sitting!");
    }

    String sql = "INSERT INTO reservations (customer, date, partySize, tableId) " +
        "VALUES (?, ?, ?, ?) RETURNING id";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setString(1, name);
      statement.setTimestamp(2, Timestamp.valueOf(dateTime));
      statement.setInt(3, partySize);
      statement.setInt(4, restaurantTable.getId());


        ArrayList<Reservation> sameTimeReservations = getReservationByDate(dateTime);
        Reservation repeatedReservation = sameTimeReservations.stream()
                .filter(reservation ->
                        Objects.equals(reservation.getTable().getId(), restaurantTable.getId())
                )
                .findFirst()
                .orElse(null);

        if (repeatedReservation == null) {
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
        }else {
            throw new SQLException("That table is already reserved for that time, please select another one.");
        }
    }
  }

  @Override
  public ArrayList<Reservation> getAllReservationsForToday() throws SQLException
  {
    String sql = "SELECT r.id, r.customer, r.date, r.partySize, t.id AS tableId, t.maxSitting " +
        "FROM reservations r JOIN restauranttable t ON r.tableId = t.id WHERE CAST(r.date AS DATE) = CURRENT_DATE";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      ResultSet rs = statement.executeQuery();
      ArrayList<Reservation> reservations = new ArrayList<>();

      while (rs.next())
      {
        RestaurantTable table = new RestaurantTable(
            rs.getInt("tableId"),
            rs.getInt("maxSitting")
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
            rs.getInt("maxSitting")
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
            rs.getInt("maxSitting")
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
            rs.getInt("maxSitting")
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
  public void deleteById(int id) throws SQLException
  {
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
    }
  }

  @Override
  public Reservation updateReservation(Reservation reservation) throws SQLException {
    try (Connection connection = getConnection())
    {
         PreparedStatement statement = connection.prepareStatement(
                 "update reservations set customer=?, " +
                         "date=?," +
                         "partySize=?," +
                         "tableId=?" +
                         " where id=?"
         );

      statement.setString(1, reservation.getName());
      statement.setTimestamp(2, Timestamp.valueOf(reservation.getDateTime()));
      statement.setInt(3,reservation.getPartySize());
      statement.setInt(4, reservation.getTable().getId());
      statement.setInt(5, reservation.getId());

      ArrayList<Reservation> sameTimeReservations = getReservationByDate(reservation.getDateTime());
      Reservation repeatedReservation = sameTimeReservations.stream()
              .filter(reservationRep ->
                      Objects.equals(reservation.getTable().getId(), reservationRep.getTable().getId()) && reservation.getDateTime().equals(reservationRep.getDateTime()) && reservation.getId() != reservationRep.getId()
              )
              .findFirst()
              .orElse(null);

      int affected = 0;

      if (repeatedReservation == null) {
          affected = statement.executeUpdate();
      } else {
          throw new SQLException("That table is already reserved for that time, please select another one.");
      }

      if (affected == 0)
      {
        throw new SQLException("Update failed, no reservation found with id: " + reservation.getId());
      }
      return reservation;
    }
  }
}
