package org.store.viarestaurant.viewModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.store.viarestaurant.dao.ReservationDAOImpl;
import org.store.viarestaurant.dao.RestaurantTableDAOImpl;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.GetReservationsRequest;
import org.store.viarestaurant.server.dto.ReservationDto.GetTablesRequest;
import org.store.viarestaurant.server.dto.ReservationDto.TableBookingRequest;

public class HostController
{
  private Button submitReservationButton;
  private Button deleteReservationButton;
  private Label Modaltitle;

  private static final LocalTime SERVICE_START = LocalTime.of(17, 0);
  private static final int SLOT_COUNT = 12;
  private static final int SLOT_WIDTH = 80;
  private static final int LABEL_WIDTH = 80;
  private static final int ROW_HEIGHT = 56;

  protected Client client;

  private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
  private final Map<String, RestaurantTable> reservableTables = new LinkedHashMap<>();

  private GridPane scheduleGrid;
  private Pane scheduleOverlayPane;

  private StackPane newReservationOverlay;
  private TextField guestNameField;
  private DatePicker reservationDatePicker;
  private TextField reservationTimeField;
  private TextField partySizeField;
  private ComboBox<String> tableComboBox;
  private Label newReservationErrorLabel;

  private ReservationDAOImpl reservationDAO;
  private RestaurantTableDAOImpl tableDAO;
  private boolean isEditMode = false;
  private Reservation editingReservation;
  private ArrayList<RestaurantTable> tables = new ArrayList<>();
  private ArrayList<Reservation> reservations = new ArrayList<>();

  public void init(GridPane scheduleGrid, Pane scheduleOverlayPane)
  {
    this.scheduleGrid = scheduleGrid;
    this.scheduleOverlayPane = scheduleOverlayPane;
//    try
//    {
//      reservationDAO = ReservationDAOImpl.getInstance();
//      tableDAO = RestaurantTableDAOImpl.getInstance();
//    }
//    catch (SQLException e)
//    {
//      e.printStackTrace();
//    }
  }

  public void initModal(StackPane overlay, TextField guestName, DatePicker datePicker,
      TextField timeField, TextField partySize, ComboBox<String> tableCombo, Label errorLabel, Button submit, Button delete, Label title)
  {
    this.newReservationOverlay = overlay;
    this.guestNameField = guestName;
    this.reservationDatePicker = datePicker;
    this.reservationTimeField = timeField;
    this.partySizeField = partySize;
    this.tableComboBox = tableCombo;
    this.newReservationErrorLabel = errorLabel;
    this.submitReservationButton = submit;
    this.deleteReservationButton = delete;
    this.Modaltitle = title;
  }

  public void initClient(Client client)
  {
    this.client = client;

    client.setTablesListener(response ->
    {
      tables = response.getTables();
      populateTableCombo();
      drawSchedule();
    });

    client.setReservationsListener(response ->
    {
      reservations = response.getReservations();
      drawSchedule();
    });

    client.setCreateReservationListener(response ->
    {
      if(response.isSuccess())
      {
        closeNewReservationModal();
        refreshSchedule();
      }
      else
      {
        showReservationError(response.getMessage());
      }
    });

    client.setReservationCreatedListener(message ->
    {
      refreshSchedule();
    });
  }

  public void openNewReservationModal()
  {
    setEditMode(false);
    Modaltitle.setText("New Reservation");
    guestNameField.clear();
    partySizeField.clear();
    reservationDatePicker.setValue(LocalDate.now());
    reservationTimeField.setText("19:00");

    hideReservationError();
    populateTableCombo();

    tableComboBox.getSelectionModel().clearSelection();
    showOverlay(newReservationOverlay);
  }

  public void closeNewReservationModal()
  {
    setEditMode(false);
    editingReservation = null;
    hideOverlay(newReservationOverlay);
  }

  public void createReservation()
  {
    hideReservationError();

    String guestName =
        guestNameField.getText() == null
            ? ""
            : guestNameField.getText().trim();

    if(guestName.isBlank())
    {
      showReservationError("Guest name is required.");
      return;
    }

    LocalDate date = reservationDatePicker.getValue();
    if (date == null) {
      showReservationError("Select a reservation date.");
      return;
    }

    LocalTime time;

    try
    {
      time = LocalTime.parse(
          reservationTimeField.getText().trim(),
          timeFormatter
      );
    }
    catch(DateTimeParseException e)
    {
      showReservationError("Time must use 24-hour HH:mm format.");
      return;
    }

    int partySize;

    try
    {
      partySize = Integer.parseInt(
          partySizeField.getText().trim()
      );
    }
    catch(NumberFormatException e)
    {
      showReservationError("Party size must be a whole number.");
      return;
    }

    RestaurantTable table =
        reservableTables.get(tableComboBox.getValue());

    if(table == null)
    {
      showReservationError("Select a table.");
      return;
    }

    try {
      RestaurantTable table = tableDAO.getRestaurantTableByID(tableId);
      LocalDateTime dt = LocalDateTime.of(date, time);

      // EDIT MODE
      if (isEditMode) {

        editingReservation.setName(guestName);
        editingReservation.setPartySize(partySize);
        editingReservation.setDateTime(dt);
        editingReservation.setTable(table);

        reservationDAO.updateReservation(editingReservation);

        isEditMode = false;
        editingReservation = null;

        closeNewReservationModal();
        refreshSchedule();
        return;
      }

      // CREATE MODE
      ArrayList<Reservation> sameTimeReservations =
              reservationDAO.getReservationByDate(dt);

      boolean conflict = sameTimeReservations.stream()
              .anyMatch(reservation ->
                      Objects.equals(reservation.getTable().getId(), tableId)
              );
    try
    {
      TableBookingRequest request =
          new TableBookingRequest(
              guestName,
              LocalDateTime.of(date, time),
              partySize,
              table
          );

      client.send(request);
    }
    catch(IOException e)
    {
      showReservationError(e.getMessage());
    }
  }

  public void refreshSchedule()
  {
    if(client == null)
    {
      return;
    }

    try
    {
      client.send(new GetTablesRequest());
      client.send(new GetReservationsRequest());
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  private void drawSchedule()
  {
    if(scheduleGrid == null || scheduleOverlayPane == null)
    {
      return;
    }

    scheduleGrid.getChildren().clear();
    scheduleGrid.getColumnConstraints().clear();
    scheduleGrid.getRowConstraints().clear();
    scheduleOverlayPane.getChildren().clear();

    ColumnConstraints labelCol = new ColumnConstraints();
    labelCol.setPrefWidth(LABEL_WIDTH);
    scheduleGrid.getColumnConstraints().add(labelCol);

    for(int s = 0; s < SLOT_COUNT; s++)
    {
      ColumnConstraints slotCol = new ColumnConstraints();
      slotCol.setPrefWidth(SLOT_WIDTH);
      scheduleGrid.getColumnConstraints().add(slotCol);
    }

    RowConstraints headerRow = new RowConstraints();
    headerRow.setPrefHeight(40);
    scheduleGrid.getRowConstraints().add(headerRow);

    for(RestaurantTable ignored : tables)
    {
      RowConstraints row = new RowConstraints();
      row.setPrefHeight(ROW_HEIGHT);
      scheduleGrid.getRowConstraints().add(row);
    }

    Label corner = new Label("Table");
    corner.getStyleClass().add("gantt-header");
    scheduleGrid.add(corner, 0, 0);

    for(int s = 0; s < SLOT_COUNT; s++)
    {
      Label timeLabel =
          new Label(
              SERVICE_START
                  .plusMinutes(s * 30L)
                  .format(timeFormatter)
          );

      timeLabel.getStyleClass().add("gantt-header");
      GridPane.setHalignment(timeLabel, HPos.CENTER);
      scheduleGrid.add(timeLabel, s + 1, 0);
    }

    int rowIndex = 1;
    Map<Integer, Integer> tableRows = new LinkedHashMap<>();

    for(RestaurantTable table : tables)
    {
      tableRows.put(table.getId(), rowIndex);

      Label lbl = new Label("T" + table.getId());
      lbl.getStyleClass().add("gantt-table-label");

      scheduleGrid.add(lbl, 0, rowIndex);

      rowIndex++;
    }

    for(Reservation reservation : reservations)
    {
      if(reservation.getTable() == null)
      {
        continue;
      }

      Integer row =
          tableRows.get(reservation.getTable().getId());

      if(row == null)
      {
        continue;
      }

      int minutesFromStart =
          reservation.getDateTime().getHour() * 60
              + reservation.getDateTime().getMinute()
              - SERVICE_START.getHour() * 60;

      int startCol =
          Math.max(
              0,
              Math.min(
                  SLOT_COUNT - 1,
                  minutesFromStart / 30
              )
          );

      Label block =
          new Label(
              reservation.getName()
                  + " • "
                  + reservation.getPartySize()
                  + " guests"
          );

      block.getStyleClass().add("reservation-block");
      block.setMaxWidth(Double.MAX_VALUE);

      block.setUserData(r);

      block.setOnMouseClicked(e -> {
        Reservation reservation = (Reservation) block.getUserData();

        openReservationAsForm(reservation);
      });

      scheduleGrid.add(block, startCol + 1, row);
      GridPane.setColumnSpan(block, 2);
    }

    double totalWidth =
        LABEL_WIDTH + SLOT_COUNT * SLOT_WIDTH;

    double totalHeight =
        40 + tables.size() * ROW_HEIGHT;

    scheduleGrid.setPrefSize(totalWidth, totalHeight);
    scheduleOverlayPane.setPrefSize(totalWidth, totalHeight);

    long minutesNow =
        LocalTime.now().getHour() * 60L
            + LocalTime.now().getMinute();

    long minutesStart =
        SERVICE_START.getHour() * 60L;

    long minutesEnd =
        LocalTime.of(23, 0).getHour() * 60L;

    if(minutesNow >= minutesStart && minutesNow <= minutesEnd)
    {
      double offset =
          (minutesNow - minutesStart)
              / 30.0
              * SLOT_WIDTH;

      Rectangle nowLine =
          new Rectangle(2, totalHeight);

      nowLine.setFill(Color.RED);
      nowLine.setLayoutX(LABEL_WIDTH + offset);
      nowLine.setLayoutY(0);

      scheduleOverlayPane.getChildren().add(nowLine);
    }
  }

  private void populateTableCombo()
  {
    if(tableComboBox == null)
    {
      return;
    }

    reservableTables.clear();
    tableComboBox.getItems().clear();

    for(RestaurantTable table : tables)
    {
      String label =
          "Table "
              + table.getId()
              + " • "
              + table.getMaxSitting()
              + " seats";

      reservableTables.put(label, table);
      tableComboBox.getItems().add(label);
    }
  }

  private void showOverlay(StackPane overlay)
  {
    overlay.setVisible(true);
    overlay.setManaged(true);
  }

  private void hideOverlay(StackPane overlay)
  {
    overlay.setVisible(false);
    overlay.setManaged(false);
  }

  private void showReservationError(String message)
  {
    newReservationErrorLabel.setText(message);
    newReservationErrorLabel.setVisible(true);
    newReservationErrorLabel.setManaged(true);
  }

  private void hideReservationError()
  {
    newReservationErrorLabel.setVisible(false);
    newReservationErrorLabel.setManaged(false);
  }

  public void openReservationAsForm(Reservation r)
  {
    setEditMode(true);
    Modaltitle.setText("Reservation " + r.getId() + " details");
    editingReservation = r;

    guestNameField.setText(r.getName());
    partySizeField.setText(String.valueOf(r.getPartySize()));

    reservationDatePicker.setValue(r.getDateTime().toLocalDate());
    reservationTimeField.setText(
            r.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
    );

    hideReservationError();
    populateTableCombo();

    String selected = reservableTables.entrySet().stream()
            .filter(e -> e.getValue().equals(r.getTable().getId()))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);

    tableComboBox.setValue(selected);

    showOverlay(newReservationOverlay);
  }

  public void deleteReservation()
  {
    if (editingReservation == null) return;

    try {
      reservationDAO.deleteById(editingReservation.getId());

      isEditMode = false;
      editingReservation = null;

      closeNewReservationModal();
      refreshSchedule();

    } catch (SQLException e) {
      showReservationError(e.getMessage());
    }
  }
  private void setEditMode(boolean edit)
  {
    isEditMode = edit;

    if (edit)
    {
      submitReservationButton.setText("Apply");
      deleteReservationButton.setVisible(true);
      deleteReservationButton.setManaged(true);
    }
    else
    {
      submitReservationButton.setText("Create");
      deleteReservationButton.setVisible(false);
      deleteReservationButton.setManaged(false);
      editingReservation = null;
    }
  }
}
