package org.store.viarestaurant.dao;


import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface ReservationDAO
{
  Reservation createReservation(String name, LocalDateTime dateTime, int partySize, RestaurantTable restaurantTable) throws SQLException;
  ArrayList<Reservation> getAllReservationsForToday() throws SQLException;
  Reservation getReservationById(int id) throws SQLException;
  Reservation getReservationByCustomerName(String name) throws SQLException;
  ArrayList<Reservation> getReservationByDate(LocalDateTime dateTime) throws SQLException;
  void deleteById(int id) throws SQLException;
}
