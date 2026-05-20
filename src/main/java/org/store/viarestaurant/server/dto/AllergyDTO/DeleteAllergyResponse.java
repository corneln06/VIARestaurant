package org.store.viarestaurant.server.dto.AllergyDTO;

import java.io.Serializable;

public class DeleteAllergyResponse implements Serializable
{
    private final boolean success;
    private final String message;

    public DeleteAllergyResponse(boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getMessage()
    {
        return message;
    }
}