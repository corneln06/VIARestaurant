package org.store.viarestaurant.server.dto.AllergyDTO;

import java.io.Serializable;

public class UpdateAllergyResponse implements Serializable
{
    private final boolean success;
    private final String message;

    public UpdateAllergyResponse(boolean success, String message)
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