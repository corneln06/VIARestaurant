package org.store.viarestaurant.viewModel;

import javafx.scene.layout.GridPane;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.TableComponent;

public class WaiterController {

    private final TableComponent tableComponent = new TableComponent();
    private TableComponent ordersTableComponent;

    public void init(GridPane tableGrid, GridPane ordersTableGrid) {
        tableComponent.initGrid(tableGrid);

        if (ordersTableGrid != null) {
            ordersTableComponent = new TableComponent();
            ordersTableComponent.setLabelSuffix(" - Order");
            ordersTableComponent.initGrid(ordersTableGrid);
        }
    }

    public void initClient(Client client)
    {
        tableComponent.initClient(client);

        if (ordersTableComponent != null) {
            ordersTableComponent.initClient(client);
        }
    }

    public void refreshTableGrid(){
        tableComponent.refreshTableGrid();

        if (ordersTableComponent != null) {
            ordersTableComponent.refreshTableGrid();
        }
    }

    public void loadtables() {
        tableComponent.loadTables();

        if (ordersTableComponent != null) {
            ordersTableComponent.loadTables();
        }
    }
}