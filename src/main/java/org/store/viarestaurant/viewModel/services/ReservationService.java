package org.store.viarestaurant.viewModel.services;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.CreateReservationResponse;
import org.store.viarestaurant.server.dto.ReservationDto.GetReservationsResponse;
import org.store.viarestaurant.server.dto.ReservationDto.GetReservationsRequest;
import org.store.viarestaurant.server.dto.ReservationDto.GetTablesRequest;
import org.store.viarestaurant.server.dto.ReservationDto.GetTablesResponse;
import org.store.viarestaurant.server.dto.ReservationDto.ReservationCreatedMessage;
import org.store.viarestaurant.server.dto.ReservationDto.TableBookingRequest;

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
    client.send(new GetTablesRequest());
    client.send(new GetReservationsRequest());
  }

  public void createReservation(
      TableBookingRequest request)
      throws IOException
  {
    client.send(request);
  }

  public void onTablesLoaded(
      Consumer<GetTablesResponse> listener)
  {
    client.setTablesListener(listener);
  }

  public void onReservationLoaded(
      Consumer<GetReservationsResponse> listener)
  {
    client.setReservationsListener(listener);
  }

  public void onCreateReservationResponse(
      Consumer<CreateReservationResponse> listener)
  {
    client.setCreateReservationListener(listener);
  }

  public void onReservationCreated(
      Consumer<ReservationCreatedMessage> listener)
  {
    client.setReservationCreatedListener(listener);
  }
}