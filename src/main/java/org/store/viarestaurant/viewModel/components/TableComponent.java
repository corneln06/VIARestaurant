package org.store.viarestaurant.viewModel.components;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javafx.fxml.FXML;
import org.store.viarestaurant.dao.ReservationDAOImpl;
import org.store.viarestaurant.dao.RestaurantTableDAOImpl;
import org.store.viarestaurant.dao.TableOrderDAOImpl;
import org.store.viarestaurant.dao.WorkersDAOImpl;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;
import org.store.viarestaurant.model.entities.TableOrder;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;
import org.store.viarestaurant.model.state.AvailableState;
import org.store.viarestaurant.model.state.SeatedState;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

  private Button tableModalPrimaryButton;
  private ComboBox<String> tableModalWaiterComboBox;
  private final Map<String, Workers> waiterMap = new LinkedHashMap<>();
  private int currentTableId;
  

  // TO BE REMOVED ONCE BACKEND IS IN PLACE
  private WorkersDAOImpl workersDAO;
  private TableOrderDAOImpl tableOrderDAO;
  private RestaurantTableDAOImpl tableDAO;
  // END OF TO BE REMOVED

  public void initGrid(GridPane tableGrid)
  {
    this.tableGrid = tableGrid;
    // TO BE REMOVED ONCE BACKEND IS IN PLACE
    try
    {
      tableDAO = RestaurantTableDAOImpl.getInstance();
      workersDAO = WorkersDAOImpl.getInstance();
      tableOrderDAO = TableOrderDAOImpl.getInstance();
    }catch (SQLException e)
    {
      e.printStackTrace();
    }
    // END OF TO BE REMOVED
    configureTableGrid();
    buildTableGrid();
  }

  public void initModal(
      StackPane overlay,
      Label title,
      Label badge,
      Label info,
      ComboBox<String> waiterComboBox,
      Button primaryButton)
  {
    this.tableModalOverlay = overlay;
    this.tableModalTitle = title;
    this.tableModalStateBadge = badge;
    this.tableModalInfo = info;
    this.tableModalWaiterComboBox = waiterComboBox;
    this.tableModalPrimaryButton = primaryButton;
  }

  public void loadTables(){
    tablesService.loadTables();
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
    refreshTableGrid();
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

    boolean reserved = reservations.stream().anyMatch(r -> r.getTable().getId() == tableId);

    tableModalTitle.setText("Table " + tableId);
    tableModalStateBadge.getStyleClass().removeAll("badge-available", "badge-reserved", "badge-unavailable");

    boolean seated = table.getStatus().getName().equals("Seated");

    if (seated)
    {
      tableModalStateBadge.setText("Seated");
      tableModalStateBadge.getStyleClass().add("badge-unavailable");
      tableModalInfo.setText("Table is currently occupied.");
      tableModalPrimaryButton.setText("Set Available");
      tableModalPrimaryButton.setOnAction(e -> setAvailable());
    }
    else if (reserved)
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
    catch (SQLException e){
      e.printStackTrace();
    }
  }

  public void initClient(Client client)
  {
    this.client = client;

    tablesService = new TablesService(client);
    reservationService = new ReservationService(client);

    tablesService.onTablesLoaded(response ->
    {
      tables = response.getTables();
      buildTableGrid();
      refreshTableGrid();
    });

    reservationService.onReservationLoaded(response ->
    {
      this.reservations = response.getReservations();

      refreshTableGrid();
    });
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

  public void setAvailable()
  {
    try {
      tableDAO.updateTableState(currentTableId, new AvailableState());
      closeTableModal();
      refreshTableGrid();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
