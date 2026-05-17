package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.Allergy;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;

import java.sql.*;
import java.util.ArrayList;

public class AllergyDAOImpl implements AllergyDAO{

    private static AllergyDAOImpl instance;
    AllergyDAOImpl() throws SQLException {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized AllergyDAOImpl getInstance() throws SQLException{
        if (instance == null){
            instance = new AllergyDAOImpl();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    @Override
    public Allergy createAllergy (String name) throws SQLException{

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO allergies (name) " +
                            "VALUES (?) RETURNING id"
            );

            statement.setString(1, name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                int id = rs.getInt("id");

                return new Allergy(id, name);
            } else {
                return null;
            }
        }
    };

  @Override
  public ArrayList<Allergy> getAllAllergies() throws SQLException
  {
    String sql = "SELECT id, name from allergies";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      ResultSet rs = statement.executeQuery();
      ArrayList<Allergy> allergies = new ArrayList<>();

      while (rs.next())
      {
        allergies.add(new Allergy(
            rs.getInt("id"),
            rs.getString("name")
        ));
      }

      return allergies;
    }
  }

    @Override
    public Allergy getAllergyById(Integer id) throws SQLException {
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM allergies WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                return new Allergy(id, name);
            } else {
                return null;
            }
        }
    };

    @Override
    public Allergy getAllergyByName(String name) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM allergies WHERE LOWER(name) = LOWER(?)"
            );
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String dbName = resultSet.getString("name");
                return new Allergy(id, dbName);
            } else {
                return null;
            }
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM allergies WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public Allergy updateAllergy(Allergy allergy) throws SQLException {
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE allergies set name=? where id=?"
            );
            statement.setString(1, allergy.getName());
            statement.setInt(2, allergy.getId());
            int rowsAffected = statement.executeUpdate();


            if (rowsAffected == 0) {
                throw new SQLException(
                        "Allergy with id " + allergy.getId() + " not found"
                );
            }

            return allergy;
        }
    }


}
