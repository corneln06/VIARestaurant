package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.MenuItems;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public interface MenuItemTableOrderDAO {
    void deleteMenuItemsFromTableOrder(Integer id, ArrayList<String> menuItems) throws SQLException;
    void addMenuItemsInTableOrder(Connection connection, Integer id, ArrayList<String> menuItems) throws SQLException;
    ArrayList<MenuItems> getMenuItemsByTableOrderId(Integer id) throws SQLException;
}
