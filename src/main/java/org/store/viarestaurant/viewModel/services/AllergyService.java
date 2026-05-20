package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.AllergyDTO.*;
import org.store.viarestaurant.server.dto.MessageResponse;

import java.io.IOException;
import java.util.function.Consumer;

public class AllergyService
{
    private final Client client;

    public AllergyService(Client client)
    {
        this.client = client;
    }

    public void createAllergy(String name) throws IOException
    {
        client.send(new CreateAllergyRequest(name));
    }

    public void onAllergyCreated(Consumer<CreateAllergyResponse> listener)
    {
        client.setAllergyCreatedListener(listener);
    }

    public void loadAllergies() throws IOException
    {
        client.send(new GetAllergiesRequest());
    }

    public void onAllergiesLoaded(Consumer<GetAllergiesResponse> listener)
    {
        client.setGetAllergiesListener(listener);
    }

    public void updateAllergy(int id, String name) throws IOException
    {
        client.send(new UpdateAllergyRequest(id, name));
    }

    public void onAllergyUpdated(Consumer<MessageResponse> listener)
    {
        client.setUpdateAllergyListener(listener);
    }

    public void deleteAllergy(int id) throws IOException
    {
        client.send(new DeleteAllergyRequest(id));
    }

    public void onAllergyDeleted(Consumer<MessageResponse> listener)
    {
        client.setDeleteAllergyListener(listener);
    }
}