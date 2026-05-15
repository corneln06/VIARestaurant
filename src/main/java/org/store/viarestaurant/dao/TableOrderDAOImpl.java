package org.store.viarestaurant.dao;

import org.postgresql.Driver;
import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class TableOrderDAOImpl implements TableOrderDAO {

    private static TableOrderDAOImpl instance;


    private TableOrderDAOImpl() throws SQLException {

        DriverManager.registerDriver(new Driver());

    }

    public static synchronized TableOrderDAOImpl getInstance()
            throws SQLException {

        if (instance == null) {
            instance = new TableOrderDAOImpl();
        }

        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    private final RestaurantTableDAO restaurantTableDAO = RestaurantTableDAOImpl.getInstance();
    private final WorkersDAO workersDAO = WorkersDAOImpl.getInstance();
    private final MenuItemDAO menuItemDAO = MenuItemDAOImpl.getInstance();

    @Override
    public TableOrder createTableOrder(
            Integer tableId,
            Integer waiterId,
            String notes,
            double bill,
            ArrayList<String> menuItems,
            boolean isReservation
    ) throws SQLException {

        try (Connection connection = getConnection()) {

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

            if (rs.next()) {

                Integer id = rs.getInt("id");

                if (menuItems != null && !menuItems.isEmpty()) {
                    try (PreparedStatement menuItemStatement = connection.prepareStatement("INSERT INTO MenuItemsTableOrder (menuItemId, tableOrderId) VALUES (?, ?)")) {

                        for (String itemName : menuItems) {
                            MenuItems items = menuItemDAO.getMenuItemByName(itemName);

                            if (items != null) {
                                menuItemStatement.setInt(1, items.getId());
                                menuItemStatement.setInt(2, id);
                                menuItemStatement.executeUpdate();
                            }
                        }
                    }
                }
                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);

                Workers waiter = workersDAO.getWorkerById(waiterId);

                return new TableOrder(
                        id,
                        restaurantTable,
                        waiter,
                        notes,
                        menuItems,
                        bill,
                        isReservation
                );
            }

            throw new SQLException("No ID returned");
        }
    }

    @Override
    public ArrayList<TableOrder> getAllTableOrders()
            throws SQLException {

        try (Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT t.id, " +
                                    " t.tableid," +
                                    " t.waiterid," +
                                    " t.notes," +
                                    " t.bill ," +
                                    " t.isReservation as Reserved," +
                                    " t.isPaid as Paid," +
                                    "mi.name as ItemName" +
                                    "  FROM tableorders t" +
                                    " left join MenuItemsTableOrder mt on t.id = mt.tableorderid" +
                                    " left join menuitems mi on mt.menuitemid = mi.id " +
                                    "ORDER by t.id"
                    );

            ResultSet rs = statement.executeQuery();

            ArrayList<TableOrder> finalList =
                    new ArrayList<>();

            while (rs.next()) {

                Integer id = rs.getInt("id");
                Integer tableId = rs.getInt("tableid");
                Integer waiterId = rs.getInt("waiterid");
                String notes = rs.getString("notes");
                double bill = rs.getDouble("bill");
                boolean isReservation = rs.getBoolean("Reserved");
                boolean isPaid = rs.getBoolean("Paid");
                String itemName = rs.getString("ItemName");

                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);
                // TEMPORARY
                Workers waiter = workersDAO.getWorkerById(waiterId);

                TableOrder tableOrder =
                        new TableOrder(
                                id,
                                restaurantTable,
                                waiter,
                                notes,
                                new ArrayList<>(),
                                bill,
                                isReservation
                        );

                tableOrder.setPaid(isPaid);
                if (itemName != null)
                    tableOrder.addMenuItems(itemName);

                finalList.add(tableOrder);
            }

            return finalList;
        }
    }

    @Override
    public TableOrder getTableOrderByID(Integer id)
            throws SQLException {

        try (Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT t.id, " +
                                    " t.tableid," +
                                    " t.waiterid," +
                                    " t.notes," +
                                    " t.bill ," +
                                    " t.isReservation as Reserved," +
                                    " t.isPaid as Paid," +
                                    "mi.name as ItemName" +
                                    "  FROM tableorders t" +
                                    " left join MenuItemsTableOrder mt on t.id = mt.tableorderid" +
                                    " left join menuitems mi on mt.menuitemid = mi.id " +
                                    "where t.id = ?"
                    );

            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            TableOrder tableOrder = null;
            while (rs.next()) {
                if (tableOrder == null) {
                    Integer tableId = rs.getInt("tableid");
                    Integer waiterId = rs.getInt("waiterid");
                    String notes = rs.getString("notes");
                    double bill = rs.getDouble("bill");
                    boolean isReservation = rs.getBoolean("Reserved");
                    boolean isPaid = rs.getBoolean("Paid");
                    String itemName = rs.getString("ItemName");

                    RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);

                    // TEMPORARY
                    Workers waiter = workersDAO.getWorkerById(waiterId);
                    tableOrder =
                            new TableOrder(
                                    id,
                                    restaurantTable,
                                    waiter,
                                    notes,
                                    new ArrayList<>(),
                                    bill,
                                    isReservation
                            );


                    tableOrder.setPaid(isPaid);
                    if (itemName != null)
                        tableOrder.addMenuItems(itemName);

                }
            }
            if (tableOrder == null) {
                throw new SQLException(
                        "TableOrder with id " + id + " not found"
                );
            }
            return tableOrder;
        }
    }

    @Override
    public void deleteTableOrderByID(Integer id)
            throws SQLException {

        try (Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "DELETE FROM tableorders WHERE id=?"
                    );

            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {

                throw new SQLException(
                        "TableOrder with id " + id +
                                " not found"
                );
            }
        }
    }

    @Override
    public ArrayList<TableOrder> getTableOrdersByWaiterId(Integer waiterId) throws SQLException {
        try (Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT t.id, " +
                                    " t.tableid," +
                                    " t.waiterid," +
                                    " t.notes," +
                                    " t.bill ," +
                                    " t.isReservation as Reserved," +
                                    " t.isPaid as Paid," +
                                    "mi.name as ItemName" +
                                    "  FROM tableorders t" +
                                    " left join MenuItemsTableOrder mt on t.id = mt.tableorderid" +
                                    " left join menuitems mi on mt.menuitemid = mi.id " +
                                    "where t.waiterid = ?"
                    );

            statement.setInt(1, waiterId);

            ResultSet rs = statement.executeQuery();

            ArrayList<TableOrder> finalList =
                    new ArrayList<>();

            while (rs.next()) {

                Integer orderId = rs.getInt("id");
                Integer tableId = rs.getInt("tableid");
                String notes = rs.getString("notes");
                double bill = rs.getDouble("bill");
                boolean isReservation = rs.getBoolean("Reserved");
                boolean isPaid = rs.getBoolean("Paid");
                String itemName = rs.getString("ItemName");

                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);
                Workers waiter = workersDAO.getWorkerById(waiterId);

                TableOrder tableOrder =
                        new TableOrder(
                                orderId,
                                restaurantTable,
                                waiter,
                                notes,
                                new ArrayList<>(),
                                bill,
                                isReservation
                        );

                tableOrder.setPaid(isPaid);
                if (itemName != null)
                    tableOrder.addMenuItems(itemName);

                finalList.add(tableOrder);
            }

            return finalList;
        }
    }


    @Override
    public ArrayList<TableOrder> getTableOrdersByTableId(Integer tableId) throws SQLException {
        try (Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT t.id, " +
                                    " t.tableid," +
                                    " t.waiterid," +
                                    " t.notes," +
                                    " t.bill ," +
                                    " t.isReservation as Reserved," +
                                    " t.isPaid as Paid," +
                                    "mi.name as ItemName" +
                                    "  FROM tableorders t" +
                                    " left join MenuItemsTableOrder mt on t.id = mt.tableorderid" +
                                    " left join menuitems mi on mt.menuitemid = mi.id " +
                                    "where t.tableid = ?"
                    );

            statement.setInt(1, tableId);

            ResultSet rs = statement.executeQuery();

            ArrayList<TableOrder> finalList =
                    new ArrayList<>();

            while (rs.next()) {

                Integer orderId = rs.getInt("id");
                Integer waiterId = rs.getInt("waiterid");
                String notes = rs.getString("notes");
                double bill = rs.getDouble("bill");
                boolean isReservation = rs.getBoolean("Reserved");
                boolean isPaid = rs.getBoolean("Paid");
                String itemName = rs.getString("ItemName");

                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);
                Workers waiter = workersDAO.getWorkerById(waiterId);

                TableOrder tableOrder =
                        new TableOrder(
                                orderId,
                                restaurantTable,
                                waiter,
                                notes,
                                new ArrayList<>(),
                                bill,
                                isReservation
                        );

                tableOrder.setPaid(isPaid);
                if (itemName != null)
                    tableOrder.addMenuItems(itemName);

                finalList.add(tableOrder);
            }

            return finalList;
        }
    }

    @Override
    public TableOrder updateTableOrder(TableOrder tableOrder) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "update tableorders set tableId=?, " +
                            "waiterId=?, " +
                            "notes=?, " +
                            "bill=?, " +
                            "isReservation=?, " +
                            "isPaid=? " +
                            "WHERE id = ?"
            );
            statement.setInt(1, tableOrder.getTable().getId());
            statement.setInt(2, tableOrder.getWaiter().getId());
            statement.setString(3, tableOrder.getNotes());
            statement.setDouble(4, tableOrder.getBill());
            statement.setBoolean(5, tableOrder.isReservation());
            statement.setBoolean(6, tableOrder.isPaid());
            statement.setInt(7, tableOrder.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException(
                        "Table Order with id " + tableOrder.getId() + " not found"
                );
            }
            else
                return tableOrder;
        }
    }
}