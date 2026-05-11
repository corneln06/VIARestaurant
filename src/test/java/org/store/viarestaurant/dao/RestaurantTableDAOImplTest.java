package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.RestaurantTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestaurantTableDAOImplTest {

    private static RestaurantTableDAOImpl dao;
    private static int createdId;

    @BeforeAll
    static void setup() throws SQLException {
        dao = RestaurantTableDAOImpl.getInstance();
    }

    @Test
    @Order(1)
    void createTable() throws SQLException {
        RestaurantTable r = dao.createRestaurantTable(4);

        assertNotNull(r);
        assertTrue(r.getId()>0);

        assertEquals(4, r.getMaxSitting());
        assertEquals("Available", r.getStatus().getName());

        createdId = r.getId();
    }

    @Test
    @Order(2) void getAllTables() throws SQLException {
        ArrayList<RestaurantTable> list = dao.getAllRestaurantTables();

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }
    @Test
    @Order(3) void getTableByID() throws SQLException {

        RestaurantTable r = dao.getRestaurantTableByID(createdId);
        assertNotNull(r);
        assertEquals(createdId, r.getId());
        assertEquals(4, r.getMaxSitting());
        assertEquals("Available", r.getStatus().getName());

    }

    @Test
    @Order(4) void deleteTable() throws SQLException {
        dao.deleteRestaurantTableByID(createdId);
        assertThrows(NoSuchElementException.class,
                () -> dao.getRestaurantTableByID(createdId));
    }

}
