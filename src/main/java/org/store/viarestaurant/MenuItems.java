package org.store.viarestaurant;

import java.util.ArrayList;

public class MenuItems
{
  private int id;
  private String name;
  private MenuTypes type;
  private boolean isVegetarian;
  private ArrayList<Integer> allergiesId;

  public MenuItems(int id, String name, MenuTypes type, boolean isVegetarian, ArrayList<Integer> allergiesId){
    this.id = id;
    this.name = name;
    this.type = type;
    this.isVegetarian = isVegetarian;
    this.allergiesId = allergiesId;
  }

  public int getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public ArrayList<Integer> getAllergiesId()
  {
    return allergiesId;
  }

  public MenuTypes getType()
  {
    return type;
  }

  public boolean isVegetarian()
  {
    return isVegetarian;
  }
}
