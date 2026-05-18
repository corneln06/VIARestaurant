package org.store.viarestaurant.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.state.TableState;

public class RestaurantTableDAOImpl implements RestaurantTableDAO {

    private static RestaurantTableDAOImpl instance;
    private RestaurantTableDAOImpl() throws SQLException{
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized RestaurantTableDAOImpl getInstance() throws SQLException{

            if (instance == null) {
                instance = new RestaurantTableDAOImpl();
            }
        return instance;
    }
    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    @Override
    public RestaurantTable createRestaurantTable(int maxSitting) throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO restauranttable (status, maxSitting) VALUES (?, ?) RETURNING id"
            );

            statement.setString(1, "Available");
            statement.setInt(2, maxSitting);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                return new RestaurantTable(id, maxSitting);
            } else {
                throw new SQLException("No ID returned");
            }
        }
    }

    @Override
    public ArrayList<RestaurantTable> getAllRestaurantTables() throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM restauranttable"
            );

            ResultSet rs = statement.executeQuery();
            ArrayList<RestaurantTable> finalList= new ArrayList<>();
            while(rs.next()) {

                Integer id = rs.getInt("id");
                int maxSitting = rs.getInt("maxSitting");

                RestaurantTable restaurantTable =
                        new RestaurantTable(id, maxSitting);

                finalList.add(restaurantTable);
            }
            return finalList;
        }
    }

    @Override
    public RestaurantTable getRestaurantTableByID(Integer id) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM restauranttable WHERE id=?"
            );

            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int tableID = rs.getInt("id");
                int maxSitting = rs.getInt("maxSitting");

                return new RestaurantTable(tableID, maxSitting);
            }
            throw new NoSuchElementException("RestaurantTable not found with id: " + id);
        }
    }

        @Override
        public void deleteRestaurantTableByID (Integer id) throws SQLException {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM restauranttable WHERE id=?"
                );

                statement.setInt(1, id);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Table with id " + id + " not found");
                }
            }
        }

    @Override
    public RestaurantTable updateRestaurantTable(RestaurantTable restaurantTable) throws SQLException {
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE restauranttable set maxsitting=? where id=?"
            );

            statement.setInt(1, restaurantTable.getMaxSitting());
            statement.setInt(2, restaurantTable.getId());
            int rowsAffected = statement.executeUpdate();


            if (rowsAffected == 0) {
                throw new SQLException(
                        "Table with id " + restaurantTable.getId() + " not found"
                );
            }

            return restaurantTable;
        }
    }

    //new method forupdating thr table status since the existing method only updates max sitting

    public void updateTableState(int tableId, TableState state) throws SQLException {
        String sql = "UPDATE restauranttable SET status = ? WHERE id = ?";
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1,state.getName());
            statement.setInt(2,tableId);
            statement.executeUpdate();
        }
    }
}
