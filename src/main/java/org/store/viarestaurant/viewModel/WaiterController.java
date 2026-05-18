package org.store.viarestaurant.viewModel;


import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.components.TableComponent;
import org.store.viarestaurant.viewModel.services.TablesService;

public class WaiterController {

    private final TableComponent tableComponent = new TableComponent();
    public void init(GridPane tableGrid)
    {
        tableComponent.initGrid(tableGrid);
    }

    public void initClient(Client client)
    {
        tableComponent.initClient(client);

    }

    public void refreshTableGrid(){
        tableComponent.refreshTableGrid();
    }

    public void loadtables() {tableComponent.loadTables();}
}
