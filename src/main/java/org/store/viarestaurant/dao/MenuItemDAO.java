package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.enums.MenuTypes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface MenuItemDAO {

    MenuItems createMenuItem(String name, MenuTypes type, Double price, boolean isVegetarian, ArrayList<String> allergiesId) throws SQLException;
    List<MenuItems> getAllMenuItems() throws SQLException;
    MenuItems getMenuItemById(Integer id) throws SQLException;
    void delete(Integer id) throws SQLException;
    MenuItems getMenuItemByName(String name) throws SQLException;
    MenuItems updateMenuItem(MenuItems item) throws SQLException;
}
