package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.entities.Waiter;
import org.store.viarestaurant.model.entities.Workers;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class TableOrderDAOImpl implements TableOrderDAO {

    private static TableOrderDAOImpl instance;
    private static RestaurantTableDAOImpl restaurantTableDAO;
    private static WorkersDAOImpl workersDAO;

    private TableOrderDAOImpl() throws SQLException {

        DriverManager.registerDriver(new org.postgresql.Driver());
        restaurantTableDAO = RestaurantTableDAOImpl.getInstance();
        workersDAO = WorkersDAOImpl.getInstance();
    }

    public static synchronized TableOrderDAOImpl getInstance()
            throws SQLException {

        if(instance == null) {
            instance = new TableOrderDAOImpl();
        }

        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public TableOrder createTableOrder(
            Integer tableId,
            Integer waiterId,
            String notes,
            double bill,
            boolean isReservation
    ) throws SQLException {

        try(Connection connection = getConnection()) {

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

            if(rs.next()) {

                Integer id = rs.getInt("id");

                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);

                Workers waiter = workersDAO.getWorkerById(waiterId);

                return new TableOrder(
                        id,
                        restaurantTable,
                        waiter,
                        notes,
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

        try(Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT * FROM tableorders"
                    );

            ResultSet rs = statement.executeQuery();

            ArrayList<TableOrder> finalList =
                    new ArrayList<>();

            while(rs.next()) {

                Integer id = rs.getInt("id");
                Integer tableId = rs.getInt("tableid");
                Integer waiterId = rs.getInt("waiterid");
                String notes = rs.getString("notes");
                double bill = rs.getDouble("bill");
                boolean isReservation = rs.getBoolean("isreservation");
                boolean isPaid = rs.getBoolean("ispaid");

                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);
                // TEMPORARY
                Workers waiter = workersDAO.getWorkerById(waiterId);

                TableOrder tableOrder =
                        new TableOrder(
                                id,
                                restaurantTable,
                                waiter,
                                notes,
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
                            "SELECT * FROM tableorders WHERE id=?"
                    );

            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {

                Integer orderId = rs.getInt("id");
                Integer tableId = rs.getInt("tableid");
                Integer waiterId = rs.getInt("waiterid");
                String notes = rs.getString("notes");
                double bill = rs.getDouble("bill");
                boolean isReservation = rs.getBoolean("isreservation");
                boolean isPaid = rs.getBoolean("ispaid");

                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);

                // TEMPORARY
                Workers waiter = workersDAO.getWorkerById(waiterId);

                TableOrder tableOrder =
                        new TableOrder(
                                orderId,
                                restaurantTable,
                                waiter,
                                notes,
                                bill,
                                isReservation
                        );

                tableOrder.setPaid(isPaid);

                return tableOrder;
            }

            throw new NoSuchElementException(
                    "TableOrder not found with id: " + id
            );
        }
    }

    @Override
    public void deleteTableOrderByID(Integer id)
            throws SQLException {

        try(Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "DELETE FROM tableorders WHERE id=?"
                    );

            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected == 0) {

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
                            "SELECT * FROM tableorders WHERE waiterid=?"
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
                boolean isReservation = rs.getBoolean("isreservation");
                boolean isPaid = rs.getBoolean("ispaid");

                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);
                // TEMPORARY
                Workers waiter = workersDAO.getWorkerById(waiterId);

                TableOrder tableOrder =
                        new TableOrder(
                                orderId,
                                restaurantTable,
                                waiter,
                                notes,
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
    public ArrayList<TableOrder> getTableOrdersByTableId(Integer tableId) throws SQLException {
        try (Connection connection = getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT * FROM tableorders WHERE tableid=?"
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
                boolean isReservation = rs.getBoolean("isreservation");
                boolean isPaid = rs.getBoolean("ispaid");

                RestaurantTable restaurantTable = restaurantTableDAO.getRestaurantTableByID(tableId);
                // TEMPORARY
                Workers waiter = workersDAO.getWorkerById(waiterId);

                TableOrder tableOrder =
                        new TableOrder(
                                orderId,
                                restaurantTable,
                                waiter,
                                notes,
                                bill,
                                isReservation
                        );

                tableOrder.setPaid(isPaid);

                finalList.add(tableOrder);
            }

            return finalList;
        }
    }
}