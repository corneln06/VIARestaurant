package org.store.viarestaurant.server.dto.AllergyDTO;

import java.io.Serializable;

public class DeleteAllergyRequest implements Serializable
{
    private final int id;

    public DeleteAllergyRequest(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
}