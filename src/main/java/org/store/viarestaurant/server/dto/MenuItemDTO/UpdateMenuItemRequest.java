package org.store.viarestaurant.server.dto.MenuItemDTO;

import org.store.viarestaurant.model.enums.MenuTypes;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateMenuItemRequest implements Serializable
{
  private final int id;
  private final String name;
  private final MenuTypes type;
  private final double price;
  private final boolean isVegetarian;
  private final ArrayList<String> allergies;

  public UpdateMenuItemRequest(int id, String name, MenuTypes type, double price,
      boolean isVegetarian, ArrayList<String> allergies)
  {
    this.id = id;
    this.name = name;
    this.type = type;
    this.price = price;
    this.isVegetarian = isVegetarian;
    this.allergies = allergies;
  }

  public int getId()             { return id; }
  public String getName()        { return name; }
  public MenuTypes getType()     { return type; }
  public double getPrice()       { return price; }
  public boolean isVegetarian()  { return isVegetarian; }
  public ArrayList<String> getAllergies() { return allergies; }
}