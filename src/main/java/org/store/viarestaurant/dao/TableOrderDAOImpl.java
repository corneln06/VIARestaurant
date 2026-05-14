package org.store.viarestaurant.dao;

import org.postgresql.Driver;
import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class TableOrderDAOImpl implements TableOrderDAO {

    private static TableOrderDAOImpl instance;

    private final RestaurantTableDAO restaurantTableDAO =
            RestaurantTableDAOImpl.getInstance();

    private final WorkersDAO workersDAO =
            WorkersDAOImpl.getInstance();

    private final MenuItemDAO menuItemDAO =
            MenuItemDAOImpl.getInstance();

    private TableOrderDAOImpl() throws SQLException {
        DriverManager.registerDriver(new Driver());
    }

    public static synchronized TableOrderDAOImpl getInstance()
            throws SQLException {

        if(instance == null) {
            instance = new TableOrderDAOImpl();
        }

        return instance;
    }

    private Connection getConnection()
            throws SQLException {

        return DatabaseConnection.getConnection();
    }

    @Override
    public TableOrder createTableOrder(
            Integer tableId,
            Integer waiterId,
            String notes,
            double bill,
            ArrayList<OrderItem> menuItems,
            boolean isReservation
    ) throws SQLException {

        try(Connection connection = getConnection()) {

            connection.setAutoCommit(false);

            PreparedStatement statement =
                    connection.prepareStatement(

                            "INSERT INTO tableorders " +
                                    "(tableid, waiterid, notes, bill, " +
                                    "isreservation, ispaid) " +

                                    "VALUES (?, ?, ?, ?, ?, ?) " +

                                    "RETURNING id"
                    );

            statement.setInt(1, tableId);
            statement.setInt(2, waiterId);
            statement.setString(3, notes);
            statement.setDouble(4, bill);
            statement.setBoolean(5, isReservation);
            statement.setBoolean(6, false);

            ResultSet rs = statement.executeQuery();

            if(!rs.next()) {
                throw new SQLException(
                        "Failed to create TableOrder"
                );
            }

            Integer orderId = rs.getInt("id");

            if(menuItems != null && !menuItems.isEmpty()) {

                PreparedStatement menuItemStatement =
                        connection.prepareStatement(

                                "INSERT INTO MenuItemsTableOrder " +
                                        "(menuitemid, tableorderid, quantity) " +

                                        "VALUES (?, ?, ?)"
                        );

                for(OrderItem item : menuItems) {

                    menuItemStatement.setInt(
                            1,
                            item.getMenuItem().getId()
                    );

                    menuItemStatement.setInt(
                            2,
                            orderId
                    );

                    menuItemStatement.setInt(
                            3,
                            item.getQuantity()
                    );

                    menuItemStatement.executeUpdate();
                }
            }

            connection.commit();

            RestaurantTable restaurantTable =
                    restaurantTableDAO
                            .getRestaurantTableByID(tableId);

            Workers waiter =
                    workersDAO
                            .getWorkerById(waiterId);

            TableOrder tableOrder =
                    new TableOrder(
                            orderId,
                            restaurantTable,
                            waiter,
                            notes,
                            menuItems,
                            bill,
                            isReservation
                    );

            tableOrder.setPaid(false);

            return tableOrder;
        }
    }

    @Override
    public ArrayList<TableOrder> getAllTableOrders()
            throws SQLException {

        try(Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(

                            "SELECT * FROM tableorders"
                    );

            ResultSet rs = statement.executeQuery();

            ArrayList<TableOrder> finalList =
                    new ArrayList<>();

            while(rs.next()) {

                Integer orderId =
                        rs.getInt("id");

                Integer tableId =
                        rs.getInt("tableid");

                Integer waiterId =
                        rs.getInt("waiterid");

                String notes =
                        rs.getString("notes");

                double bill =
                        rs.getDouble("bill");

                boolean isReservation =
                        rs.getBoolean("isreservation");

                boolean isPaid =
                        rs.getBoolean("ispaid");

                RestaurantTable restaurantTable =
                        restaurantTableDAO
                                .getRestaurantTableByID(tableId);

                Workers waiter =
                        workersDAO
                                .getWorkerById(waiterId);

                ArrayList<OrderItem> items =
                        getOrderItemsByOrderId(orderId);

                TableOrder tableOrder =
                        new TableOrder(
                                orderId,
                                restaurantTable,
                                waiter,
                                notes,
                                items,
                                bill,
                                isReservation
                        );

                tableOrder.setPaid(isPaid);

                finalList.add(tableOrder);
            }

            return finalList;
        }
    }

    @Override
    public TableOrder getTableOrderByID(Integer id)
            throws SQLException {

        try(Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(

                            "SELECT * FROM tableorders " +
                                    "WHERE id=?"
                    );

            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            if(!rs.next()) {

                throw new NoSuchElementException(
                        "TableOrder with id " +
                                id +
                                " not found"
                );
            }

            Integer tableId =
                    rs.getInt("tableid");

            Integer waiterId =
                    rs.getInt("waiterid");

            String notes =
                    rs.getString("notes");

            double bill =
                    rs.getDouble("bill");

            boolean isReservation =
                    rs.getBoolean("isreservation");

            boolean isPaid =
                    rs.getBoolean("ispaid");

            RestaurantTable restaurantTable =
                    restaurantTableDAO
                            .getRestaurantTableByID(tableId);

            Workers waiter =
                    workersDAO
                            .getWorkerById(waiterId);

            ArrayList<OrderItem> items =
                    getOrderItemsByOrderId(id);

            TableOrder tableOrder =
                    new TableOrder(
                            id,
                            restaurantTable,
                            waiter,
                            notes,
                            items,
                            bill,
                            isReservation
                    );

            tableOrder.setPaid(isPaid);

            return tableOrder;
        }
    }

    private ArrayList<OrderItem>
    getOrderItemsByOrderId(Integer orderId)
            throws SQLException {

        try(Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(

                            "SELECT * FROM MenuItemsTableOrder " +
                                    "WHERE tableorderid=?"
                    );

            statement.setInt(1, orderId);

            ResultSet rs = statement.executeQuery();

            ArrayList<OrderItem> items =
                    new ArrayList<>();

            while(rs.next()) {

                Integer orderItemId =
                        rs.getInt("id");

                Integer menuItemId =
                        rs.getInt("menuitemid");

                Integer quantity =
                        rs.getInt("quantity");

                MenuItems menuItem =
                        menuItemDAO
                                .getMenuItemById(menuItemId);

                items.add(
                        new OrderItem(
                                orderItemId,
                                menuItem,
                                quantity
                        )
                );
            }

            return items;
        }
    }

    @Override
    public void deleteTableOrderByID(Integer id)
            throws SQLException {

        try(Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(

                            "DELETE FROM tableorders " +
                                    "WHERE id=?"
                    );

            statement.setInt(1, id);

            int rowsAffected =
                    statement.executeUpdate();

            if(rowsAffected == 0) {

                throw new SQLException(
                        "TableOrder with id " +
                                id +
                                " not found"
                );
            }
        }
    }

    @Override
    public ArrayList<TableOrder>
    getTableOrdersByWaiterId(Integer waiterId)
            throws SQLException {

        try(Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(

                            "SELECT * FROM tableorders " +
                                    "WHERE waiterid=?"
                    );

            statement.setInt(1, waiterId);

            ResultSet rs = statement.executeQuery();

            ArrayList<TableOrder> finalList =
                    new ArrayList<>();

            while(rs.next()) {

                Integer orderId =
                        rs.getInt("id");

                finalList.add(
                        getTableOrderByID(orderId)
                );
            }

            return finalList;
        }
    }

    @Override
    public ArrayList<TableOrder>
    getTableOrdersByTableId(Integer tableId)
            throws SQLException {

        try(Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(

                            "SELECT * FROM tableorders " +
                                    "WHERE tableid=?"
                    );

            statement.setInt(1, tableId);

            ResultSet rs = statement.executeQuery();

            ArrayList<TableOrder> finalList =
                    new ArrayList<>();

            while(rs.next()) {

                Integer orderId =
                        rs.getInt("id");

                finalList.add(
                        getTableOrderByID(orderId)
                );
            }

            return finalList;
        }
    }

    @Override
    public TableOrder updateTableOrder(
            TableOrder tableOrder
    ) throws SQLException {

        try(Connection connection = getConnection()) {

            connection.setAutoCommit(false);

            PreparedStatement statement =
                    connection.prepareStatement(

                            "UPDATE tableorders SET " +
                                    "tableid=?, " +
                                    "waiterid=?, " +
                                    "notes=?, " +
                                    "bill=?, " +
                                    "isreservation=?, " +
                                    "ispaid=? " +

                                    "WHERE id=?"
                    );

            statement.setInt(
                    1,
                    tableOrder.getTable().getId()
            );

            statement.setInt(
                    2,
                    tableOrder.getWaiter().getId()
            );

            statement.setString(
                    3,
                    tableOrder.getNotes()
            );

            statement.setDouble(
                    4,
                    tableOrder.getBill()
            );

            statement.setBoolean(
                    5,
                    tableOrder.isReservation()
            );

            statement.setBoolean(
                    6,
                    tableOrder.isPaid()
            );

            statement.setInt(
                    7,
                    tableOrder.getId()
            );

            int rowsAffected =
                    statement.executeUpdate();

            if(rowsAffected == 0) {

                throw new SQLException(
                        "TableOrder with id " +
                                tableOrder.getId() +
                                " not found"
                );
            }

            updateMenuItemList(tableOrder.getMenuItems(), tableOrder.getId(), connection);

            connection.commit();

            return tableOrder;
        }
    }
    @Override
    public void updateMenuItemList(
            ArrayList<OrderItem> items,
            Integer tableOrderId, Connection connection
    ) throws SQLException {

        try(connection) {

            for(OrderItem orderItem : items) {

                PreparedStatement checkStatement =
                        connection.prepareStatement(
                                "SELECT quantity " +
                                        "FROM MenuItemsTableOrder " +
                                        "WHERE menuItemId=? AND tableOrderId=?"
                        );

                checkStatement.setInt(
                        1,
                        orderItem.getMenuItem().getId()
                );

                checkStatement.setInt(2, tableOrderId);

                ResultSet rs = checkStatement.executeQuery();

                if(rs.next()) {

                    int currentQuantity =
                            rs.getInt("quantity");

                    PreparedStatement updateStatement =
                            connection.prepareStatement(
                                    "UPDATE MenuItemsTableOrder " +
                                            "SET quantity=? " +
                                            "WHERE menuItemId=? " +
                                            "AND tableOrderId=?"
                            );

                    updateStatement.setInt(
                            1,
                            currentQuantity +
                                    orderItem.getQuantity()
                    );

                    updateStatement.setInt(
                            2,
                            orderItem.getMenuItem().getId()
                    );

                    updateStatement.setInt(
                            3,
                            tableOrderId
                    );

                    updateStatement.executeUpdate();

                } else {

                    PreparedStatement insertStatement =
                            connection.prepareStatement(
                                    "INSERT INTO MenuItemsTableOrder " +
                                            "(menuItemId, tableOrderId, quantity) " +
                                            "VALUES (?, ?, ?)"
                            );

                    insertStatement.setInt(
                            1,
                            orderItem.getMenuItem().getId()
                    );

                    insertStatement.setInt(
                            2,
                            tableOrderId
                    );

                    insertStatement.setInt(
                            3,
                            orderItem.getQuantity()
                    );

                    insertStatement.executeUpdate();
                }
            }
        }
    }
}