package org.store.viarestaurant.model.entities;

import org.store.viarestaurant.model.enums.PaymentMethod;
import org.store.viarestaurant.model.enums.WorkerRole;

public class Payment
{
  private int id;
  private double amount;
  private PaymentMethod method;
  private TableOrder orderId;

  public Payment(int id, double amount, PaymentMethod method, TableOrder orderId){
    this.id = id;
    this.amount = amount;
    this.method = method;
    this.orderId = orderId;
  }

  public int getId()
  {
    return id;
  }

  public double getAmount()
  {
    return amount;
  }

  public TableOrder getOrderId()
  {
    return orderId;
  }

  public PaymentMethod getMethod()
  {
    return method;
  }
}