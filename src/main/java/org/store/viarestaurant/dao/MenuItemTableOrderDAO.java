package org.store.viarestaurant.dao;

import java.sql.SQLException;
import java.util.ArrayList;

public interface MenuItemTableOrderDAO {
    void deleteMenuItemsFromTableOrder(Integer id, ArrayList<String> menuItems) throws SQLException;
    void addMenuItemsInTableOrder(Integer id, ArrayList<String> menuItems) throws SQLException;
}
