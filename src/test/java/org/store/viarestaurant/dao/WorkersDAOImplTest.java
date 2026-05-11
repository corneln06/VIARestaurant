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
  private static Workers createdWorker;

  @BeforeAll
  static void setup() throws SQLException
  {
    workersDAO = WorkersDAOImpl.getInstance();
  }

  @Test
  @Order(1)
  void createWorkers() throws SQLException
  {
    createdWorker = workersDAO.createWorkers(
        "John",
        "Doe",
        "john@test.com",
        "1234",
        WorkerRole.Waiter
    );

    assertNotNull(createdWorker);
    assertEquals("John", createdWorker.getFirstName());
    assertEquals("Doe", createdWorker.getLastName());
    assertEquals("john@test.com", createdWorker.getEmail());
    assertEquals(WorkerRole.Waiter, createdWorker.getRole());
  }

  @Test
  @Order(2)
  void getAllWorkers() throws SQLException
  {
    ArrayList<Workers> workers = workersDAO.getAllWorkers();

    assertNotNull(workers);
    assertFalse(workers.isEmpty());
  }

  @Test
  @Order(3)
  void getWorkerById() throws SQLException
  {
    Workers worker =
        workersDAO.getWorkerById(createdWorker.getId());

    assertNotNull(worker);
    assertEquals(createdWorker.getId(), worker.getId());
    assertEquals("john@test.com", worker.getEmail());
  }

  @Test
  @Order(4)
  void getWorkerByEmail() throws SQLException
  {
    Workers worker =
        workersDAO.getWorkerByEmail("john@test.com");

    assertNotNull(worker);
    assertEquals("John", worker.getFirstName());
    assertEquals("Doe", worker.getLastName());
  }

  @Test
  @Order(5)
  void deleteWorkerById() throws SQLException
  {
    assertDoesNotThrow(() ->
        workersDAO.deleteWorkerById(createdWorker.getId())
    );
  }

  @Test
  @Order(6)
  void deletedWorkerShouldNotExist()
  {
    assertThrows(SQLException.class, () ->
        workersDAO.getWorkerById(createdWorker.getId())
    );
  }
}
