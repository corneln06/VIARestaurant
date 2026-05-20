package org.store.viarestaurant.server.dto.RestaurantTableDTO;

import java.io.Serializable;

public class DeleteTableResponse implements Serializable
{
    private boolean success;
    private String message;

    public DeleteTableResponse(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public boolean isSuccess()
    {
        return success;
    }
}