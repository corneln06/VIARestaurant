package org.store.viarestaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HostController
{
  private static final LocalTime SERVICE_START = LocalTime.of(17, 0);
  private static final int SLOT_COUNT = 12;
  private static final int SLOT_WIDTH = 80;
  private static final int LABEL_WIDTH = 80;
  private static final int ROW_HEIGHT = 56;

  private final SessionManager sessionManager = SessionManager.getInstance();
  private final Map<Integer, Button> tableButtonMap = new HashMap<>();
  private final Map<String, Integer> reservableTables = new LinkedHashMap<>();
  private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
  private final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("MMM d, uuuu 'at' HH:mm");

  @FXML private Label sidebarName;
  @FXML private Label sidebarRole;
  @FXML private Button btnTables;
  @FXML private Button btnReservations;
  @FXML private AnchorPane tablesPage;
  @FXML private AnchorPane reservationsPage;
  @FXML private GridPane tableGrid;
  @FXML private Label reservationsDateLabel;
  @FXML private GridPane reservationGrid;
  @FXML private Pane reservationOverlayPane;
  @FXML private StackPane tableModalOverlay;
  @FXML private Label tableModalTitle;
  @FXML private Label tableModalStateBadge;
  @FXML private Label tableModalInfo;
  @FXML private Button tableModalPrimaryButton;
  @FXML private Button tableModalSecondaryButton;
  @FXML private StackPane newReservationOverlay;
  @FXML private TextField guestNameField;
  @FXML private DatePicker reservationDatePicker;
  @FXML private TextField reservationTimeField;
  @FXML private TextField partySizeField;
  @FXML private ComboBox<String> tableComboBox;
  @FXML private Label newReservationErrorLabel;

  private int selectedTableId = -1;
  private Runnable tableModalPrimaryAction;
  private Runnable tableModalSecondaryAction;
  private LocalDate displayedReservationDate = LocalDate.now();

  @FXML
  private void initialize()
  {
    configureTableGrid();
    buildTableGrid();
    reservationDatePicker.setValue(LocalDate.now());
    reservationTimeField.setText("19:00");
    showTablesPage();
    refreshReservationsView();
  }

  public void initData(Workers worker)
  {
    sidebarName.setText(worker.getFirstName() + " " + worker.getLastName());
    sidebarRole.setText("Host");
    refreshTableGrid();
    refreshReservationsView();
  }

  @FXML
  private void showTablesPage()
  {
    setPageVisible(tablesPage, true);
    setPageVisible(reservationsPage, false);
    setActiveNavButton(btnTables, btnReservations);
    refreshTableGrid();
  }

  @FXML
  private void showReservationsPage()
  {
    setPageVisible(tablesPage, false);
    setPageVisible(reservationsPage, true);
    setActiveNavButton(btnReservations, btnTables);
    refreshReservationsView();
  }

  @FXML
  private void openNewReservationModal()
  {
    guestNameField.clear();
    partySizeField.clear();
    reservationDatePicker.setValue(displayedReservationDate);
    reservationTimeField.setText("19:00");
    hideReservationError();
    populateReservableTables();
    tableComboBox.getSelectionModel().clearSelection();
    showOverlay(newReservationOverlay);
  }

  @FXML
  private void createReservation()
  {
    hideReservationError();

    String guestName = guestNameField.getText() == null ? "" : guestNameField.getText().trim();
    if (guestName.isBlank())
    {
      showReservationError("Guest name is required.");
      return;
    }

    LocalDate reservationDate = reservationDatePicker.getValue();
    if (reservationDate == null)
    {
      showReservationError("Select a reservation date.");
      return;
    }

    LocalTime reservationTime;
    try
    {
      reservationTime = LocalTime.parse(reservationTimeField.getText().trim(), timeFormatter);
    }
    catch (DateTimeParseException exception)
    {
      showReservationError("Time must use 24-hour HH:mm format.");
      return;
    }

    int partySize;
    try
    {
      partySize = Integer.parseInt(partySizeField.getText().trim());
    }
    catch (NumberFormatException exception)
    {
      showReservationError("Party size must be a whole number.");
      return;
    }

    String selectedTable = tableComboBox.getValue();
    Integer tableId = reservableTables.get(selectedTable);
    if (tableId == null)
    {
      showReservationError("Select an available table.");
      return;
    }

    Table table = sessionManager.getTableById(tableId);
    if (table != null && partySize > table.getMaxSitting())
    {
      showReservationError("Selected table does not fit that party size.");
      return;
    }

    displayedReservationDate = reservationDate;
    sessionManager.createReservation(
        guestName,
        LocalDateTime.of(reservationDate, reservationTime),
        partySize,
        tableId
    );

    refreshTableGrid();
    refreshReservationsView();
    hideOverlay(newReservationOverlay);
  }

  @FXML
  private void closeTableModal(MouseEvent event)
  {
    hideOverlay(tableModalOverlay);
  }

  @FXML
  private void closeTableModalAction()
  {
    hideOverlay(tableModalOverlay);
  }

  @FXML
  private void closeNewReservationModal(MouseEvent event)
  {
    hideOverlay(newReservationOverlay);
  }

  @FXML
  private void closeNewReservationModalAction()
  {
    hideOverlay(newReservationOverlay);
  }

  @FXML
  private void consumeModalClick(MouseEvent event)
  {
    event.consume();
  }

  @FXML
  private void handleTableModalPrimaryAction()
  {
    if (tableModalPrimaryAction != null)
    {
      tableModalPrimaryAction.run();
      refreshTableGrid();
      refreshReservationsView();
      hideOverlay(tableModalOverlay);
    }
  }

  @FXML
  private void handleTableModalSecondaryAction()
  {
    if (tableModalSecondaryAction != null)
    {
      tableModalSecondaryAction.run();
      refreshTableGrid();
      refreshReservationsView();
      hideOverlay(tableModalOverlay);
    }
  }

  private void buildTableGrid()
  {
    tableGrid.getChildren().clear();
    tableButtonMap.clear();

    int index = 0;
    for (Table table : sessionManager.getTables())
    {
      Button tableButton = new Button();
      tableButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      tableButton.getStyleClass().add("table-btn");
      tableButton.setOnAction(event -> openTableModal(table.getId()));

      GridPane.setHgrow(tableButton, Priority.ALWAYS);
      GridPane.setVgrow(tableButton, Priority.ALWAYS);

      tableGrid.add(tableButton, index % 5, index / 5);
      tableButtonMap.put(table.getId(), tableButton);
      updateTableButton(tableButton, table);
      index++;
    }
  }

  private void refreshTableGrid()
  {
    for (Table table : sessionManager.getTables())
    {
      Button tableButton = tableButtonMap.get(table.getId());
      if (tableButton != null)
      {
        updateTableButton(tableButton, table);
      }
    }
  }

  private void refreshReservationsView()
  {
    reservationGrid.getChildren().clear();
    reservationGrid.getColumnConstraints().clear();
    reservationGrid.getRowConstraints().clear();
    reservationOverlayPane.getChildren().clear();

    reservationsDateLabel.setText(displayedReservationDate.toString());

    ColumnConstraints labelColumn = new ColumnConstraints();
    labelColumn.setPrefWidth(LABEL_WIDTH);
    reservationGrid.getColumnConstraints().add(labelColumn);

    for (int slot = 0; slot < SLOT_COUNT; slot++)
    {
      ColumnConstraints slotColumn = new ColumnConstraints();
      slotColumn.setPrefWidth(SLOT_WIDTH);
      reservationGrid.getColumnConstraints().add(slotColumn);
    }

    RowConstraints headerRow = new RowConstraints();
    headerRow.setPrefHeight(40);
    reservationGrid.getRowConstraints().add(headerRow);

    for (Table table : sessionManager.getTables())
    {
      RowConstraints row = new RowConstraints();
      row.setPrefHeight(ROW_HEIGHT);
      reservationGrid.getRowConstraints().add(row);
    }

    Label topLeftLabel = new Label("Table");
    topLeftLabel.getStyleClass().add("gantt-header");
    reservationGrid.add(topLeftLabel, 0, 0);

    for (int slot = 0; slot < SLOT_COUNT; slot++)
    {
      Label timeLabel = new Label(SERVICE_START.plusMinutes(slot * 30L).format(timeFormatter));
      timeLabel.getStyleClass().add("gantt-header");
      GridPane.setHalignment(timeLabel, HPos.CENTER);
      reservationGrid.add(timeLabel, slot + 1, 0);
    }

    for (Table table : sessionManager.getTables())
    {
      int rowIndex = table.getId();
      Label tableLabel = new Label("T" + table.getId());
      tableLabel.getStyleClass().add("gantt-table-label");
      reservationGrid.add(tableLabel, 0, rowIndex);
    }

    List<SessionManager.Reservation> reservations =
        sessionManager.getReservationsForDate(displayedReservationDate);

    for (SessionManager.Reservation reservation : reservations)
    {
      int minutesFromStart = reservation.dateTime().getHour() * 60
          + reservation.dateTime().getMinute()
          - SERVICE_START.getHour() * 60;
      int startColumn = Math.max(0, Math.min(SLOT_COUNT - 1, minutesFromStart / 30));
      final int columnSpan = 2;

      Label block = new Label(
          reservation.guestName() + " • " + reservation.partySize() + " guests"
      );
      block.getStyleClass().add("reservation-block");
      block.setMaxWidth(Double.MAX_VALUE);
      reservationGrid.add(block, startColumn + 1, reservation.tableId());
      GridPane.setColumnSpan(block, columnSpan);
    }

    double totalWidth = LABEL_WIDTH + SLOT_COUNT * SLOT_WIDTH;
    double totalHeight = 40 + sessionManager.getTables().size() * ROW_HEIGHT;
    reservationGrid.setPrefSize(totalWidth, totalHeight);
    reservationOverlayPane.setPrefSize(totalWidth, totalHeight);

    if (displayedReservationDate.equals(LocalDate.now()))
    {
      long minutesNow = LocalTime.now().getHour() * 60L + LocalTime.now().getMinute();
      long minutesStart = SERVICE_START.getHour() * 60L;
      long minutesEnd = LocalTime.of(23, 0).getHour() * 60L;

      if (minutesNow >= minutesStart && minutesNow <= minutesEnd)
      {
        double offset = (minutesNow - minutesStart) / 30.0 * SLOT_WIDTH;
        Rectangle nowLine = new Rectangle(2, totalHeight);
        nowLine.setFill(Color.web("#273469"));
        nowLine.setLayoutX(LABEL_WIDTH + offset);
        nowLine.setLayoutY(0);
        reservationOverlayPane.getChildren().add(nowLine);
      }
    }
  }

  private void openTableModal(int tableId)
  {
    Table table = sessionManager.getTableById(tableId);
    if (table == null)
    {
      return;
    }

    selectedTableId = tableId;
    tableModalTitle.setText("Table " + table.getId());
    setStateBadge(tableModalStateBadge, getStateText(table), getBadgeStyleClass(table));
    tableModalInfo.setText(buildTableInfo(table));
    configureTableModalActions(table);
    showOverlay(tableModalOverlay);
  }

  private void configureTableModalActions(Table table)
  {
    switch (stateOf(table))
    {
      case AVAILABLE -> {
        tableModalPrimaryButton.setText("Unavailable");
        tableModalSecondaryButton.setText("Reserved");
        setButtonVariant(tableModalPrimaryButton, "btn-danger");
        setButtonVariant(tableModalSecondaryButton, "btn-warning");
        tableModalPrimaryAction = () -> sessionManager.setTableUnavailable(selectedTableId);
        tableModalSecondaryAction = () -> sessionManager.setTableReserved(selectedTableId);
      }
      case UNAVAILABLE -> {
        tableModalPrimaryButton.setText("Reserved");
        tableModalSecondaryButton.setText("Available");
        setButtonVariant(tableModalPrimaryButton, "btn-warning");
        setButtonVariant(tableModalSecondaryButton, "btn-success");
        tableModalPrimaryAction = () -> sessionManager.setTableReserved(selectedTableId);
        tableModalSecondaryAction = () -> sessionManager.setTableAvailable(selectedTableId);
      }
      case RESERVED -> {
        tableModalPrimaryButton.setText("Unavailable");
        tableModalSecondaryButton.setText("Available");
        setButtonVariant(tableModalPrimaryButton, "btn-danger");
        setButtonVariant(tableModalSecondaryButton, "btn-success");
        tableModalPrimaryAction = () -> sessionManager.setTableUnavailable(selectedTableId);
        tableModalSecondaryAction = () -> sessionManager.setTableAvailable(selectedTableId);
      }
    }
  }

  private String buildTableInfo(Table table)
  {
    return switch (stateOf(table))
    {
      case RESERVED -> {
        SessionManager.Reservation reservation = sessionManager.getReservationForTable(table.getId());
        if (reservation == null)
        {
          yield "Awaiting reservation details.";
        }
        yield reservation.guestName()
            + " reserved this table for "
            + reservation.dateTime().format(dateTimeFormatter)
            + ".";
      }
      case UNAVAILABLE -> {
        int orderLines = sessionManager.getOrderLines(table.getId()).size();
        yield orderLines == 0
            ? "Guests have arrived and the table is occupied."
            : "Guests are seated with " + orderLines + " open order lines.";
      }
      case AVAILABLE -> "Ready for walk-ins and upcoming service.";
    };
  }

  private void populateReservableTables()
  {
    reservableTables.clear();
    tableComboBox.getItems().clear();

    for (Table table : sessionManager.getTables())
    {
      if (!(table.getStatus() instanceof AvailableState))
      {
        continue;
      }

      String label = "Table " + table.getId() + " • " + table.getMaxSitting() + " seats";
      reservableTables.put(label, table.getId());
      tableComboBox.getItems().add(label);
    }

    if (tableComboBox.getItems().isEmpty())
    {
      showReservationError("No available tables right now.");
    }
  }

  private void configureTableGrid()
  {
    if (!tableGrid.getColumnConstraints().isEmpty())
    {
      return;
    }

    for (int column = 0; column < 5; column++)
    {
      ColumnConstraints constraints = new ColumnConstraints();
      constraints.setHgrow(Priority.ALWAYS);
      tableGrid.getColumnConstraints().add(constraints);
    }

    for (int row = 0; row < 4; row++)
    {
      RowConstraints constraints = new RowConstraints();
      constraints.setPrefHeight(95);
      constraints.setVgrow(Priority.ALWAYS);
      tableGrid.getRowConstraints().add(constraints);
    }
  }

  private void updateTableButton(Button tableButton, Table table)
  {
    tableButton.setText("Table " + table.getId());
    tableButton.getStyleClass().removeAll(
        "table-available",
        "table-reserved",
        "table-unavailable"
    );
    tableButton.getStyleClass().add(getStateStyleClass(table));
  }

  private void setPageVisible(AnchorPane page, boolean visible)
  {
    page.setVisible(visible);
    page.setManaged(visible);
  }

  private void setActiveNavButton(Button activeButton, Button inactiveButton)
  {
    if (!activeButton.getStyleClass().contains("nav-btn-active"))
    {
      activeButton.getStyleClass().add("nav-btn-active");
    }
    inactiveButton.getStyleClass().remove("nav-btn-active");
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

  private void setStateBadge(Label label, String text, String styleClass)
  {
    label.setText(text);
    label.getStyleClass().removeAll("badge-available", "badge-reserved", "badge-unavailable");
    if (!label.getStyleClass().contains("state-badge"))
    {
      label.getStyleClass().add("state-badge");
    }
    label.getStyleClass().add(styleClass);
  }

  private void setButtonVariant(Button button, String styleClass)
  {
    button.getStyleClass().removeAll(
        "btn-primary",
        "btn-secondary",
        "btn-success",
        "btn-warning",
        "btn-danger"
    );
    if (!button.getStyleClass().contains("btn-base"))
    {
      button.getStyleClass().add("btn-base");
    }
    button.getStyleClass().add(styleClass);
  }

  private String getStateText(Table table)
  {
    return switch (stateOf(table))
    {
      case AVAILABLE -> "Available";
      case RESERVED -> "Reserved";
      case UNAVAILABLE -> "Unavailable";
    };
  }

  private String getStateStyleClass(Table table)
  {
    return switch (stateOf(table))
    {
      case AVAILABLE -> "table-available";
      case RESERVED -> "table-reserved";
      case UNAVAILABLE -> "table-unavailable";
    };
  }

  private String getBadgeStyleClass(Table table)
  {
    return switch (stateOf(table))
    {
      case AVAILABLE -> "badge-available";
      case RESERVED -> "badge-reserved";
      case UNAVAILABLE -> "badge-unavailable";
    };
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

  private StateView stateOf(Table table)
  {
    if (table.getStatus() instanceof AvailableState) return StateView.AVAILABLE;
    if (table.getStatus() instanceof ReservedState) return StateView.RESERVED;
    return StateView.UNAVAILABLE;
  }

  private enum StateView
  {
    AVAILABLE, RESERVED, UNAVAILABLE
  }
}
