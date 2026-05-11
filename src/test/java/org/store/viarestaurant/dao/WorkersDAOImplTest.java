package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkersDAOImplTest
{
  private static WorkersDAOImpl workersDAO;

  private static Workers waiterWorker;
  private static Workers hostWorker;
  private static Workers managerWorker;

  @BeforeAll
  static void setup() throws SQLException
  {
    workersDAO = WorkersDAOImpl.getInstance();
  }

  @Test
  @Order(1)
  void createWaiter() throws SQLException
  {
    waiterWorker = workersDAO.createWorkers(
        "Cornel",
        "Negru",
        "waiter@test.com",
        "1234",
        WorkerRole.Waiter
    );

    assertNotNull(waiterWorker);

    assertEquals("Cornel", waiterWorker.getFirstName());
    assertEquals("Negru", waiterWorker.getLastName());
    assertEquals("waiter@test.com", waiterWorker.getEmail());
    assertEquals(WorkerRole.Waiter, waiterWorker.getRole());
  }

  @Test
  @Order(2)
  void createHost() throws SQLException
  {
    hostWorker = workersDAO.createWorkers(
        "John",
        "Doe",
        "host@test.com",
        "1234",
        WorkerRole.Host
    );

    assertNotNull(hostWorker);

    assertEquals("John", hostWorker.getFirstName());
    assertEquals("Doe", hostWorker.getLastName());
    assertEquals("host@test.com", hostWorker.getEmail());
    assertEquals(WorkerRole.Host, hostWorker.getRole());
  }

  @Test
  @Order(3)
  void createManager() throws SQLException
  {
    managerWorker = workersDAO.createWorkers(
        "Ada",
        "Smith",
        "manager@test.com",
        "1234",
        WorkerRole.Manager
    );

    assertNotNull(managerWorker);

    assertEquals("Ada", managerWorker.getFirstName());
    assertEquals("Smith", managerWorker.getLastName());
    assertEquals("manager@test.com", managerWorker.getEmail());
    assertEquals(WorkerRole.Manager, managerWorker.getRole());
  }

  @Test
  @Order(4)
  void getAllWorkers() throws SQLException
  {
    ArrayList<Workers> workers = workersDAO.getAllWorkers();

    assertNotNull(workers);
    assertFalse(workers.isEmpty());
  }

  @Test
  @Order(5)
  void getWorkerById() throws SQLException
  {
    Workers worker =
        workersDAO.getWorkerById(waiterWorker.getId());

    assertNotNull(worker);

    assertEquals(waiterWorker.getId(), worker.getId());
    assertEquals("waiter@test.com", worker.getEmail());
  }

  @Test
  @Order(6)
  void getWorkerByEmail() throws SQLException
  {
    Workers worker =
        workersDAO.getWorkerByEmail("manager@test.com");

    assertNotNull(worker);

    assertEquals("Ada", worker.getFirstName());
    assertEquals("Smith", worker.getLastName());
    assertEquals(WorkerRole.Manager, worker.getRole());
  }

  @Test
  @Order(7)
  void deleteWorkers() throws SQLException
  {
    assertDoesNotThrow(() ->
        workersDAO.deleteWorkerById(waiterWorker.getId())
    );

    assertDoesNotThrow(() ->
        workersDAO.deleteWorkerById(hostWorker.getId())
    );

    assertDoesNotThrow(() ->
        workersDAO.deleteWorkerById(managerWorker.getId())
    );
  }

  @Test
  @Order(8)
  void deletedWorkersShouldNotExist()
  {
    assertThrows(SQLException.class, () ->
        workersDAO.getWorkerById(waiterWorker.getId())
    );

    assertThrows(SQLException.class, () ->
        workersDAO.getWorkerById(hostWorker.getId())
    );

    assertThrows(SQLException.class, () ->
        workersDAO.getWorkerById(managerWorker.getId())
    );
  }
}