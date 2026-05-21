package org.store.viarestaurant.server.dto.TableOrderDTO;

import java.io.Serializable;
import java.util.ArrayList;

public class AddItemsToOrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int tableOrderId;
    private final ArrayList<String> itemNames;

    public AddItemsToOrderRequest(int tableOrderId, ArrayList<String> itemNames) {
        this.tableOrderId = tableOrderId;
        this.itemNames = itemNames;
    }

    public ArrayList<String> getItemNames() {
        return itemNames;
    }

    public int getTableOrderId() {
        return tableOrderId;
    }    
}
