package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.*;
import org.store.viarestaurant.model.enums.MenuTypes;
import org.store.viarestaurant.model.enums.WorkerRole;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableOrderDAOImplTest {

    private TableOrderDAO dao;

    private RestaurantTable testTable;
    private RestaurantTable testTable2;

    private Workers testWorker;
    private Workers testWorker2;

    private MenuItemDAO menuItemDAO;

    private ArrayList<OrderItem> menuItemList =
            new ArrayList<>();

    private MenuItems item1;
    private MenuItems item2;

    private int createdId;
    private int createdWaiterId;
    private int createdTableId;

    @BeforeAll
    void setup() throws SQLException {

        dao = TableOrderDAOImpl.getInstance();

        menuItemDAO =
                MenuItemDAOImpl.getInstance();

        testTable =
                RestaurantTableDAOImpl
                        .getInstance()
                        .createRestaurantTable(4);

        testTable2 =
                RestaurantTableDAOImpl
                        .getInstance()
                        .createRestaurantTable(6);

        testWorker =
                WorkersDAOImpl
                        .getInstance()
                        .createWorkers(
                                "adam",
                                "adam",
                                "email@gmail.com",
                                "pass",
                                WorkerRole.Waiter
                        );

        testWorker2 =
                WorkersDAOImpl
                        .getInstance()
                        .createWorkers(
                                "john",
                                "john",
                                "email2@gmail.com",
                                "pass",
                                WorkerRole.Waiter
                        );

        ArrayList<String> allergies =
                new ArrayList<>();

        allergies.add("Peanuts");

        item1 =
                menuItemDAO.createMenuItem(
                        "Burger",
                        MenuTypes.Main,
                        25.50,
                        false,
                        allergies
                );

        item2 =
                menuItemDAO.createMenuItem(
                        "Pizza",
                        MenuTypes.Main,
                        33.33,
                        false,
                        allergies
                );

        menuItemList.add(
                new OrderItem(
                        0,
                        item1,
                        1
                )
        );

        createdTableId =
                testTable.getId();

        createdWaiterId =
                testWorker.getId();
    }

    @Test
    @Order(1)
    void create() throws SQLException {

        TableOrder order =
                dao.createTableOrder(
                        testTable.getId(),
                        testWorker.getId(),
                        "No onions",
                        25.50,
                        menuItemList,
                        false
                );

        assertNotNull(order);

        assertTrue(order.getId() > 0);

        assertEquals(
                testTable.getId(),
                order.getTable().getId()
        );

        assertEquals(
                testWorker.getId(),
                order.getWaiter().getId()
        );

        assertEquals(
                "No onions",
                order.getNotes()
        );

        assertEquals(
                25.50,
                order.getBill()
        );

        assertEquals(
                1,
                order.getMenuItems().size()
        );

        assertFalse(order.isReservation());

        createdId = order.getId();
    }

    @Test
    @Order(2)
    void getAll() throws SQLException {

        ArrayList<TableOrder> list =
                dao.getAllTableOrders();

        assertNotNull(list);

        assertFalse(list.isEmpty());
    }

    @Test
    @Order(3)
    void getByID() throws SQLException {

        TableOrder order =
                dao.getTableOrderByID(createdId);

        assertNotNull(order);

        assertEquals(
                createdId,
                order.getId()
        );

        assertEquals(
                testTable.getId(),
                order.getTable().getId()
        );

        assertEquals(
                testWorker.getId(),
                order.getWaiter().getId()
        );

        assertEquals(
                "No onions",
                order.getNotes()
        );

        assertEquals(
                25.50,
                order.getBill()
        );

        assertEquals(
                1,
                order.getMenuItems().size()
        );

        assertEquals(
                "Burger",
                order.getMenuItems()
                        .get(0)
                        .getMenuItem()
                        .getName()
        );

        assertFalse(order.isReservation());
    }

    @Test
    @Order(4)
    void getByWorkerID() throws SQLException {

        ArrayList<TableOrder> list =
                dao.getTableOrdersByWaiterId(
                        createdWaiterId
                );

        assertNotNull(list);

        assertTrue(
                list.stream()
                        .anyMatch(
                                o -> o.getId() == createdId
                        )
        );
    }

    @Test
    @Order(5)
    void getByTableID() throws SQLException {

        ArrayList<TableOrder> list =
                dao.getTableOrdersByTableId(
                        createdTableId
                );

        assertNotNull(list);

        assertTrue(
                list.stream()
                        .anyMatch(
                                o -> o.getId() == createdId
                        )
        );
    }

    @Test
    @Order(6)
    void update() throws SQLException {

        TableOrder order = dao.getTableOrderByID(createdId);
        order.setTable(testTable2);
        order.setWaiter(testWorker2);
        order.getMenuItems().add(new OrderItem(0, item2, 2));

        order.setBill(order.getBill() + (item2.getPrice() * 2));

        TableOrder updated = dao.updateTableOrder(order);

        assertNotNull(updated);
        assertEquals(createdId,updated.getId());
        assertEquals(testTable2.getId(), updated.getTable().getId());
        assertEquals(testWorker2.getId(), updated.getWaiter().getId());

        assertEquals(2, updated.getMenuItems().size());

        assertEquals(92.16, updated.getBill());
    }

    @Test
    @Order(7)
    void delete() throws SQLException {

        dao.deleteTableOrderByID(createdId);

        assertThrows(
                Exception.class,
                () -> dao.getTableOrderByID(createdId)
        );
    }
}