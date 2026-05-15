package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class MenuItemTableOrderDAOImpl implements MenuItemTableOrderDAO {

    private static MenuItemTableOrderDAOImpl instance;

    private MenuItemTableOrderDAOImpl() throws SQLException
    {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    private static synchronized MenuItemTableOrderDAOImpl getInstance() throws SQLException
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

                // 1.
                findItemStmt.setString(1, itemName);
                ResultSet rs = findItemStmt.executeQuery();
                if (!rs.next()) continue;

                int menuItemId = rs.getInt("id");

                // 2. delete one occurrence
                deleteStmt.setInt(1, menuItemId);
                deleteStmt.setInt(2, tableOrderId);

                deleteStmt.executeUpdate();
            }
        }
    }

    @Override
    public void addMenuItemsInTableOrder(
            Integer tableOrderId,
            ArrayList<String> menuItems
    ) throws SQLException {

        try (Connection connection = getConnection()) {

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
    }
}
