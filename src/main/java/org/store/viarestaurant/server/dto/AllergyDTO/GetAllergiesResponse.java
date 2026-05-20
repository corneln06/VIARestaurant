package org.store.viarestaurant.server.dto.AllergyDTO;

import org.store.viarestaurant.model.entities.Allergy;

import java.io.Serializable;
import java.util.List;

public class GetAllergiesResponse implements Serializable
{
    private final List<Allergy> allergies;

    public GetAllergiesResponse(List<Allergy> allergies)
    {
        this.allergies = allergies;
    }

    public List<Allergy> getAllergies()
    {
        return allergies;
    }
}