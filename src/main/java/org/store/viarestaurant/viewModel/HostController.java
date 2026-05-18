package org.store.viarestaurant.viewModel;


import java.sql.SQLException;

import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.ReservationComponent;
import org.store.viarestaurant.viewModel.components.TableComponent;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class HostController
{
  private final TableComponent tableComponent = new TableComponent();



  private final ReservationComponent reservationComponent =
      new ReservationComponent();

  public void init(
      GridPane scheduleGrid,
      Pane scheduleOverlayPane, GridPane tableGrid) throws SQLException
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
      Label info,
    ComboBox<String> waiterComboBox)
  {
    tableComponent.initModal(overlay, title, badge, info, waiterComboBox);
  }

  public void initClient(Client client)
  {
    reservationComponent.initClient(client);
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

  //Table Modal function
  public void seatTable(){
    tableComponent.seatTable();
  }

}