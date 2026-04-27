package org.store.viarestaurant.model.entities;

public class Allergy
{
  private int id;
  private String name;

  public Allergy(int id, String name){
    this.id = id;
    this.name = name;
  }

  public int getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }
}
