package org.store.viarestaurant.server.dto.AllergyDTO;

import java.io.Serializable;

public class CreateAllergyRequest implements Serializable
{
    private final String name;

    public CreateAllergyRequest(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
