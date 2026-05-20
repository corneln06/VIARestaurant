package org.store.viarestaurant.model.entities;

import java.io.Serializable;

public class Allergy implements Serializable
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

  public void setName(String name) {
    this.name = name;
  }
}
