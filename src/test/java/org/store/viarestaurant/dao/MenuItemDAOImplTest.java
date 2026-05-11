package org.store.viarestaurant.dao;

import org.junit.jupiter.api.*;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.enums.MenuTypes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MenuItemDAOImplTest {
    private MenuItemDAOImpl dao;
    private int createdId;

    @BeforeAll
    void setup() throws SQLException {
        dao = MenuItemDAOImpl.getInstance();
    }

    @Test
    @Order(1)
    void testCreateMenuItem() throws SQLException
    {
        ArrayList<String> allergiesList = new ArrayList<>();
        allergiesList.add("Peanuts");
        MenuItems newItem = dao.createMenuItem("Asado", MenuTypes.Main, 33.33, false, allergiesList);

        assertNotNull(newItem);
        assertTrue(newItem.getId() > 0);
        assertEquals("Asado", newItem.getName());
        assertEquals(MenuTypes.Main, newItem.getType());
        assertEquals(33.33, newItem.getPrice());
        assertFalse(newItem.isVegetarian());
        assertEquals(allergiesList, newItem.getAllergies());

        createdId = newItem.getId();
    }

    @Test
    @Order(2)
    void testGetMenuItemByID() throws SQLException {
        MenuItems menuItem = dao.getMenuItemById(createdId);

        assertNotNull(menuItem);
        assertTrue(menuItem.getId() > 0);
        assertEquals("Asado", menuItem.getName());
        assertEquals(MenuTypes.Main, menuItem.getType());
        assertEquals(33.33, menuItem.getPrice());
        assertFalse(menuItem.isVegetarian());
        assertFalse(menuItem.getAllergies().isEmpty());
    }

    @Test
    @Order(3)
    void testGetAllMenuItems() throws SQLException
    {
        List<MenuItems> list = dao.getAllMenuItems();

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(4)
    void testDeleteById() throws SQLException
    {
        dao.delete(createdId);
        assertNull(dao.getMenuItemById(createdId));
    }
}
