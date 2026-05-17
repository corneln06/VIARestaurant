package org.store.viarestaurant.viewModel;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.*;
import org.store.viarestaurant.viewModel.components.ReservationComponent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class HostController
{
  private final ReservationComponent reservationComponent =
      new ReservationComponent();

  public void init(
      GridPane scheduleGrid,
      Pane scheduleOverlayPane)
  {
    reservationComponent.initView(
        scheduleGrid,
        scheduleOverlayPane
    );
  }

  public void initModal(
      StackPane overlay,
      TextField guestName,
      DatePicker datePicker,
      TextField timeField,
      TextField partySize,
      ComboBox<String> tableCombo,
      Label errorLabel)
  {
    reservationComponent.initModal(
        overlay,
        guestName,
        datePicker,
        timeField,
        partySize,
        tableCombo,
        errorLabel
    );
  }

  public void initClient(Client client)
  {
    reservationComponent.initClient(client);
  }

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
}

//package org.store.viarestaurant.viewModel;
//
//import java.io.IOException;
//import java.time.*;
//        import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//
//        import javafx.geometry.HPos;
//import javafx.scene.control.*;
//        import javafx.scene.layout.*;
//
//        import org.store.viarestaurant.model.entities.Reservation;
//import org.store.viarestaurant.model.entities.RestaurantTable;
//import org.store.viarestaurant.server.Client;
//import org.store.viarestaurant.server.dto.ReservationDto.*;
//
//public class HostController {
//
//    private Button submitReservationButton;
//    private Button deleteReservationButton;
//    private Label Modaltitle;
//
//    private static final LocalTime SERVICE_START = LocalTime.of(17, 0);
//    private static final int SLOT_COUNT = 12;
//    private static final int SLOT_WIDTH = 80;
//    private static final int LABEL_WIDTH = 80;
//    private static final int ROW_HEIGHT = 56;
//
//    protected Client client;
//
//    private final DateTimeFormatter timeFormatter =
//            DateTimeFormatter.ofPattern("HH:mm");
//
//    private final Map<String, RestaurantTable> reservableTables =
//            new LinkedHashMap<>();
//
//    private GridPane scheduleGrid;
//    private Pane scheduleOverlayPane;
//
//    private StackPane newReservationOverlay;
//    private TextField guestNameField;
//    private DatePicker reservationDatePicker;
//    private TextField reservationTimeField;
//    private TextField partySizeField;
//    private ComboBox<String> tableComboBox;
//    private Label newReservationErrorLabel;
//
//    private boolean isEditMode = false;
//    private Reservation editingReservation;
//
//    private ArrayList<RestaurantTable> tables = new ArrayList<>();
//    private ArrayList<Reservation> reservations = new ArrayList<>();
//
//    public void init(GridPane scheduleGrid, Pane scheduleOverlayPane) {
//        this.scheduleGrid = scheduleGrid;
//        this.scheduleOverlayPane = scheduleOverlayPane;
//    }
//
//    public void initModal(
//            StackPane overlay,
//            TextField guestName,
//            DatePicker datePicker,
//            TextField timeField,
//            TextField partySize,
//            ComboBox<String> tableCombo,
//            Label errorLabel,
//            Button submit,
//            Button delete,
//            Label title
//    ) {
//        this.newReservationOverlay = overlay;
//        this.guestNameField = guestName;
//        this.reservationDatePicker = datePicker;
//        this.reservationTimeField = timeField;
//        this.partySizeField = partySize;
//        this.tableComboBox = tableCombo;
//        this.newReservationErrorLabel = errorLabel;
//        this.submitReservationButton = submit;
//        this.deleteReservationButton = delete;
//        this.Modaltitle = title;
//    }
//
//    ///////////////////// SOCKET INITIALIZATION ///////////////////////////
//
//    public void initClient(Client client) {
//        this.client = client;
//
//        client.setTablesListener(response -> {
//            tables = response.getTables();
//            populateTableCombo();
//            drawSchedule();
//        });
//
//        client.setReservationsListener(response -> {
//            reservations = response.getReservations();
//            drawSchedule();
//        });
//
//        client.setCreateReservationListener(response -> {
//            if (response.isSuccess()) {
//                closeNewReservationModal();
//                refreshSchedule();
//            } else {
//                showReservationError(response.getMessage());
//            }
//        });
//
//        client.setReservationCreatedListener(msg -> refreshSchedule());
//        client.setUpdateReservationListener(response -> {
//            if (response.isSuccess()) {
//                closeNewReservationModal();
//                refreshSchedule();
//            } else {
//                showReservationError(response.getMessage());
//            }
//        });
//
//        client.setDeleteReservationListener(response -> {
//            if (response.isSuccess()) {
//                closeNewReservationModal();
//                refreshSchedule();
//            } else {
//                showReservationError(response.getMessage());
//            }
//        });
//
//    }
//
//    ///////////////////// RESERVATIONS ///////////////////////////
//
//    public void createReservation() {
//
//        hideReservationError();
//
//        String guestName =
//                Optional.ofNullable(guestNameField.getText())
//                        .orElse("")
//                        .trim();
//
//        if (guestName.isBlank()) {
//            showReservationError("Guest name is required.");
//            return;
//        }
//
//        LocalDate date = reservationDatePicker.getValue();
//        if (date == null) {
//            showReservationError("Select a reservation date.");
//            return;
//        }
//
//        LocalTime time;
//        try {
//            time = LocalTime.parse(reservationTimeField.getText().trim(), timeFormatter);
//        } catch (DateTimeParseException e) {
//            showReservationError("Time must be HH:mm format.");
//            return;
//        }
//
//        int partySize;
//        try {
//            partySize = Integer.parseInt(partySizeField.getText().trim());
//        } catch (NumberFormatException e) {
//            showReservationError("Invalid party size.");
//            return;
//        }
//
//        RestaurantTable table =
//                reservableTables.get(tableComboBox.getValue());
//
//        if (table == null) {
//            showReservationError("Select a table.");
//            return;
//        }
//
//        if (isEditMode) {
//
//            try {
//                UpdateReservationRequest request =
//                        new UpdateReservationRequest(
//                                editingReservation.getId(),
//                                guestName,
//                                LocalDateTime.of(date, time),
//                                partySize,
//                                table
//                        );
//
//                client.send(request);
//
//
//            } catch (IOException e) {
//                showReservationError("Network error: " + e.getMessage());
//            }
//
//            return;
//        }
//        try {
//            TableBookingRequest request =
//                    new TableBookingRequest(
//                            guestName,
//                            LocalDateTime.of(date, time),
//                            partySize,
//                            table
//                    );
//
//            client.send(request);
//
//        } catch (IOException e) {
//            showReservationError("Network error: " + e.getMessage());
//        }
//    }
//
//    public void openReservationAsForm(Reservation r) {
//
//        setEditMode(true);
//        editingReservation = r;
//
//        Modaltitle.setText("Reservation " + r.getId());
//
//        guestNameField.setText(r.getName());
//        partySizeField.setText(String.valueOf(r.getPartySize()));
//
//        reservationDatePicker.setValue(r.getDateTime().toLocalDate());
//
//        reservationTimeField.setText(
//                r.getDateTime()
//                        .toLocalTime()
//                        .format(timeFormatter)
//        );
//
//        hideReservationError();
//        populateTableCombo();
//
//        String selectedTableLabel =
//                reservableTables.entrySet()
//                        .stream()
//                        .filter(e ->
//                                Objects.equals(e.getValue().getId(), r.getTable().getId())
//                        )
//                        .map(Map.Entry::getKey)
//                        .findFirst()
//                        .orElse(null);
//
//        tableComboBox.getSelectionModel().clearSelection();
//        tableComboBox.setValue(selectedTableLabel);
//
//        showOverlay(newReservationOverlay);
//    }
//
//    //////////////////// SCHEDULE /////////////////////////////
//
//    private void drawSchedule() {
//
//        if(scheduleGrid == null || scheduleOverlayPane == null)
//        {
//            return;
//        }
//
//        scheduleGrid.getChildren().clear();
//        scheduleGrid.getColumnConstraints().clear();
//        scheduleGrid.getRowConstraints().clear();
//        scheduleOverlayPane.getChildren().clear();
//
//        ColumnConstraints labelCol = new ColumnConstraints();
//        labelCol.setPrefWidth(LABEL_WIDTH);
//        scheduleGrid.getColumnConstraints().add(labelCol);
//
//        for(int s = 0; s < SLOT_COUNT; s++)
//        {
//            ColumnConstraints slotCol = new ColumnConstraints();
//            slotCol.setPrefWidth(SLOT_WIDTH);
//            scheduleGrid.getColumnConstraints().add(slotCol);
//        }
//
//        RowConstraints headerRow = new RowConstraints();
//        headerRow.setPrefHeight(40);
//        scheduleGrid.getRowConstraints().add(headerRow);
//
//        for(RestaurantTable ignored : tables)
//        {
//            RowConstraints row = new RowConstraints();
//            row.setPrefHeight(ROW_HEIGHT);
//            scheduleGrid.getRowConstraints().add(row);
//        }
//
//        Label corner = new Label("Table");
//        corner.getStyleClass().add("gantt-header");
//        scheduleGrid.add(corner, 0, 0);
//
//        for(int s = 0; s < SLOT_COUNT; s++)
//        {
//            Label timeLabel =
//                    new Label(
//                            SERVICE_START
//                                    .plusMinutes(s * 30L)
//                                    .format(timeFormatter)
//                    );
//
//            timeLabel.getStyleClass().add("gantt-header");
//            GridPane.setHalignment(timeLabel, HPos.CENTER);
//            scheduleGrid.add(timeLabel, s + 1, 0);
//        }
//        Map<Integer, Integer> tableRows = new LinkedHashMap<>();
//        int row = 1;
//
//        for (RestaurantTable table : tables) {
//
//            tableRows.put(table.getId(), row);
//
//            Label lbl = new Label("T" + table.getId());
//            scheduleGrid.add(lbl, 0, row);
//
//            row++;
//        }
//
//        for (Reservation r : reservations) {
//
//            if (r.getTable() == null) continue;
//
//            Integer rowIndex = tableRows.get(r.getTable().getId());
//            if (rowIndex == null) continue;
//
//            int minutes =
//                    r.getDateTime().getHour() * 60 +
//                            r.getDateTime().getMinute() -
//                            SERVICE_START.getHour() * 60;
//
//            int col = Math.max(0, Math.min(SLOT_COUNT - 1, minutes / 30));
//
//            Label block = new Label(
//                    r.getName() + " • " + r.getPartySize()
//            );
//
//            block.setUserData(r);
//
//            block.setOnMouseClicked(e ->
//                    openReservationAsForm((Reservation) block.getUserData())
//            );
//
//            scheduleGrid.add(block, col + 1, rowIndex);
//            GridPane.setColumnSpan(block, 2);
//        }
//    }
//
//
//    public void refreshSchedule()
//    {
//        if(client == null)
//        {
//            return;
//        }
//
//        try
//        {
//            client.send(new GetTablesRequest());
//            client.send(new GetReservationsRequest());
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    //////////////////// TABLE COMBO /////////////////////////////////
//
//    private void populateTableCombo() {
//
//        if (tableComboBox == null) return;
//
//        reservableTables.clear();
//        tableComboBox.getItems().clear();
//
//        for (RestaurantTable t : tables) {
//
//            String label =
//                    "Table " + t.getId() + " • " + t.getMaxSitting();
//
//            reservableTables.put(label, t);
//            tableComboBox.getItems().add(label);
//        }
//    }
//
//    //////////////////// UI HELPERS /////////////////////////////////
//
//    private void showReservationError(String msg) {
//        newReservationErrorLabel.setText(msg);
//        newReservationErrorLabel.setVisible(true);
//        newReservationErrorLabel.setManaged(true);
//    }
//
//    private void hideReservationError() {
//        newReservationErrorLabel.setVisible(false);
//        newReservationErrorLabel.setManaged(false);
//    }
//
//    public void openNewReservationModal() {
//        setEditMode(false);
//        Modaltitle.setText("New Reservation");
//
//        guestNameField.clear();
//        partySizeField.clear();
//        reservationDatePicker.setValue(LocalDate.now());
//        reservationTimeField.setText("19:00");
//
//        populateTableCombo();
//        showOverlay(newReservationOverlay);
//    }
//
//    public void closeNewReservationModal() {
//        setEditMode(false);
//        editingReservation = null;
//        hideOverlay(newReservationOverlay);
//    }
//
//    private void showOverlay(StackPane overlay) {
//        overlay.setVisible(true);
//        overlay.setManaged(true);
//    }
//
//    private void hideOverlay(StackPane overlay) {
//        overlay.setVisible(false);
//        overlay.setManaged(false);
//    }
//
//    private void setEditMode(boolean edit) {
//        isEditMode = edit;
//
//        submitReservationButton.setText(edit ? "Apply" : "Create");
//
//        deleteReservationButton.setVisible(edit);
//        deleteReservationButton.setManaged(edit);
//    }
//
//    public void deleteReservation()
//    {
//        if (editingReservation == null)
//        {
//            showReservationError("No reservation selected.");
//            return;
//        }
//
//        try
//        {
//            client.send(
//                    new DeleteReservationRequest(editingReservation.getId())
//            );
//        }
//        catch (IOException e)
//        {
//            showReservationError("Failed to send delete request.");
//        }
//    }
//}