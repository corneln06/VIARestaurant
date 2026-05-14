package org.store.viarestaurant.model.entities;

import org.store.viarestaurant.model.enums.MenuTypes;

import java.util.ArrayList;

public class MenuItems
{
  private int id;
  private String name;
  private MenuTypes type;
  private Double price;
  private boolean isVegetarian;
  private ArrayList<String> allergies;

  public MenuItems(int id, String name, MenuTypes type, Double price, boolean isVegetarian, ArrayList<String> allergies){
    this.id = id;
    this.name = name;
    this.type = type;
    this.price = price;
    this.isVegetarian = isVegetarian;
    this.allergies = allergies;
  }

  public int getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public ArrayList<String> getAllergies()
  {
    return allergies;
  }

  public MenuTypes getType()
  {
    return type;
  }

  public Double getPrice(){
      return price;
  }

  public void addAllergy(String allergyName){
      allergies.add(allergyName);
  }

  public boolean isVegetarian()
  {
    return isVegetarian;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setType(MenuTypes type) {
    this.type = type;
  }

  public void setVegetarian(boolean vegetarian) {
    isVegetarian = vegetarian;
  }

  public void removeAllergy(String allergy) {
    if (allergy != null )
      allergies.remove(allergy);
  }

  @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", isVegetarian=" + isVegetarian +
                ", allergies=" + allergies +
                '}';
    }
}
