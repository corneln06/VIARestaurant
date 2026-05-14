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
  private RestaurantTable testTable, testTable2;
  private int createdId;

  @BeforeAll
  void setup() throws SQLException
  {
    dao = ReservationDAOImpl.getInstance();
    testTable = RestaurantTableDAOImpl.getInstance().createRestaurantTable(4);
    testTable2 = RestaurantTableDAOImpl.getInstance().createRestaurantTable(5);


  }

  @Test
  @Order(1)
  void testCreateReservation() throws SQLException
  {
    Reservation r = dao.createReservation(
        "Jan Novak",
        LocalDateTime.now().withHour(20).withMinute(0).withSecond(0).withNano(0),
        4,
        testTable
    );

    assertNotNull(r);
    assertTrue(r.getId() > 0);
    assertEquals("Jan Novak", r.getName());
    assertEquals(4, r.getPartySize());
    assertEquals(testTable, r.getTable());

    createdId = r.getId();
  }

  @Test
  @Order(2)
  void testCreateReservationPartySizeExceedsMaxSitting() throws SQLException
  {
    assertThrows(SQLException.class, () -> dao.createReservation(
        "Big Party",
        LocalDateTime.now(),
        10,
        testTable
    ));
  }

  @Test
  @Order(3)
  void testCreateReservationWithPastDate() throws SQLException
  {
    assertThrows(SQLException.class, () -> dao.createReservation(
        "Past Person",
        LocalDateTime.of(2020, 1, 1, 12, 0),
        2,
        testTable
    ));
  }

  @Test
  @Order(4)
  void testGetAllReservationsForToday() throws SQLException
  {
    ArrayList<Reservation> list = dao.getAllReservationsForToday();

    assertNotNull(list);
    assertFalse(list.isEmpty());
  }

  @Test
  @Order(5)
  void testGetReservationById() throws SQLException
  {
    Reservation r = dao.getReservationById(createdId);

    assertNotNull(r);
    assertEquals(createdId, r.getId());
    assertEquals("Jan Novak", r.getName());
  }

  @Test
  @Order(6)
  void testGetReservationByName() throws SQLException
  {
    Reservation r = dao.getReservationByCustomerName("Jan Novak");

    assertNotNull(r);
    assertEquals("Jan Novak", r.getName());
  }
  @Test
  @Order(7)
  void testUpdateReservation() throws SQLException
  {
    Reservation r = dao.getReservationById(createdId);
    r.setName("James Bond");
    r.setTable(testTable2);
    dao.updateReservation(r);

    Reservation r2 = dao.getReservationById(createdId);


    assertNotNull(r2);
    assertTrue(r2.getId() > 0);
    assertEquals("James Bond", r2.getName());
    assertEquals(4, r2.getPartySize());
    assertEquals(testTable2, r2.getTable());


  }
  @Test
  @Order(8)
  void testDeleteById() throws SQLException
  {
    dao.deleteById(createdId);

    assertThrows(SQLException.class, () -> dao.getReservationById(createdId));
  }
}