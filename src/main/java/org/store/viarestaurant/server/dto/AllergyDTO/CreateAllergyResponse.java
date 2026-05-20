package org.store.viarestaurant.server.dto.AllergyDTO;

import org.store.viarestaurant.model.entities.Allergy;

import java.io.Serializable;

public class CreateAllergyResponse implements Serializable {
    private final Allergy allergy;

    public CreateAllergyResponse(Allergy allergy)
    {
        this.allergy = allergy;
    }

    public Allergy getAllergy()
    {
        return allergy;
    }
}
