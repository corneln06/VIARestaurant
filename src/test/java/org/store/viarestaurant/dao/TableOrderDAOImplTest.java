package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.MenuTypes;
import org.store.viarestaurant.model.enums.WorkerRole;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableOrderDAOImplTest {

    private TableOrderDAO dao;
    private RestaurantTable testTable, testTable2;
    private Workers testWorker, testWorker2;
    private MenuItemDAO menuItemDAO;
    private MenuItemTableOrderDAO menuItemTableOrderDAO;
    private AllergyDAO allergyDAO;
    ArrayList<String> allergiesList = new ArrayList<>();
    ArrayList<String> menuItemList = new ArrayList<>();
    private int createdId;
    private MenuItems newItem2;
    private int createdWaiterId ;
    private int createdTableId;

    @BeforeAll
    void setup() throws SQLException {
        dao = TableOrderDAOImpl.getInstance();
        menuItemDAO = MenuItemDAOImpl.getInstance();
        menuItemTableOrderDAO = MenuItemTableOrderDAOImpl.getInstance();
        allergyDAO = AllergyDAOImpl.getInstance();
        testTable = RestaurantTableDAOImpl.getInstance().createRestaurantTable(4);
        testTable2 = RestaurantTableDAOImpl.getInstance().createRestaurantTable(6);
        testWorker = WorkersDAOImpl.getInstance().createWorkers("adam","adam","email@gmail.com","pass", WorkerRole.Waiter);
        testWorker2 = WorkersDAOImpl.getInstance().createWorkers("NOTadam","NOTadam","email2@gmail.com","pass", WorkerRole.Waiter);
        allergiesList.add("Peanuts");
        MenuItems newItem = menuItemDAO.createMenuItem("Asado", MenuTypes.Main, 33.33, false, allergiesList);
        newItem2 = menuItemDAO.createMenuItem("Asado", MenuTypes.Main, 33.33, false, allergiesList);
        menuItemList.add(newItem.getName());
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
                menuItemList,
                false
        );

        assertNotNull(order);
        assertTrue(order.getId() > 0);

        assertEquals(testTable.getId(), order.getTable().getId());
        assertEquals(testWorker.getId(), order.getWaiter().getId());
        assertEquals("No onions", order.getNotes());
        assertEquals(25.50, order.getBill());
        assertEquals(menuItemList, order.getMenuItems());
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
        assertEquals(testTable.getId(), order.getTable().getId());
        assertEquals(testWorker.getId(), order.getWaiter().getId());
        assertEquals("No onions", order.getNotes());
        assertEquals(25.50, order.getBill());
        assertEquals(menuItemList, order.getMenuItems());
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
    void testUpdateTable() throws SQLException {

        TableOrder order = dao.getTableOrderByID(createdId);
        order.setTable(testTable2);
        order.setWaiter(testWorker2);

        order.addMenuItems(newItem2.getName());
        menuItemTableOrderDAO.addMenuItemsInTableOrder(order.getId(), order.getMenuItems());
        menuItemList.add(newItem2.getName());

        order.setBill(order.getBill()+ newItem2.getPrice());

        order = dao.updateTableOrder(order);


        assertNotNull(order);
        assertEquals(createdId, order.getId());
        assertEquals(testTable2, order.getTable());
        assertEquals(testWorker2.getId(), order.getWaiter().getId());
        assertEquals("No onions", order.getNotes());
        assertEquals(25.50+33.33 , order.getBill());
        assertEquals(menuItemList, order.getMenuItems());
        assertFalse(order.isReservation());
    }

    @Test
    @Order(7)
    void delete() throws SQLException {

        dao.deleteTableOrderByID(createdId);

        assertThrows(SQLException.class, () ->
                dao.getTableOrderByID(createdId)
        );
    }
}