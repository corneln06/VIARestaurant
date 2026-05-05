package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.*;
import org.store.viarestaurant.model.state.TableState;
import org.store.viarestaurant.model.state.TableStateFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class RestaurantTableDAOImpl implements RestaurantTableDAO {

    private static RestaurantTableDAOImpl instance;
    private RestaurantTableDAOImpl() throws SQLException{
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized RestaurantTableDAOImpl getInstance() throws SQLException{

            if (instance != null) {
                RestaurantTableDAOImpl restaurantTableDAO = (RestaurantTableDAOImpl) instance;
            }
        return instance;
    }
    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    @Override
    public RestaurantTable createRestaurantTable(int maxSitting, TableState status) throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO restauranttable (status, maxSitting)" +
                            "VALUES (?,?) RETURNING id"
            );

            statement.setString(1,status.getName());
            statement.setInt(2,maxSitting);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                return new RestaurantTable(id,maxSitting,status);
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
            RestaurantTable restaurantTable = null;
            while(rs.next())
            {
                Integer id = rs.getInt("id");
                if (restaurantTable == null || id != restaurantTable.getId())
                {
                    int maxSitting = rs.getInt("maxSitting");
                    TableState status = TableStateFactory.fromString(rs.getString("status"));

                    restaurantTable = new RestaurantTable(id,maxSitting, status);
                    finalList.add(restaurantTable);
                }
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
                TableState status = TableStateFactory.fromString(rs.getString("status"));

                return new RestaurantTable(tableID, maxSitting, status);
            }
            throw new NoSuchElementException("RestaurantTable not found with id: " + id);
        }
    }

        @Override
        public void deleteRestaurantTableByID (Integer id) throws SQLException {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE * FROM restauranttable WHERE id=?"
                );

                statement.setInt(1, id);

                int rowsAffected = statement.executeUpdate();

                throw new IllegalArgumentException("No Table with id: " + id);
            }
        }
    }
