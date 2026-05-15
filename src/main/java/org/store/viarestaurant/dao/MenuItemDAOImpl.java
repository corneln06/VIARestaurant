package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.Allergy;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.enums.MenuTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuItemDAOImpl implements MenuItemDAO {

    private static MenuItemDAOImpl instance;

    private MenuItemDAOImpl() throws SQLException {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized MenuItemDAOImpl getInstance() throws SQLException {
        if (instance == null) {
            instance = new MenuItemDAOImpl();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    private final AllergyDAO allergyDAO = new AllergyDAOImpl();

    @Override
    public MenuItems createMenuItem(String name, MenuTypes type, Double price, boolean isVegetarian, ArrayList<String> allergies) throws SQLException {

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO menuitems (name, type, price, isVegetarian) " + "VALUES (?, ?, ?, ?) RETURNING id");

            statement.setString(1, name);
            statement.setString(2, String.valueOf(type));
            statement.setDouble(3, price);
            statement.setBoolean(4, isVegetarian);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");

                if (allergies != null && !allergies.isEmpty()) {
                    try (PreparedStatement allergyStatement = connection.prepareStatement("INSERT INTO menuitemsallergies (menuItemId, allergyId) VALUES (?, ?)")) {

                        for (String allergyName : allergies) {
                            Allergy allergy = allergyDAO.getAllergyByName(allergyName);

                            if (allergy != null) {
                                allergyStatement.setInt(1, id);
                                allergyStatement.setInt(2, allergy.getId());
                                allergyStatement.executeUpdate();
                            }
                        }
                    }
                }

                return new MenuItems(id, name, type, price, isVegetarian, allergies);
            } else {
                return null;
            }
        }
    }

    @Override
    public List<MenuItems> getAllMenuItems() throws SQLException {

        try (Connection connection = getConnection()) {

            String sql = """
                    
                        SELECT\s
                        m.id,
                        m.name,
                        m.type,
                        m.price,
                        m.isvegetarian,
                        a.name as allergyname
                    FROM menuitems m
                    LEFT JOIN menuitemsallergies ma ON m.id = ma.menuitemid
                    LEFT JOIN allergies a ON ma.allergyid = a.id
                    ORDER BY m.id
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            List<MenuItems> result = new ArrayList<>();

            MenuItems currentItem = null;
            int currentId = -1;

            while (rs.next()) {

                int id = rs.getInt("id");

                if (currentItem == null || id != currentId) {
                    currentId = id;

                    String name = rs.getString("name");
                    MenuTypes type = MenuTypes.valueOf(rs.getString("type"));
                    double price = rs.getDouble("price");
                    boolean isVegetarian = rs.getBoolean("isVegetarian");

                    currentItem = new MenuItems(id, name, type, price, isVegetarian, new ArrayList<>());
                    result.add(currentItem);
                }

                String allergyName = rs.getString("allergyname");

                if (allergyName != null) {
                    currentItem.addAllergy(allergyName);
                }
            }

            return result;
        }
    }

    ;

    @Override
    public MenuItems getMenuItemById(Integer id) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
            SELECT 
                m.id,
                m.name,
                m.type,
                m.price,
                m.isvegetarian,
                a.name AS allergyname
            FROM menuitems m
            LEFT JOIN menuitemsallergies ma ON m.id = ma.menuitemid
            LEFT JOIN allergies a ON ma.allergyid = a.id
            WHERE m.id = ?
        """);

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            MenuItems menuItem = null;
            ArrayList<String> allergies = new ArrayList<>();

            while (resultSet.next()) {
                if (menuItem == null) {
                    String name = resultSet.getString("name");
                    MenuTypes type = MenuTypes.valueOf(resultSet.getString("type"));
                    Double price = resultSet.getDouble("price");
                    boolean isVegetarian = resultSet.getBoolean("isvegetarian");

                    menuItem = new MenuItems(id, name, type, price, isVegetarian, allergies);
                }

                String allergyName = resultSet.getString("allergyname");

                if (allergyName != null) {
                    allergies.add(allergyName);
                }
            }

            return menuItem;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM menuitems WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public MenuItems getMenuItemByName(String name) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
            SELECT 
                m.id,
                m.name,
                m.type,
                m.price,
                m.isvegetarian,
                a.name AS allergyname
            FROM menuitems m
            LEFT JOIN menuitemsallergies ma ON m.id = ma.menuitemid
            LEFT JOIN allergies a ON ma.allergyid = a.id
            WHERE m.name = ?
        """);

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            MenuItems menuItem = null;
            ArrayList<String> allergies = new ArrayList<>();

            while (resultSet.next()) {
                if (menuItem == null) {
                    int id = resultSet.getInt("id");
                    MenuTypes type = MenuTypes.valueOf(resultSet.getString("type"));
                    Double price = resultSet.getDouble("price");
                    boolean isVegetarian = resultSet.getBoolean("isvegetarian");

                    menuItem = new MenuItems(id, name, type, price, isVegetarian, allergies);
                }

                String allergyName = resultSet.getString("allergyname");

                if (allergyName != null) {
                    allergies.add(allergyName);
                }
            }

            return menuItem;
        }
    }

    @Override
    public MenuItems updateMenuItem(MenuItems item) throws SQLException {

        try (Connection connection = getConnection()) {

            connection.setAutoCommit(false);

            try {
                MenuItems existing = getMenuItemById(item.getId());

                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE menuitems SET " +
                                "name=?, " +
                                "type=?, " +
                                "price=?, " +
                                "isvegetarian=? " +
                                "WHERE id=?"
                );

                statement.setString(1, item.getName());
                statement.setObject(2, item.getType().name());
                statement.setDouble(3, item.getPrice());
                statement.setBoolean(4, item.isVegetarian());
                statement.setInt(5, item.getId());

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException(
                            "Menu item with id " + item.getId() + " not found"
                    );
                }
                /// check if the array actually changed
                if( !Objects.equals(item.getAllergies(), existing.getAllergies()))
                {  /// Delete old allergies
                    PreparedStatement deleteAllergies = connection.prepareStatement(
                                "DELETE FROM menuitemsallergies WHERE menuitemid=?"
                        );

                    deleteAllergies.setInt(1, item.getId());
                    deleteAllergies.executeUpdate();

                /// insert new allergies
                    if (item.getAllergies() != null && !item.getAllergies().isEmpty()) {

                    PreparedStatement insertAllergy =
                            connection.prepareStatement(
                                    "INSERT INTO menuitemsallergies (menuitemid, allergyid) VALUES (?, ?)"
                            );

                    for (String allergyName : item.getAllergies()) {

                        Allergy allergy =
                                allergyDAO.getAllergyByName(allergyName);

                        if (allergy != null) {

                            insertAllergy.setInt(1, item.getId());
                            insertAllergy.setInt(2, allergy.getId());

                            insertAllergy.addBatch();
                        }
                    }

                    insertAllergy.executeBatch();
                }
            }

                connection.commit();
                return item;

            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }
    }

