package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.MessageResponse;
import org.store.viarestaurant.server.dto.ReservationDto.*;
import org.store.viarestaurant.server.dto.RestaurantTableDTO.TableUpdatedMessage;

import java.io.IOException;
import java.util.function.Consumer;

public class ReservationService
{
  private final Client client;

  public ReservationService(Client client)
  {
    this.client = client;
  }

  public void loadSchedule() throws IOException
  {
    client.send(new GetReservationsRequest());
  }

  public void createReservation(
      TableBookingRequest request)
      throws IOException
  {
    client.send(request);
  }

  public void updateReservation(
          UpdateReservationRequest request
  ) throws IOException
  {
    client.send(request);
  }

  public void deleteReservation(
          DeleteReservationRequest request
  ) throws IOException
  {
    client.send(request);
  }


  public void onReservationLoaded(
      Consumer<GetReservationsResponse> listener)
  {
    client.setReservationsListener(listener);
  }

  public void onCreateReservationResponse(
      Consumer<MessageResponse> listener)
  {
    client.setCreateReservationListener(listener);
  }

  public void onReservationCreated(
      Consumer<ReservationCreatedMessage> listener)
  {
    client.setReservationCreatedListener(listener);
  }

  public void onReservationDelete(
          Consumer<DeleteReservationResponse> listener
  )
  {
    client.setDeleteReservationListener(listener);
  }

  public void onReservationUpdate(
          Consumer<UpdateReservationResponse> listener
  )
  {
    client.setUpdateReservationListener(listener);
  }
  public void onUpdatedMessage(
      Consumer<TableUpdatedMessage> listener
  ){
    client.setUpdatedMessageListener(listener);
  }
}