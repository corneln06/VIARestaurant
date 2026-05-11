package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationDAOImplTest
{
  private ReservationDAOImpl dao;
  private RestaurantTable testTable;
  private int createdId;

  @BeforeAll
  void setup() throws SQLException
  {
    dao = ReservationDAOImpl.getInstance();
    testTable = new RestaurantTable(1, 4, null);
  }

  @Test
  @Order(1)
  void testCreateReservation() throws SQLException
  {
    Reservation r = dao.createReservation(
        "Jan Novak",
        LocalDateTime.now(),
        4,
        testTable
    );

    assertNotNull(r);
    assertTrue(r.getId() > 0);
    assertEquals("Jan Novak", r.getName());
    assertEquals(4, r.getPartySize());

    createdId = r.getId(); // now this is saved on the same instance
  }

  @Test
  @Order(2)
  void testGetAllReservationsForToday() throws SQLException
  {
    ArrayList<Reservation> list = dao.getAllReservationsForToday();

    assertNotNull(list);
    assertFalse(list.isEmpty());
  }

  @Test
  @Order(3)
  void testGetReservationById() throws SQLException
  {
    Reservation r = dao.getReservationById(createdId);

    assertNotNull(r);
    assertEquals(createdId, r.getId());
    assertEquals("Jan Novak", r.getName());
  }

  @Test
  @Order(4)
  void testGetReservationByName() throws SQLException
  {
    Reservation r = dao.getReservationByCustomerName("Jan Novak");

    assertNotNull(r);
    assertEquals("Jan Novak", r.getName());
  }

  @Test
  @Order(5)
  void testDeleteById() throws SQLException
  {
    Reservation deleted = dao.deleteById(createdId);

    assertNotNull(deleted);
    assertEquals(createdId, deleted.getId());

    assertThrows(SQLException.class, () -> dao.getReservationById(createdId));
  }
}