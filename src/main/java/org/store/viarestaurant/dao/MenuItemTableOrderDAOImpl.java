package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.enums.MenuTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuItemTableOrderDAOImpl implements MenuItemTableOrderDAO {

    private static MenuItemTableOrderDAOImpl instance;

    private MenuItemTableOrderDAOImpl() throws SQLException
    {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized MenuItemTableOrderDAOImpl getInstance() throws SQLException
    {
        if (instance==null)
        {instance = new MenuItemTableOrderDAOImpl();}

        return instance;
    }
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public void deleteMenuItemsFromTableOrder(Integer tableOrderId, ArrayList<String> menuItems) throws SQLException {

        try (Connection connection = getConnection()) {

            PreparedStatement findItemStmt = connection.prepareStatement(
                    "SELECT id FROM MenuItems WHERE name = ?"
            );

            PreparedStatement deleteStmt = connection.prepareStatement(
                    "DELETE FROM MenuItemsTableOrder " +
                            "WHERE id = ( " +
                            "    SELECT id " +
                            "    FROM MenuItemsTableOrder " +
                            "    WHERE menuItemId = ? " +
                            "      AND tableOrderId = ? " +
                            "    ORDER BY id " +
                            "    LIMIT 1 " +
                            ")"
            );

            for (String itemName : menuItems) {

                findItemStmt.setString(1, itemName);
                ResultSet rs = findItemStmt.executeQuery();
                if (!rs.next()) continue;

                int menuItemId = rs.getInt("id");

                deleteStmt.setInt(1, menuItemId);
                deleteStmt.setInt(2, tableOrderId);

                deleteStmt.executeUpdate();
            }
        }
    }

    @Override
    public void addMenuItemsInTableOrder(Connection connection, Integer tableOrderId, ArrayList<String> menuItems) throws SQLException {

                PreparedStatement findItemStmt =
                        connection.prepareStatement(
                                "SELECT id FROM MenuItems WHERE name = ?"
                        );

                PreparedStatement insertStmt =
                        connection.prepareStatement(
                                "INSERT INTO MenuItemsTableOrder (menuItemId, tableOrderId) " +
                                        "VALUES (?, ?) " +
                                        "ON CONFLICT (menuItemId, tableOrderId) DO NOTHING"
                        );

                for (String itemName : menuItems) {

                    findItemStmt.setString(1, itemName);

                    ResultSet rs = findItemStmt.executeQuery();

                    if (!rs.next()) continue;

                    int menuItemId = rs.getInt("id");

                    insertStmt.setInt(1, menuItemId);
                    insertStmt.setInt(2, tableOrderId);

                    insertStmt.executeUpdate();
                }

    }

    @Override
    public ArrayList<MenuItems> getMenuItemsByTableOrderId(Integer tableOrderId) throws SQLException {

        try (Connection connection = getConnection()) {

            PreparedStatement statement = connection.prepareStatement(
                    "SELECT " +
                            "mi.id AS menu_id, " +
                            "mi.name, " +
                            "mi.type, " +
                            "mi.price, " +
                            "mi.isvegetarian, " +
                            "a.name AS allergy_name " +
                            "FROM MenuItemsTableOrder mt " +
                            "JOIN MenuItems mi ON mi.id = mt.menuItemId " +
                            "LEFT JOIN MenuItemsAllergies ma ON mi.id = ma.menuItemId " +
                            "LEFT JOIN Allergies a ON a.id = ma.allergyId " +
                            "WHERE mt.tableOrderId = ?;"
            );

            statement.setInt(1, tableOrderId);

            ResultSet rs = statement.executeQuery();

            Map<Integer, MenuItems> map = new HashMap<>();

            while (rs.next()) {

                int id = rs.getInt("menu_id");

                MenuItems item = map.get(id);

                if (item == null) {

                    item = new MenuItems(
                            id,
                            rs.getString("name"),
                            MenuTypes.valueOf(rs.getString("type")),
                            rs.getDouble("price"),
                            rs.getBoolean("isvegetarian"),
                            new ArrayList<>()
                    );

                    map.put(id, item);
                }

                String allergyName = rs.getString("allergy_name");

                if (allergyName != null && item.getAllergies() != null) {
                    item.getAllergies().add(allergyName);
                }
            }

            return new ArrayList<>(map.values());
        }
    }
}
