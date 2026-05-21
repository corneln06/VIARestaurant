package org.store.viarestaurant.server.dto.TableOrderDTO;

import java.io.Serializable;

public class PayOrderResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;

    public PayOrderResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
