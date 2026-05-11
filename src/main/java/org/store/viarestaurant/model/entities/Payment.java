package org.store.viarestaurant.model.entities;

public class Payment
{
  private int id;
  private double amount;
  private String method;
  private TableOrder orderId;

  public Payment(int id, double amount, String method, TableOrder orderId){
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

  public String getMethod()
  {
    return method;
  }
}