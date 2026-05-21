package org.store.viarestaurant.server.dto.ReservationDto;

import java.io.Serializable;

public class GetClosestReservationRequest implements Serializable {

    private int tableId;

    public GetClosestReservationRequest(int tableId){
        this.tableId = tableId;
    }

    public int getTableId() {
        return tableId;
    }
}
