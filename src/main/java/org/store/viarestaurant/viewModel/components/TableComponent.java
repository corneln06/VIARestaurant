package org.store.viarestaurant.viewModel.components;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.store.viarestaurant.dao.ReservationDAOImpl;
import org.store.viarestaurant.dao.RestaurantTableDAOImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.store.viarestaurant.model.entities.*;
import org.store.viarestaurant.model.enums.WorkerRole;
import javafx.scene.layout.*;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.SeatTableRequest;
import org.store.viarestaurant.server.dto.ReservationDto.SetTableAvailableRequest;
import org.store.viarestaurant.viewModel.services.ReservationService;
import org.store.viarestaurant.viewModel.services.TablesService;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.viewModel.services.WorkersService;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.ReservationDto.TableCreateRequest;
import org.store.viarestaurant.server.dto.ReservationDto.UpdateReservationRequest;
import org.store.viarestaurant.viewModel.services.ReservationService;
import org.store.viarestaurant.viewModel.services.TablesService;

public class TableComponent
{
  private GridPane tableGrid;
  private final Map<Integer, Button> tableButtonMap = new HashMap<>();

  private StackPane tableModalOverlay;
  private Label tableModalTitle;
  private Label tableModalStateBadge;
  private Label tableModalInfo;
  private Client client;

  private StackPane newTableModalOverlay;
  private TextField maxSittingField;
  private Label newTableErrorLabel;

  private ReservationService reservationService;
  private TablesService tablesService;
  private ArrayList<RestaurantTable> tables = new ArrayList<>();
  private List<Reservation> reservations = new ArrayList<>();
  private ArrayList<Workers> workers = new ArrayList<>();
  private WorkersService workersService;

  private Button tableModalPrimaryButton;
  private ComboBox<String> tableModalWaiterComboBox;
  private final Map<String, Workers> waiterMap = new LinkedHashMap<>();
  private int currentTableId;

  private String labelSuffix = "";

  public void setLabelSuffix(String labelSuffix) {
      this.labelSuffix = labelSuffix;
  }




  public void initGrid(GridPane tableGrid)
  {
    this.tableGrid = tableGrid;
    configureTableGrid();
    buildTableGrid();
  }

  public void initModal(
      StackPane overlay,
      Label title,
      Label badge,
      Label info,
      StackPane newOverlay,
      TextField maxSitting,
      Label error,
      ComboBox<String> waiterComboBox,
      Button primaryButton)
  {
    this.tableModalOverlay = overlay;
    this.tableModalTitle = title;
    this.tableModalStateBadge = badge;
    this.tableModalInfo = info;
    this.tableModalWaiterComboBox = waiterComboBox;
    this.tableModalPrimaryButton = primaryButton;
    this.newTableModalOverlay = newOverlay;
    this.maxSittingField = maxSitting;
    this.newTableErrorLabel = error;
  }

  public void loadTables(){
    if (tablesService == null) {
      return;
    }

    tablesService.loadTables();
  }
  public void loadData()
  {
    try
    {
      reservationService.loadSchedule();
      workersService.loadWorkers();
    }
    catch(IOException e)
    {
      throw new RuntimeException(e);
    }
  }



  public void refreshTableGrid()
  {
    if (tableGrid == null) return;
    Set<Integer> reservedIds = new HashSet<>();

    for (Reservation r : reservations)
    {
      reservedIds.add(r.getTable().getId());
    }
    for (RestaurantTable table : tables)
    {
      Button btn = tableButtonMap.get(table.getId());
      if (btn != null)
      {
        btn.setText("Table " + table.getId() + labelSuffix);
        boolean reserved = reservedIds.contains(table.getId());
        boolean seated = table.getStatus().getName().equals("Seated");
        updateTableButton(btn, reserved, seated);
      }
    }
  }

  public void closeTableModal()
  {
    if (tableModalOverlay == null) return;
    tableModalOverlay.setVisible(false);
    tableModalOverlay.setManaged(false);
  }

  public void configureTableGrid()
  {
    for (int col = 0; col < 5; col++)
    {
      ColumnConstraints cc = new ColumnConstraints();
      cc.setHgrow(Priority.ALWAYS);
      tableGrid.getColumnConstraints().add(cc);
    }
    for (int row = 0; row < 4; row++)
    {
      RowConstraints rc = new RowConstraints();
      rc.setPrefHeight(95);
      rc.setVgrow(Priority.ALWAYS);
      tableGrid.getRowConstraints().add(rc);
    }
  }

  private void buildTableGrid()
  {
    tableGrid.getChildren().clear();
    tableButtonMap.clear();
    for (int i = 0; i < tables.size(); i++)
    {
      RestaurantTable table = tables.get(i);
      Button btn = new Button();
      btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      btn.getStyleClass().add("table-btn");
      GridPane.setHgrow(btn, Priority.ALWAYS);
      GridPane.setVgrow(btn, Priority.ALWAYS);
      final int id = table.getId();
      btn.setOnAction(e -> {
          if (labelSuffix.isEmpty()){
            openTableModal(id);
          } else {
              System.out.println("YES");
          }
      });
      tableGrid.add(btn, i % 5, i / 5);
      tableButtonMap.put(id, btn);
      updateTableButton(btn, false, false);
    }
    refreshTableGrid();
  }

  private void updateTableButton(Button btn, boolean reserved, boolean seated)
  {
    btn.getStyleClass().removeAll("table-available", "table-reserved", "table-unavailable");
    if (seated) btn.getStyleClass().add("table-unavailable");
    else if (reserved) btn.getStyleClass().add("table-reserved");
    else btn.getStyleClass().add("table-available");
  }

  private void openTableModal(int tableId)
  {
    currentTableId = tableId;
    waiterMap.clear();
    tableModalWaiterComboBox.getItems().clear();

    for(Workers worker : workers)
    {
      if(worker.getRole() == WorkerRole.Waiter)
      {
        String label =
            worker.getFirstName() + " " + worker.getLastName();

        waiterMap.put(label, worker);
        tableModalWaiterComboBox.getItems().add(label);
      }
    }

    tableModalWaiterComboBox.getSelectionModel().clearSelection();
    if(tableModalOverlay == null)
    {
      return;
    }
    RestaurantTable table = tables.stream()
        .filter(t -> t.getId() == tableId)
        .findFirst()
        .orElse(null);

    if(table == null)
    {
      return;
    }
    boolean reserved = reservations.stream()
        .anyMatch(r ->
            r.getTable() != null &&
                r.getTable().getId() == tableId
        );

    tableModalTitle.setText("Table " + tableId);
    tableModalStateBadge.getStyleClass().removeAll(
        "badge-available",
        "badge-reserved",
        "badge-unavailable"
    );

    boolean seated =
        table.getStatus() != null &&
            table.getStatus().getName().equals("Seated");

    if(seated)
    {
      tableModalStateBadge.setText("Seated");
      tableModalStateBadge.getStyleClass().add("badge-unavailable");
      tableModalInfo.setText("Table is currently occupied.");
      tableModalPrimaryButton.setText("Set Available");
      tableModalPrimaryButton.setOnAction(e -> setAvailable());
    }
    else if(reserved)
    {
      tableModalStateBadge.setText("Reserved");
      tableModalStateBadge.getStyleClass().add("badge-reserved");
      tableModalInfo.setText("This table has a reservation today.");
      tableModalPrimaryButton.setText("Seat Table");
      tableModalPrimaryButton.setOnAction(e -> seatTable());
    }
    else
    {
      tableModalStateBadge.setText("Available");
      tableModalStateBadge.getStyleClass().add("badge-available");
      tableModalInfo.setText("Ready for walk-ins and upcoming service.");
      tableModalPrimaryButton.setText("Seat Table");
      tableModalPrimaryButton.setOnAction(e -> seatTable());

    }
    tableModalOverlay.setVisible(true);
    tableModalOverlay.setManaged(true);
    }


  public void initClient(Client client)
  {
    this.client = client;

    tablesService = new TablesService(client);
    reservationService = new ReservationService(client);
    workersService = new WorkersService(client);

    tablesService.onTablesLoaded(response ->
    {
      tables = response.getTables();
      buildTableGrid();
      refreshTableGrid();
    });
    workersService.onWorkersLoaded(response ->{
      workers = response.getWorkers();

    });

    tablesService.onTablesCreated(response -> {
      if (response.isSuccess()) {
        closeNewTableModal();
        loadTables();
      } else{
        showError(response.getMessage());
      }
    });
    reservationService.onUpdatedMessage(message ->
    {
      closeTableModal();
      loadData();
    });
    reservationService.onReservationLoaded(response ->
    {
      this.reservations = response.getReservations();

      refreshTableGrid();
    });

      tablesService.onTablesCreatedResponse(response -> {
        loadTables();
      });
    }




  public void openNewTableModal() {
    if (newTableModalOverlay == null) return;

    maxSittingField.clear();

    hideError();

    newTableModalOverlay.setVisible(true);
    newTableModalOverlay.setManaged(true);
  }

  public void seatTable()
  {
    Workers waiter =
        waiterMap.get(
            tableModalWaiterComboBox.getValue()
        );
    if(waiter == null)
    {
      return;
    }
    try
    {
      client.send(
          new SeatTableRequest(
              currentTableId,
              waiter.getId()
          )
      );
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    refreshTableGrid();
  }

  public void setAvailable()
  {
    try
    {
      client.send(
          new SetTableAvailableRequest(currentTableId)
      );
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public void closeNewTableModal() {
    if (newTableModalOverlay == null) return;

    newTableModalOverlay.setVisible(false);
    newTableModalOverlay.setManaged(false);
  }

  private void showError(String message) {
    if (newTableErrorLabel == null) {
      return;
    }

    newTableErrorLabel.setText(message);
    newTableErrorLabel.setVisible(true);
    newTableErrorLabel.setManaged(true);
  }

  private void hideError() {
    if (newTableErrorLabel == null) {
      return;
    }

    newTableErrorLabel.setVisible(false);
    newTableErrorLabel.setManaged(false);
  }

  public void createTable() {
    hideError();

    String raw = maxSittingField.getText();

    if (raw == null || raw.isBlank()) {
      showError("Max seats required.");
      return;
    }

    int maxSitters;

    try {
      maxSitters = Integer.parseInt(raw.trim());
    } catch (NumberFormatException e) {
      showError("Must be a number.");
      return;
    }

    if (maxSitters <= 0 || maxSitters > 50) {
      showError("Seats must be between 1 and 50.");
      return;
    }

    try {
      TableCreateRequest request = new TableCreateRequest(maxSitters);

      tablesService.createTable(request);

    } catch (Exception e) {
      showError("Failed: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

