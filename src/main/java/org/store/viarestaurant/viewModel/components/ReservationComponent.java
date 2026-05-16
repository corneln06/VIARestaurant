package org.store.viarestaurant.viewModel.components;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.TableBookingRequest;
import org.store.viarestaurant.viewModel.services.ReservationService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class ReservationComponent
{
  private final ReservationScheduleView scheduleView =
      new ReservationScheduleView();

  private final ReservationTableComboBinder tableComboBinder =
      new ReservationTableComboBinder();

  private final DateTimeFormatter timeFormatter =
      DateTimeFormatter.ofPattern("HH:mm");

  private ReservationService reservationService;

  private ArrayList<RestaurantTable> tables =
      new ArrayList<>();

  private ArrayList<Reservation> reservations =
      new ArrayList<>();

  private GridPane scheduleGrid;
  private Pane scheduleOverlayPane;

  private StackPane reservationOverlay;
  private TextField guestNameField;
  private DatePicker reservationDatePicker;
  private TextField reservationTimeField;
  private TextField partySizeField;
  private ComboBox<String> tableComboBox;
  private Label errorLabel;

  public void initView(
      GridPane scheduleGrid,
      Pane scheduleOverlayPane)
  {
    this.scheduleGrid = scheduleGrid;
    this.scheduleOverlayPane = scheduleOverlayPane;
  }

  public void initModal(
      StackPane reservationOverlay,
      TextField guestNameField,
      DatePicker reservationDatePicker,
      TextField reservationTimeField,
      TextField partySizeField,
      ComboBox<String> tableComboBox,
      Label errorLabel)
  {
    this.reservationOverlay = reservationOverlay;
    this.guestNameField = guestNameField;
    this.reservationDatePicker = reservationDatePicker;
    this.reservationTimeField = reservationTimeField;
    this.partySizeField = partySizeField;
    this.tableComboBox = tableComboBox;
    this.errorLabel = errorLabel;
  }

  public void initClient(Client client)
  {
    this.reservationService =
        new ReservationService(client);

    reservationService.onTablesLoaded(response ->
    {
      tables = response.getTables();
      if(tableComboBox != null)
      {
        tableComboBinder.populate(tableComboBox, tables);
      }
      drawSchedule();
    });

    reservationService.onReservationLoaded(response ->
    {
      reservations = response.getReservations();
      drawSchedule();
    });

    reservationService.onCreateReservationResponse(response ->
    {
      if(response.isSuccess())
      {
        closeReservationModal();
        refreshSchedule();
      }
      else
      {
        showError(response.getMessage());
      }
    });

    reservationService.onReservationCreated(message ->
    {
      refreshSchedule();
    });
  }

  public void refreshSchedule()
  {
    if(reservationService == null)
    {
      return;
    }

    try
    {
      reservationService.loadSchedule();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public void openReservationModal()
  {
    if(reservationOverlay == null)
    {
      return;
    }

    guestNameField.clear();
    partySizeField.clear();
    reservationDatePicker.setValue(LocalDate.now());
    reservationTimeField.setText("19:00");

    hideError();
    if(tableComboBox != null)
    {
      tableComboBinder.populate(tableComboBox, tables);
      tableComboBox.getSelectionModel().clearSelection();
    }

    reservationOverlay.setVisible(true);
    reservationOverlay.setManaged(true);
  }

  public void closeReservationModal()
  {
    if(reservationOverlay == null)
    {
      return;
    }

    reservationOverlay.setVisible(false);
    reservationOverlay.setManaged(false);
  }

  public void createReservation()
  {
    hideError();

    String guestName =
        guestNameField.getText() == null
            ? ""
            : guestNameField.getText().trim();

    if(guestName.isBlank())
    {
      showError("Guest name is required.");
      return;
    }

    LocalDate date =
        reservationDatePicker.getValue();

    if(date == null)
    {
      showError("Select a reservation date.");
      return;
    }

    LocalTime time;

    try
    {
      time =
          LocalTime.parse(
              reservationTimeField.getText().trim(),
              timeFormatter
          );
    }
    catch(DateTimeParseException e)
    {
      showError("Time must use 24-hour HH:mm format.");
      return;
    }

    int partySize;

    try
    {
      partySize =
          Integer.parseInt(
              partySizeField.getText().trim()
          );
    }
    catch(NumberFormatException e)
    {
      showError("Party size must be a whole number.");
      return;
    }

    RestaurantTable table =
        tableComboBinder.getSelectedTable(tableComboBox);

    if(table == null)
    {
      showError("Select a table.");
      return;
    }

    try
    {
      TableBookingRequest request =
          new TableBookingRequest(
              guestName,
              LocalDateTime.of(date, time),
              partySize,
              table
          );

      reservationService.createReservation(request);
    }
    catch(IOException e)
    {
      showError(e.getMessage());
    }
  }

  private void drawSchedule()
  {
    scheduleView.draw(
        scheduleGrid,
        scheduleOverlayPane,
        tables,
        reservations
    );
  }

  private void showError(String message)
  {
    if(errorLabel == null)
    {
      return;
    }

    errorLabel.setText(message);
    errorLabel.setVisible(true);
    errorLabel.setManaged(true);
  }

  private void hideError()
  {
    if(errorLabel == null)
    {
      return;
    }

    errorLabel.setVisible(false);
    errorLabel.setManaged(false);
  }
}