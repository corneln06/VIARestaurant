package org.store.viarestaurant.model.entities;

public class OrderItem {

    private final int id;
    private MenuItems menuItem;
    private int quantity;

    public OrderItem(
            int id,
            MenuItems menuItem,
            int quantity
    ) {
        this.id = id;
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public MenuItems getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItems menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {

        if(quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }

        this.quantity = quantity;
    }

    public void incrementQuantity() {
        quantity++;
    }

    public void decrementQuantity() {

        if(quantity > 1) {
            quantity--;
        }
    }
}