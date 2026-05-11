package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableOrderDAOImplTest {

    private TableOrderDAOImpl dao;
    private RestaurantTable testTable;
    private Workers testWorker;
    private int createdId;
    private int createdWaiterId ;
    private int createdTableId;

    @BeforeAll
    void setup() throws SQLException {
        dao = TableOrderDAOImpl.getInstance();
        testTable = RestaurantTableDAOImpl.getInstance().createRestaurantTable(4);
        testWorker = WorkersDAOImpl.getInstance().createWorkers("adam","adam","email@gmail.com","pass", WorkerRole.Waiter);
        createdTableId = testTable.getId();
        createdWaiterId = testWorker.getId();
    }

    @Test
    @Order(1)
    void create() throws SQLException {

        TableOrder order = dao.createTableOrder(
                testTable.getId(),
                testWorker.getId(),
                "No onions",
                25.50,
                false
        );

        assertNotNull(order);
        assertTrue(order.getId() > 0);

        assertEquals(testTable.getId(), order.getTable().getId());
        assertEquals("No onions", order.getNotes());
        assertEquals(25.50, order.getBill());
        assertFalse(order.isReservation());

        createdId = order.getId();

    }

    @Test
    @Order(2)
    void getall() throws SQLException {

        ArrayList<TableOrder> list = dao.getAllTableOrders();

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(3)
    void getByID() throws SQLException {

        TableOrder order = dao.getTableOrderByID(createdId);

        assertNotNull(order);
        assertEquals(createdId, order.getId());
        assertEquals("No onions", order.getNotes());
        assertEquals(25.50, order.getBill());
        assertFalse(order.isReservation());
    }

    @Test
    @Order(4)
    void getByworkerID() throws SQLException {

        ArrayList<TableOrder> list =
                dao.getTableOrdersByWaiterId(createdWaiterId);

        assertNotNull(list);

        assertTrue(list.stream()
                .anyMatch(o -> o.getId() == createdId));
    }
    @Test
    @Order(5)
    void getBytableID() throws SQLException {

        ArrayList<TableOrder> list =
                dao.getTableOrdersByTableId(createdTableId);

        assertNotNull(list);

        assertTrue(list.stream()
                .anyMatch(o -> o.getId() == createdId));
    }

    @Test
    @Order(6)
    void delete() throws SQLException {

        dao.deleteTableOrderByID(createdId);

        assertThrows(Exception.class, () ->
                dao.getTableOrderByID(createdId)
        );
    }
}