package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import java.io.Serializable;

public class DeleteTableRequest implements Serializable {
    private int restaurantTableid;

    public DeleteTableRequest(int tableId) {
        this.restaurantTableid = tableId;

    }

    public int getRestaurantTableid() {
        return restaurantTableid;
    }
}
