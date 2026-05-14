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
    ArrayList<String> allergiesList = new ArrayList<>();

    @BeforeAll
    void setup() throws SQLException {
        dao = MenuItemDAOImpl.getInstance();
        AllergyDAOImpl.getInstance().createAllergy("Peanuts");

        allergiesList.add(AllergyDAOImpl.getInstance().getAllergyByName("Peanuts").getName());
    }

    @Test
    @Order(1)
    void testCreateMenuItem() throws SQLException
    {

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
    void testUpdateMenuItem() throws SQLException
    {
        MenuItems menuItem = dao.getMenuItemById(createdId);

        menuItem.setName("NotAsado");
        menuItem.setPrice(menuItem.getPrice()+10.1);

        dao.updateMenuItem(menuItem);

        MenuItems menuItem1 = dao.getMenuItemById(createdId);

        menuItem.setType(MenuTypes.Beverage); /// Intentional

                assertNotNull(menuItem1);
        assertTrue(menuItem1.getId() > 0);
        assertEquals("NotAsado", menuItem1.getName());
        assertEquals(MenuTypes.Main, menuItem1.getType());
        assertEquals(43.43, menuItem1.getPrice());
        assertFalse(menuItem1.isVegetarian());
        assertFalse(menuItem1.getAllergies().isEmpty());
    }

    @Test
    @Order(6)
    void testDeleteById() throws SQLException
    {
        dao.delete(createdId);
        assertNull(dao.getMenuItemById(createdId));
    }
}
