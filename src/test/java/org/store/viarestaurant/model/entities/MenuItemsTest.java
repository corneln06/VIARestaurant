package org.store.viarestaurant.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.store.viarestaurant.model.enums.MenuTypes;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MenuItemsTest {

    private MenuItems item;

    @BeforeEach
    public void setUp() {
        ArrayList<String> allergies = new ArrayList<>();
        allergies.add("Gluten");
        item = new MenuItems(1, "Burger", MenuTypes.Main, 9.99, false, allergies);
    }

    @Test // I
    public void getId_afterConstruction_returnsCorrectId() {
        assertEquals(1, item.getId());
    }

    @Test // I
    public void getName_afterConstruction_returnsCorrectName() {
        assertEquals("Burger", item.getName());
    }

    @Test // I
    public void getType_afterConstruction_returnsCorrectType() {
        assertEquals(MenuTypes.Main, item.getType());
    }

    @Test // I
    public void getPrice_afterConstruction_returnsCorrectPrice() {
        assertEquals(9.99, item.getPrice(), 0.001);
    }

    @Test // I
    public void isVegetarian_setFalseInConstructor_returnsFalse() {
        assertFalse(item.isVegetarian());
    }

    @Test // I
    public void isVegetarian_setTrueInConstructor_returnsTrue() {
        MenuItems vegItem = new MenuItems(2, "Salad", MenuTypes.Starter, 5.50, true, new ArrayList<>());
        assertTrue(vegItem.isVegetarian());
    }

    @Test // Z
    public void getAllergies_emptyListAtConstruction_returnsEmptyList() {
        MenuItems noAllergy = new MenuItems(3, "Water", MenuTypes.Beverage, 1.0, true, new ArrayList<>());
        assertTrue(noAllergy.getAllergies().isEmpty());
    }

    @Test // O
    public void getAllergies_oneAllergyAtConstruction_returnsListSizeOne() {
        assertEquals(1, item.getAllergies().size());
        assertEquals("Gluten", item.getAllergies().get(0));
    }

    @Test // M
    public void getAllergies_manyAllergiesAtConstruction_allPresent() {
        ArrayList<String> many = new ArrayList<>();
        many.add("Gluten");
        many.add("Dairy");
        many.add("Soy");
        MenuItems m = new MenuItems(4, "Cake", MenuTypes.Dessert, 4.50, false, many);
        assertEquals(3, m.getAllergies().size());
        assertTrue(m.getAllergies().contains("Dairy"));
    }

    @Test // Z
    public void addAllergy_toEmptyList_listSizeBecomesOne() {
        MenuItems m = new MenuItems(5, "Fries", MenuTypes.Starter, 2.5, true, new ArrayList<>());
        m.addAllergy("Soy");
        assertEquals(1, m.getAllergies().size());
    }

    @Test // O
    public void addAllergy_oneAllergy_allergyIsPresentInList() {
        MenuItems m = new MenuItems(6, "Toast", MenuTypes.Starter, 1.5, true, new ArrayList<>());
        m.addAllergy("Wheat");
        assertTrue(m.getAllergies().contains("Wheat"));
    }

    @Test // M
    public void addAllergy_manyAllergies_allStoredInList() {
        MenuItems m = new MenuItems(7, "Pasta", MenuTypes.Main, 11.0, false, new ArrayList<>());
        m.addAllergy("Gluten");
        m.addAllergy("Dairy");
        m.addAllergy("Eggs");
        assertEquals(3, m.getAllergies().size());
    }

    @Test // S
    public void addAllergy_sameAllergyTwice_listContainsTwoCopies() {
        MenuItems m = new MenuItems(8, "Bread", MenuTypes.Starter, 2.0, true, new ArrayList<>());
        m.addAllergy("Gluten");
        m.addAllergy("Gluten");
        assertEquals(2, m.getAllergies().size());
    }

    @Test // I
    public void removeAllergy_existingAllergy_allergyRemovedFromList() {
        item.removeAllergy("Gluten");
        assertFalse(item.getAllergies().contains("Gluten"));
    }

    @Test // S
    public void removeAllergy_existingAllergy_listSizeDecreasesByOne() {
        int sizeBefore = item.getAllergies().size();
        item.removeAllergy("Gluten");
        assertEquals(sizeBefore - 1, item.getAllergies().size());
    }

    @Test // Z
    public void removeAllergy_nonExistingAllergy_listUnchanged() {
        int sizeBefore = item.getAllergies().size();
        item.removeAllergy("Shellfish");
        assertEquals(sizeBefore, item.getAllergies().size());
    }

    @Test // E
    public void removeAllergy_null_doesNotThrow() {
        assertDoesNotThrow(() -> item.removeAllergy(null));
    }

    @Test // Z
    public void removeAllergy_null_listUnchanged() {
        int sizeBefore = item.getAllergies().size();
        item.removeAllergy(null);
        assertEquals(sizeBefore, item.getAllergies().size());
    }

    @Test // S
    public void setName_validName_nameUpdated() {
        item.setName("Veggie Burger");
        assertEquals("Veggie Burger", item.getName());
    }

    @Test // S
    public void setPrice_newPositivePrice_priceUpdated() {
        item.setPrice(19.99);
        assertEquals(19.99, item.getPrice(), 0.001);
    }

    @Test // B
    public void setPrice_zero_priceIsZero() {
        item.setPrice(0.0);
        assertEquals(0.0, item.getPrice(), 0.001);
    }

    @Test // S
    public void setType_beverage_typeUpdated() {
        item.setType(MenuTypes.Beverage);
        assertEquals(MenuTypes.Beverage, item.getType());
    }

    @Test // S
    public void setVegetarian_true_isVegetarianReturnsTrue() {
        item.setVegetarian(true);
        assertTrue(item.isVegetarian());
    }

    @Test // I
    public void toString_returnsNonNull() {
        assertNotNull(item.toString());
    }

    @Test // I
    public void toString_containsIdAndName() {
        String result = item.toString();
        assertTrue(result.contains("1") && result.contains("Burger"));
    }
}
