// I needed a reservation test tha did not deelete the tables so they would persist in the UI
//To delete just run ReservationDAOImplTest


package org.store.viarestaurant.dao;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationSeedTest
{
  private ReservationDAOImpl dao;
  private RestaurantTable seedTable;

  @BeforeAll
  void setup() throws SQLException
  {
    dao = ReservationDAOImpl.getInstance();
    seedTable = new RestaurantTable(3, 4);
  }

  @Test
  void seedTodayReservation() throws SQLException
  {
    Reservation r = dao.createReservation(
        "Vini JR",
        LocalDateTime.now().withHour(19).withMinute(0).withSecond(0).withNano(0),
        2,
        seedTable
    );

    assertNotNull(r);
    assertTrue(r.getId() > 0);
  }
}
