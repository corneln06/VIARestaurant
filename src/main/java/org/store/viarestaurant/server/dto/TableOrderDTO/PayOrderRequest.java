package org.store.viarestaurant.server.dto.TableOrderDTO;

import java.io.Serializable;

public class PayOrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int tableOrderId;

    public PayOrderRequest(int tableOrderId) {
        this.tableOrderId = tableOrderId;
    }

    public int getTableOrderId() {
        return tableOrderId;
    }
}
