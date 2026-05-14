package org.store.viarestaurant.model.entities;

import org.store.viarestaurant.model.enums.PaymentMethod;
import org.store.viarestaurant.model.enums.WorkerRole;

public class Payment
{
  private int id;
  private double amount;
  private PaymentMethod method;
  private TableOrder order;

  public Payment(int id, double amount, PaymentMethod method, TableOrder order){
    this.id = id;
    this.amount = amount;
    this.method = method;
    this.order = order;
  }

  public int getId()
  {
    return id;
  }

  public double getAmount()
  {
    return amount;
  }

  public TableOrder getOrderLinked()
  {
    return order;
  }

  public PaymentMethod getMethod()
  {
    return method;
  }
}