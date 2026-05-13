package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.entities.TableOrder;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TableOrderDAO {

    TableOrder createTableOrder(
            Integer tableId,
            Integer waiterId,
            String notes,
            double bill,
            ArrayList<String> menuItems,
            boolean isReservation
    ) throws SQLException;

    ArrayList<TableOrder> getAllTableOrders() throws SQLException;
    TableOrder getTableOrderByID(Integer id) throws SQLException;
    void deleteTableOrderByID(Integer id) throws SQLException;
    ArrayList<TableOrder> getTableOrdersByWaiterId(Integer id) throws SQLException;
    ArrayList<TableOrder> getTableOrdersByTableId(Integer id) throws SQLException;
    TableOrder updateTableOrder(TableOrder tableOrder) throws SQLException;
}