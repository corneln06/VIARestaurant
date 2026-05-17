package org.store.viarestaurant.viewModel.components;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.DeleteReservationRequest;
import org.store.viarestaurant.server.dto.ReservationDto.TableBookingRequest;
import org.store.viarestaurant.server.dto.ReservationDto.UpdateReservationRequest;
import org.store.viarestaurant.viewModel.services.ReservationService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ReservationComponent {
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

  private boolean isEditMode = false;

  private Reservation editingReservation;

  private GridPane scheduleGrid;
  private Pane scheduleOverlayPane;

  private StackPane reservationOverlay;
  private TextField guestNameField;
  private DatePicker reservationDatePicker;
  private TextField reservationTimeField;
  private TextField partySizeField;
  private ComboBox<String> tableComboBox;
  private Label errorLabel;
  private Button submitReservationButton;
  private Button deleteReservationButton;
  private Label modalTitle;

  public void initView(
          GridPane scheduleGrid,
          Pane scheduleOverlayPane) {
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
          Label errorLabel,
          Button submit,
          Button delete,
          Label title) {
    this.reservationOverlay = reservationOverlay;
    this.guestNameField = guestNameField;
    this.reservationDatePicker = reservationDatePicker;
    this.reservationTimeField = reservationTimeField;
    this.partySizeField = partySizeField;
    this.tableComboBox = tableComboBox;
    this.errorLabel = errorLabel;
    this.submitReservationButton = submit;
    this.deleteReservationButton = delete;
    this.modalTitle = title;
  }

  public void initClient(Client client) {
    this.reservationService =
            new ReservationService(client);

    reservationService.onTablesLoaded(response ->
    {
      tables = response.getTables();
      if (tableComboBox != null) {
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
      if (response.isSuccess()) {
        closeReservationModal();
        refreshSchedule();
      } else {
        showError(response.getMessage());
      }
    });

    reservationService.onReservationCreated(message ->
    {
      refreshSchedule();
    });

    reservationService.onReservationUpdate(response -> {
      if (response.isSuccess()) {
        closeReservationModal();
        refreshSchedule();
      } else {
        showError(response.getMessage());
      }
    });

    reservationService.onReservationDelete(response -> {
      if (response.isSuccess()) {
        closeReservationModal();
        refreshSchedule();
      } else {
        showError(response.getMessage());
      }
    });
    scheduleView.setOnReservationClicked(
            this::openReservationAsForm
    );
  }

  public void refreshSchedule() {
    if (reservationService == null) {
      return;
    }

    try {
      reservationService.loadSchedule();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void openReservationModal() {
    if (reservationOverlay == null) {
      return;
    }
    setEditMode(false);
    modalTitle.setText("New Reservation");

    guestNameField.clear();
    partySizeField.clear();
    reservationDatePicker.setValue(LocalDate.now());
    reservationTimeField.setText("19:00");

    hideError();
    if (tableComboBox != null) {
      tableComboBinder.populate(tableComboBox, tables);
      tableComboBox.getSelectionModel().clearSelection();
    }

    reservationOverlay.setVisible(true);
    reservationOverlay.setManaged(true);
  }

  public void closeReservationModal() {
    if (reservationOverlay == null) {
      return;
    }

    reservationOverlay.setVisible(false);
    reservationOverlay.setManaged(false);
  }

  public void createReservation() {
    hideError();

    String guestName =
            guestNameField.getText() == null
                    ? ""
                    : guestNameField.getText().trim();

    if (guestName.isBlank()) {
      showError("Guest name is required.");
      return;
    }

    LocalDate date =
            reservationDatePicker.getValue();

    if (date == null) {
      showError("Select a reservation date.");
      return;
    }

    LocalTime time;

    try {
      time =
              LocalTime.parse(
                      reservationTimeField.getText().trim(),
                      timeFormatter
              );
    } catch (DateTimeParseException e) {
      showError("Time must use 24-hour HH:mm format.");
      return;
    }

    int partySize;

    try {
      partySize =
              Integer.parseInt(
                      partySizeField.getText().trim()
              );
    } catch (NumberFormatException e) {
      showError("Party size must be a whole number.");
      return;
    }

    RestaurantTable table =
            tableComboBinder.getSelectedTable(tableComboBox);

    if (table == null) {
      showError("Select a table.");
      return;
    }

    if (isEditMode) {

      try {
        UpdateReservationRequest request =
                new UpdateReservationRequest(
                        editingReservation.getId(),
                        guestName,
                        LocalDateTime.of(date, time),
                        partySize,
                        table
                );

        reservationService.updateReservation(request);


      } catch (IOException e) {
        showError("Network error: " + e.getMessage());
      }

      return;
    }

    try {
      TableBookingRequest request =
              new TableBookingRequest(
                      guestName,
                      LocalDateTime.of(date, time),
                      partySize,
                      table
              );

      reservationService.createReservation(request);
    } catch (IOException e) {
      showError(e.getMessage());
    }
  }

  private void drawSchedule() {
    scheduleView.draw(
            scheduleGrid,
            scheduleOverlayPane,
            tables,
            reservations
    );
  }

  private void showError(String message) {
    if (errorLabel == null) {
      return;
    }

    errorLabel.setText(message);
    errorLabel.setVisible(true);
    errorLabel.setManaged(true);
  }

  private void hideError() {
    if (errorLabel == null) {
      return;
    }

    errorLabel.setVisible(false);
    errorLabel.setManaged(false);
  }


  private void setEditMode(boolean edit) {
    isEditMode = edit;

    submitReservationButton.setText(edit ? "Apply" : "Create");

    deleteReservationButton.setVisible(edit);
    deleteReservationButton.setManaged(edit);
  }

  public void openReservationAsForm(Reservation r) {

    setEditMode(true);
    editingReservation = r;

    modalTitle.setText("Reservation " + r.getId());

    guestNameField.setText(r.getName());
    partySizeField.setText(String.valueOf(r.getPartySize()));

    reservationDatePicker.setValue(r.getDateTime().toLocalDate());

    reservationTimeField.setText(
            r.getDateTime()
                    .toLocalTime()
                    .format(timeFormatter)
    );

    hideError();
    tableComboBinder.populate(
            tableComboBox, tables
    );

    String selectedTableLabel =
            tableComboBinder.getLabelForTable(r.getTable());

    tableComboBox.getSelectionModel().clearSelection();
    tableComboBox.setValue(selectedTableLabel);

    reservationOverlay.setVisible(true);
    reservationOverlay.setManaged(true);
  }

  public void deleteReservation()
    {
        if (editingReservation == null)
        {
            showError("No reservation selected.");
            return;
        }
      DeleteReservationRequest request = new DeleteReservationRequest(editingReservation.getId());

        try
        {
            reservationService.deleteReservation(
                    request
            );
        }
        catch (IOException e)
        {
            showError("Failed to send delete request.");
        }
    }
}
