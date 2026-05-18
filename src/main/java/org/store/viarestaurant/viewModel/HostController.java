package org.store.viarestaurant.viewModel;


import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.*;
import org.store.viarestaurant.viewModel.components.ReservationComponent;

import javafx.scene.control.*;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.ReservationComponent;
import org.store.viarestaurant.viewModel.components.TableComponent;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class HostController
{
  private final TableComponent tableComponent = new TableComponent();



  private final ReservationComponent reservationComponent =
      new ReservationComponent();

  public void init(
      GridPane scheduleGrid,
      Pane scheduleOverlayPane, GridPane tableGrid)
  {

    tableComponent.initGrid(tableGrid);

    reservationComponent.initView(
        scheduleGrid,
        scheduleOverlayPane
    );
  }

  public void initReservationModal(
      StackPane overlay,
      TextField guestName,
      DatePicker datePicker,
      TextField timeField,
      TextField partySize,
      ComboBox<String> tableCombo,
      Label errorLabel,
      Button submitButton,
      Button deleteButton,
      Label modalTitle)
  {
    reservationComponent.initModal(
        overlay,
        guestName,
        datePicker,
        timeField,
        partySize,
        tableCombo,
        errorLabel,
        submitButton,
            deleteButton,
            modalTitle
    );
  }

  public void initTableModal(
      StackPane overlay,
      Label title,
      Label badge,
      Label info)
  {
    tableComponent.initModal(overlay, title, badge, info);
  }

  public void initClient(Client client)
  {
    reservationComponent.initClient(client);
    tableComponent.initClient(client);
  }

  //InitClient for Tables missing TODO

  public void refreshSchedule()
  {
    reservationComponent.refreshSchedule();
  }

  public void openNewReservationModal()
  {
    reservationComponent.openReservationModal();
  }

  public void closeNewReservationModal()
  {
    reservationComponent.closeReservationModal();
  }

  public void createReservation()
  {
    reservationComponent.createReservation();
  }


  public void deleteReservation()
  {
    reservationComponent.deleteReservation();
  }

  //table component functionns

  public void refreshTableGrid(){
    tableComponent.refreshTableGrid();
  }

  public void closeTableModal(){
    tableComponent.closeTableModal();
  }

}