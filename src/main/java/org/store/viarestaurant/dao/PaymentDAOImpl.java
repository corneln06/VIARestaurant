package org.store.viarestaurant.dao;

import org.store.viarestaurant.config.DatabaseConnection;
import org.store.viarestaurant.model.entities.Payment;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.enums.PaymentMethod;

import java.sql.*;

public class PaymentDAOImpl implements PaymentDAO
{

  private static PaymentDAOImpl instance;
  private PaymentDAOImpl() throws SQLException{
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized PaymentDAOImpl getInstance() throws SQLException{

    if (instance == null) {
      instance = new PaymentDAOImpl();
    }
    return instance;
  }
  private Connection getConnection() throws SQLException{
    return DatabaseConnection.getConnection();
  }

  @Override public Payment createPayment(double amount, PaymentMethod method,
      TableOrder order) throws SQLException
  {

    if (amount <= 0){
      throw new SQLException("Amount has to be greater than 0!");
    }
    if (order == null)
    {
      throw new SQLException("Order cannot be null!");
    }
    try(Connection connection = getConnection()){
      PreparedStatement statement = connection.prepareStatement(
          "INSERT INTO payments (amount, method, orderId) VALUES (?, ?, ?) RETURNING id"
      );

      statement.setDouble(1, amount);
      statement.setString(2, method.name());
      statement.setInt(3, order.getId());

      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        int id = rs.getInt("id");
        return new Payment(id, amount, method, order);
      } else {
        throw new SQLException("No ID returned");
      }
    }
  }

  @Override public Payment getPaymentById(int id) throws SQLException
  {
    String sql = "SELECT id, amount, method, orderId FROM payments WHERE id = ?";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();

      if (rs.next())
      {
        int orderId = rs.getInt("orderId");
        TableOrderDAO tableOrderDAO = TableOrderDAOImpl.getInstance();
        TableOrder order = tableOrderDAO.getTableOrderByID(orderId);
        PaymentMethod method = PaymentMethod.valueOf(rs.getString("method"));
        return new Payment(
            rs.getInt("id"),
            rs.getDouble("amount"),
            method,
            order
        );
      }
      else
      {
        throw new SQLException("No payment found with id: " + id);
      }
    }
  }

  @Override public void deleteById(int id) throws SQLException
  {
    String sql = "DELETE FROM payments WHERE id = ?";

    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
    {
      statement.setInt(1, id);
      int affected = statement.executeUpdate();

      if (affected == 0)
      {
        throw new SQLException("Delete failed, no payment found with id: " + id);
      }
    }
  }
}
