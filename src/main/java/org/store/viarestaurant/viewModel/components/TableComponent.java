package org.store.viarestaurant.viewModel.components;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.store.viarestaurant.dao.ReservationDAOImpl;
import org.store.viarestaurant.dao.RestaurantTableDAOImpl;
import org.store.viarestaurant.dao.TableOrderDAOImpl;
import org.store.viarestaurant.dao.WorkersDAOImpl;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;
import org.store.viarestaurant.model.state.SeatedState;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class TableComponent
{
  private GridPane tableGrid;
  private final Map<Integer, Button> tableButtonMap = new HashMap<>();

  private StackPane tableModalOverlay;
  private Label tableModalTitle;
  private Label tableModalStateBadge;
  private Label tableModalInfo;

  private ReservationDAOImpl reservationDAO;
  private RestaurantTableDAOImpl tableDAO;

  private ComboBox<String> tableModalWaiterComboBox;
  private final Map<String, Workers> waiterMap = new LinkedHashMap<>();
  private int currentTableId;
  private WorkersDAOImpl workersDAO;
  private TableOrderDAOImpl tableOrderDAO;


  public void initGrid(GridPane tableGrid)
  {
    this.tableGrid = tableGrid;
    try
    {
      reservationDAO = ReservationDAOImpl.getInstance();
      tableDAO = RestaurantTableDAOImpl.getInstance();
      workersDAO = WorkersDAOImpl.getInstance();
      tableOrderDAO = TableOrderDAOImpl.getInstance();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    configureTableGrid();
    buildTableGrid();
  }

  public void initModal(
      StackPane overlay,
      Label title,
      Label badge,
      Label info,
      ComboBox<String> waiterComboBox)
  {
    this.tableModalOverlay = overlay;
    this.tableModalTitle = title;
    this.tableModalStateBadge = badge;
    this.tableModalInfo = info;
    this.tableModalWaiterComboBox = waiterComboBox;
  }

  public void refreshTableGrid()
  {
    if (tableGrid == null) return;
    try
    {
      List<RestaurantTable> tables = tableDAO.getAllRestaurantTables();
      Set<Integer> reservedIds = new HashSet<>();
      for (Reservation r : reservationDAO.getAllReservationsForToday())
      {
        reservedIds.add(r.getTable().getId());
      }
      for (RestaurantTable table : tables)
      {
        Button btn = tableButtonMap.get(table.getId());
        if (btn != null)
        {
          btn.setText("Table " + table.getId());
          boolean reserved = reservedIds.contains(table.getId());
          boolean seated = table.getStatus().getName().equals("Seated");
          updateTableButton(btn, reserved, seated);
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
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
    try
    {
      List<RestaurantTable> tables = tableDAO.getAllRestaurantTables();
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
          try {
            openTableModal(id);
          } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        });
        tableGrid.add(btn, i % 5, i / 5);
        tableButtonMap.put(id, btn);
        updateTableButton(btn, false, false);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  private void updateTableButton(Button btn, boolean reserved, boolean seated)
  {
    btn.getStyleClass().removeAll("table-available", "table-reserved", "table-unavailable");
    if (seated) btn.getStyleClass().add("table-unavailable");
    else if (reserved) btn.getStyleClass().add("table-reserved");
    else btn.getStyleClass().add("table-available");
  }

  private void openTableModal(int tableId) throws SQLException
  {
    currentTableId = tableId;
    //populate waiter combo boc
    waiterMap.clear();
    tableModalWaiterComboBox.getItems().clear();
    List<Workers> allWorkers = workersDAO.getAllWorkers();
    for(Workers w : allWorkers) {
      if(w.getRole() == WorkerRole.Waiter){
        String label = w.getFirstName() + " " + w.getLastName();
        waiterMap.put(label, w);
        tableModalWaiterComboBox.getItems().add(label);


      }
    }
    tableModalWaiterComboBox.getSelectionModel().clearSelection();
    List<TableOrder> activeOrders = tableOrderDAO.getTableOrdersByTableId(tableId);
    activeOrders.stream()
        .filter(o -> !o.isPaid() && o.getWaiter() != null)
        .findFirst()
        .ifPresent(o -> {
          String label = o.getWaiter().getFirstName() + " " + o.getWaiter().getLastName();
          tableModalWaiterComboBox.setValue(label);
        });
    
    if (tableModalOverlay == null) return;
    try
    {
      List<RestaurantTable> tables = tableDAO.getAllRestaurantTables();
      RestaurantTable table = tables.stream()
          .filter(t -> t.getId() == tableId).findFirst().orElse(null);
      if (table == null) return;

      boolean reserved = reservationDAO.getAllReservationsForToday()
          .stream().anyMatch(r -> r.getTable().getId() == tableId);

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
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  //seating table function
  public void seatTable(){
    Workers waiter = waiterMap.get(tableModalWaiterComboBox.getValue());
    if(waiter == null){
      //if waiter not selected, no function
      return;
    }
    try {
        tableOrderDAO.createTableOrder(currentTableId, waiter.getId(), "", 0.0,
        new ArrayList<>(), false);
        tableDAO.updateTableState(currentTableId, new SeatedState());
        closeTableModal();
        refreshTableGrid();
    } catch (SQLException e) {
        e.printStackTrace();
    }
  }
}
