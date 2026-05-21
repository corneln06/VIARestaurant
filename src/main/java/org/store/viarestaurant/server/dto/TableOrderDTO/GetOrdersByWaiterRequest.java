package org.store.viarestaurant.server.dto.TableOrderDTO;

import java.io.Serializable;

public class GetOrdersByWaiterRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int waiterId;
    public GetOrdersByWaiterRequest(int waiterId){
        this.waiterId = waiterId;
    }
    public int getWaiterId(){
        return waiterId;
    }
}
