package org.store.viarestaurant.server.dto.AllergyDTO;

import java.io.Serializable;

public class UpdateAllergyRequest implements Serializable
{
    private final int id;
    private final String name;

    public UpdateAllergyRequest(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}