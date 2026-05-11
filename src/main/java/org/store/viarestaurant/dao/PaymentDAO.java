package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.Payment;

import org.store.viarestaurant.model.entities.TableOrder;

import java.sql.SQLException;


public interface PaymentDAO
{

  Payment createPayment(double amount, String method, TableOrder orderId) throws
      SQLException;
  Payment getPaymentById(int id) throws SQLException;
  void deleteById(int id) throws SQLException;
}
