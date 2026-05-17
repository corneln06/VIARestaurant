package org.store.viarestaurant.viewModel.components;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javafx.fxml.FXML;
import org.store.viarestaurant.dao.ReservationDAOImpl;
import org.store.viarestaurant.dao.RestaurantTableDAOImpl;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.server.Client;
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

  private ReservationService reservationService;
  private TablesService tablesService;
  private ArrayList<RestaurantTable> tables = new ArrayList<>();
  private List<Reservation> reservations = new ArrayList<>();

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
      Label info)
  {
    this.tableModalOverlay = overlay;
    this.tableModalTitle = title;
    this.tableModalStateBadge = badge;
    this.tableModalInfo = info;
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
        btn.setText("Table " + table.getId());
        updateTableButton(btn, reservedIds.contains(table.getId()));
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
      btn.setOnAction(e -> openTableModal(id));
      tableGrid.add(btn, i % 5, i / 5);
      tableButtonMap.put(id, btn);
      updateTableButton(btn, false);
    }
    refreshTableGrid();
  }

  private void updateTableButton(Button btn, boolean reserved)
  {
    btn.getStyleClass().removeAll("table-available", "table-reserved");
    btn.getStyleClass().add(reserved ? "table-reserved" : "table-available");
  }

  private void openTableModal(int tableId)
  {
    if (tableModalOverlay == null) return;
    RestaurantTable table = tables.stream()
        .filter(t -> t.getId() == tableId).findFirst().orElse(null);
    if (table == null) return;

    boolean reserved = reservations.stream().anyMatch(r -> r.getTable().getId() == tableId);

    tableModalTitle.setText("Table " + tableId);
    tableModalStateBadge.getStyleClass().removeAll("badge-available", "badge-reserved");
    if (reserved)
    {
      tableModalStateBadge.setText("Reserved");
      tableModalStateBadge.getStyleClass().add("badge-reserved");
      tableModalInfo.setText("This table has a reservation today.");
    }
    else
    {
      tableModalStateBadge.setText("Available");
      tableModalStateBadge.getStyleClass().add("badge-available");
      tableModalInfo.setText("Ready for walk-ins and upcoming service.");
    }
    tableModalOverlay.setVisible(true);
    tableModalOverlay.setManaged(true);
  }
  public void initClient(Client client)
  {
    this.client = client;

    tablesService = new TablesService(client);
    reservationService = new ReservationService(client);

    tablesService.onTablesLoaded(response ->
    {
      this.tables = response.getTables();

      buildTableGrid();
    });

    reservationService.onReservationLoaded(response ->
    {
      this.reservations = response.getReservations();

      refreshTableGrid();
    });
  }
}
