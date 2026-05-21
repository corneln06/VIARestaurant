package org.store.viarestaurant.server.dto.TableOrderDTO;

import java.io.Serializable;

public class AddItemsToOrderResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean success;
    private final String message;
    private final double newBill;
    public AddItemsToOrderResponse(boolean success, String message, double newBill) {
        this.success = success;
        this.message = message;
        this.newBill = newBill;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public double getNewBill() {
        return newBill;
    }

    
}
