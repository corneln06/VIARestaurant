package org.store.viarestaurant.dao;

import org.store.viarestaurant.model.entities.Payment;

import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.enums.PaymentMethod;

import java.sql.SQLException;


public interface PaymentDAO
{

  Payment createPayment(double amount, PaymentMethod method, TableOrder orderId) throws
      SQLException;
  Payment getPaymentById(int id) throws SQLException;
  void deleteById(int id) throws SQLException;
}
