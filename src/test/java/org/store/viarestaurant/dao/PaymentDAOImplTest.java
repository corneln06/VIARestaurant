package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.Payment;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentDAOImplTest
{
  private PaymentDAOImpl dao;
  private TableOrder testOrder;
  private Workers testWorker;
  private RestaurantTable testTable;
  private int createdPaymentId;

  @BeforeAll
  void setup() throws SQLException
  {
    dao = PaymentDAOImpl.getInstance();
    WorkersDAO workersDAO = WorkersDAOImpl.getInstance();
    TableOrderDAO tableOrderDAO = TableOrderDAOImpl.getInstance();
    RestaurantTableDAO restaurantTableDAO = RestaurantTableDAOImpl.getInstance();

    testTable = restaurantTableDAO.createRestaurantTable(4);
    testWorker = workersDAO.createWorkers("Test", "Waiter", "test@via.dk", "1234", WorkerRole.Waiter);

    testOrder = tableOrderDAO.createTableOrder(
        testTable.getId(),
        testWorker.getId(),
        "Test notes",
        0.0,
        new ArrayList<>(),
        false
    );
  }

  @AfterAll
  void cleanup() throws SQLException
  {
    TableOrderDAO tableOrderDAO = TableOrderDAOImpl.getInstance();
    WorkersDAO workersDAO = WorkersDAOImpl.getInstance();
    RestaurantTableDAO restaurantTableDAO = RestaurantTableDAOImpl.getInstance();

    if (testOrder != null)
      tableOrderDAO.deleteTableOrderByID(testOrder.getId());
    if (testWorker != null)
      workersDAO.deleteWorkerById(testWorker.getId());
    if (testTable != null)
      restaurantTableDAO.deleteRestaurantTableByID(testTable.getId());
  }

  @Test
  @Order(1)
  void testCreatePayment() throws SQLException
  {
    Payment p = dao.createPayment(50.00, "Card", testOrder);

    assertNotNull(p);
    assertTrue(p.getId() > 0);
    assertEquals(50.00, p.getAmount());
    assertEquals("Card", p.getMethod());
    assertNotNull(p.getOrderId());

    createdPaymentId = p.getId();
  }

  @Test
  @Order(2)
  void testCreatePaymentWithCash() throws SQLException
  {
    Payment p = dao.createPayment(30.00, "Cash", testOrder);

    assertNotNull(p);
    assertEquals("Cash", p.getMethod());
    assertEquals(30.00, p.getAmount());

    dao.deleteById(p.getId());
  }

  @Test
  @Order(3)
  void testCreatePaymentInvalidAmount() throws SQLException
  {
    assertThrows(SQLException.class, () ->
        dao.createPayment(-10.00, "Card", testOrder));
  }

  @Test
  @Order(4)
  void testCreatePaymentZeroAmount() throws SQLException
  {
    assertThrows(SQLException.class, () ->
        dao.createPayment(0.00, "Card", testOrder));
  }

  @Test
  @Order(5)
  void testCreatePaymentInvalidMethod() throws SQLException
  {
    assertThrows(SQLException.class, () ->
        dao.createPayment(50.00, "Bitcoin", testOrder));
  }

  @Test
  @Order(6)
  void testCreatePaymentNullOrder() throws SQLException
  {
    assertThrows(SQLException.class, () ->
        dao.createPayment(50.00, "Card", null));
  }

  @Test
  @Order(7)
  void testGetPaymentById() throws SQLException
  {
    Payment p = dao.getPaymentById(createdPaymentId);

    assertNotNull(p);
    assertEquals(createdPaymentId, p.getId());
    assertEquals(50.00, p.getAmount());
    assertEquals("Card", p.getMethod());
    assertNotNull(p.getOrderId());
  }

  @Test
  @Order(8)
  void testGetPaymentByIdNotFound() throws SQLException
  {
    assertThrows(SQLException.class, () ->
        dao.getPaymentById(-1));
  }

  @Test
  @Order(9)
  void testDeleteById() throws SQLException
  {
    dao.deleteById(createdPaymentId);

    assertThrows(SQLException.class, () ->
        dao.getPaymentById(createdPaymentId));
  }

  @Test
  @Order(10)
  void testDeleteByIdNotFound() throws SQLException
  {
    assertThrows(SQLException.class, () ->
        dao.deleteById(-1));
  }
}