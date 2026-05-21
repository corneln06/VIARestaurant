package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import java.io.Serializable;

public class UpdateTableRequest implements Serializable {
    private Integer id;
    private int maxSitting;


    public UpdateTableRequest(Integer id, int maxSitting){
        this.id = id;
        this.maxSitting = maxSitting;
    }

    public Integer getId()
    {
        return id;
    }

    public int getMaxSitting()
    {
        return maxSitting;
    }



}
