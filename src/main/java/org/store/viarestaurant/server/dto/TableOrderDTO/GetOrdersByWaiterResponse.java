package org.store.viarestaurant.server.dto.TableOrderDTO;

import java.io.Serializable;
import java.util.ArrayList;

import org.store.viarestaurant.model.entities.TableOrder;

public class GetOrdersByWaiterResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<TableOrder> orders;
    public GetOrdersByWaiterResponse(ArrayList<TableOrder> orders){
        this.orders = orders;
    }

    public ArrayList<TableOrder> getOrders() {
        return orders;
    }
    
}
