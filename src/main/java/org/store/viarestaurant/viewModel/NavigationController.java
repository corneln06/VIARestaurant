package org.store.viarestaurant.viewModel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.view.HelloApplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.MenuItems;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.view.HelloApplication;

public class NavigationController
{

    public Label reservationsDateLabel;
    @FXML private Label sidebarName;
    @FXML private Label sidebarRole;

    @FXML private Button btnTables;
    @FXML private Button btnReservations;
    @FXML private Button btnWorkers;
    @FXML private Button btnOrders;
    @FXML private Button btnBills;
    @FXML private Button btnMenu;

    @FXML private Button logOutButton;
    @FXML private AnchorPane tablesPage;
    @FXML private AnchorPane reservationsPage;
    @FXML private AnchorPane ordersListPage;
    @FXML private AnchorPane ordersDetailPage;
    @FXML private AnchorPane billsListPage;
    @FXML private AnchorPane billDetailPage;
    @FXML private AnchorPane menuPage;

    @FXML private GridPane reservationGrid;
    @FXML private Pane reservationOverlayPane;

    @FXML private GridPane tableGrid;
    @FXML private StackPane tableModalOverlay;
    @FXML private Label tableModalTitle;
    @FXML private Label tableModalStateBadge;
    @FXML private Label tableModalInfo;

    @FXML private StackPane newReservationOverlay;
    @FXML private TextField guestNameField;
    @FXML private DatePicker reservationDatePicker;
    @FXML private TextField reservationTimeField;
    @FXML private TextField partySizeField;
    @FXML private ComboBox<String> tableComboBox;
    @FXML private Label newReservationErrorLabel;

    @FXML private StackPane newDishOverlay;
    @FXML private TextField dishNameField;
    @FXML private CheckBox isVegetarianCheckBox;
    @FXML private ListView<String> allergiesListView;
    @FXML private TextField dishPriceField;
    @FXML private ComboBox<String> dishTypeComboBox;
    @FXML private Label newDishErrorLabel;
    @FXML private TableView<MenuItems> menuTable;

    protected Client client;

    @FXML private Button submitReservationButton;
    @FXML private Button deleteReservationButton;

    @FXML private Label modalTitle;


    private HostController hostController;
    private ManagerController managerController;
    private WaiterController waiterController;

    public void initData(Workers worker)
    {
        if (sidebarName != null)
        {
            sidebarName.setText(worker.getFirstName() + " " + worker.getLastName());
        }

        if (sidebarRole != null)
        {
            sidebarRole.setText(worker.getRole().toString());
        }
        showDefaultPage(worker);
        if(hostController != null){
            reservationsDateLabel.setText(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        }
    }
    public void initClient(Client client)
    {
        this.client = client;
    }
    private void showDefaultPage(Workers worker)
    {
        switch (worker.getRole())
        {
            case Host -> {
                hostController = new HostController();

                hostController.initClient(client);

                hostController.init(reservationGrid, reservationOverlayPane, tableGrid);

                hostController.initReservationModal(
                        newReservationOverlay,
                        guestNameField,
                        reservationDatePicker,
                        reservationTimeField,
                        partySizeField,
                        tableComboBox,
                        newReservationErrorLabel,
                        submitReservationButton,
                        deleteReservationButton,
                        modalTitle

                );

                hostController.initTableModal(tableModalOverlay, tableModalTitle, tableModalStateBadge, tableModalInfo);

                hostController.refreshSchedule();
                showTablesPage();
            }
            case Waiter -> {
                waiterController = new WaiterController();
                waiterController.initClient(client);
                waiterController.init(tableGrid);

                showTablesPage();
                showWaiterTablesPage();
            }
            case Manager -> {
                managerController = new ManagerController();

                managerController.initClient(client);

                managerController.init(
                        reservationGrid,
                        reservationOverlayPane, tableGrid
                );

                managerController.initModal(
                        //Reservation Modal props
                        newReservationOverlay,
                        guestNameField,
                        reservationDatePicker,
                        reservationTimeField,
                        partySizeField,
                        tableComboBox,
                        newReservationErrorLabel,
                        // Menu Modal props
                        newDishOverlay,
                        dishNameField,
                        dishTypeComboBox,
                        dishPriceField,
                        isVegetarianCheckBox,
                        allergiesListView,
                        newDishErrorLabel,
                        menuTable,
                        submitReservationButton,
                        deleteReservationButton,
                        modalTitle
                );

                managerController.refreshMenuTable();

                managerController.initTableModal(tableModalOverlay, tableModalTitle, tableModalStateBadge, tableModalInfo);

                showTablesPage();
                showMenuPage();
            }
        }
    }

    @FXML
    private void showTablesPage()
    {
        showOnly(tablesPage);
        setActive(btnTables, btnReservations, btnWorkers, btnMenu, btnBills, btnOrders);
        if (hostController != null) hostController.refreshTableGrid();
        if (managerController != null) {managerController.refreshTableGrid(); managerController.loadTables();}
        if (waiterController != null) {waiterController.refreshTableGrid(); waiterController.loadtables();}
    }

    @FXML
    private void showReservationsPage()
    {
        showOnly(reservationsPage);
        setActive(btnReservations, btnTables, btnWorkers, btnMenu);

        if(hostController != null)
        {
            hostController.closeNewReservationModal();
            hostController.refreshSchedule();
        }
        else if(managerController != null)
        {
            managerController.refreshSchedule();
        }
    }
    @FXML
    private void showWorkersPage()
    {
//        showOnly(reservationsPage);
        setActive(btnWorkers, btnMenu, btnReservations, btnTables);

//        if(hostController != null)
//        {
//            hostController.closeNewReservationModal();
//            hostController.refreshSchedule();
//        }
//        else if(managerController != null)
//        {
//            managerController.refreshSchedule();
//        }
    }

    @FXML
    private void showWaiterTablesPage()
    {
        showOnly(tablesPage);
        setActive(btnTables, btnOrders, btnBills);
    }

    @FXML
    private void showOrdersPage()
    {
        showOnly(ordersListPage);
        setActive(btnOrders, btnBills, btnTables);
    }

    @FXML
    private void showBillsPage()
    {
        showOnly(billsListPage);
        setActive(btnBills, btnTables, btnOrders);
    }

    @FXML
    private void showOrderDetailPage()
    {
        showOnly(ordersDetailPage);
        setActive(btnOrders, btnTables, btnBills);
    }

    @FXML
    private void showBillDetailPage()
    {
        showOnly(billDetailPage);
        setActive(btnBills, btnTables, btnOrders);
    }
    @FXML
    private void handleLogOut() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/org/store/viarestaurant/LoginView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);

        scene.getStylesheets().add(
                getClass()
                        .getResource("/org/store/viarestaurant/Stylesheet.css")
                        .toExternalForm()
        );
        Stage stage = (Stage) logOutButton.getScene().getWindow();

        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void showMenuPage()
    {
        showOnly(menuPage);
        setActive(btnMenu, btnReservations, btnTables, btnWorkers);

//        if(hostController != null)
//        {
//            hostController.closeNewReservationModal();
//            hostController.refreshSchedule();
//        }
//        else if(managerController != null)
//        {
//            managerController.refreshSchedule();
//        }
    }

    @FXML
    private void backToOrdersList()
    {
        showOrdersPage();
    }

    @FXML
    private void backToBillsList()
    {
        showBillsPage();
    }
    @FXML
    private void openNewReservationModal()
    {
        if(hostController != null) hostController.openNewReservationModal();
        else if(managerController != null) managerController.openNewReservationModal();
    }

    @FXML
    private void closeNewReservationModal(MouseEvent event)
    {
        if(hostController != null) hostController.closeNewReservationModal();
        else if(managerController != null) managerController.closeNewReservationModal();
    }

    @FXML
    private void closeNewReservationModalAction()
    {
        if(hostController != null) hostController.closeNewReservationModal();
        else if(managerController != null) managerController.closeNewReservationModal();
    }

    @FXML
    private void createReservation()
    {
        if(hostController != null) hostController.createReservation();
        else if(managerController != null) managerController.createReservation();
    }

    @FXML
    private void closeTableModal(MouseEvent event)
    {
        if (hostController != null) hostController.closeTableModal();
        if (managerController != null) managerController.closeTableModal();
    }

    @FXML
    private void closeTableModalAction()
    {
        if (hostController != null) hostController.closeTableModal();
        if (managerController != null) managerController.closeTableModal();
    }

    @FXML
    private void consumeModalClick(MouseEvent event)
    {
        event.consume();
    }

    private void showOnly(AnchorPane selectedPage)
    {
        setVisible(tablesPage, selectedPage == tablesPage);
        setVisible(reservationsPage, selectedPage == reservationsPage);
        setVisible(ordersListPage, selectedPage == ordersListPage);
        setVisible(ordersDetailPage, selectedPage == ordersDetailPage);
        setVisible(billsListPage, selectedPage == billsListPage);
        setVisible(billDetailPage, selectedPage == billDetailPage);
        setVisible(menuPage, selectedPage == menuPage);
    }

    private void setVisible(AnchorPane page, boolean visible)
    {
        if (page == null)
        {
            return;
        }

        page.setVisible(visible);
        page.setManaged(visible);
    }

    private void setActive(Button activeButton, Button... inactiveButtons)
    {
        if (activeButton != null && !activeButton.getStyleClass().contains("nav-btn-active"))
        {
            activeButton.getStyleClass().add("nav-btn-active");
        }

        for (Button button : inactiveButtons)
        {
            if (button != null)
            {
                button.getStyleClass().remove("nav-btn-active");
            }
        }
    }
    @FXML
    private void deleteReservation()
    {
        if (hostController != null)
        {
            hostController.deleteReservation();
        }
        if (managerController != null)
        {
            managerController.deleteReservation();
        }
    }
    @FXML
    private void openNewDishModal() {
        if (managerController != null) managerController.openNewDishModal();
    }

    @FXML
    private void closeNewDishModal(MouseEvent event) {
        if (managerController != null) managerController.closeNewDishModal();
    }

    @FXML
    private void closeNewDishModalAction() {
        if (managerController != null) managerController.closeNewDishModal();
    }

    @FXML
    private void createDish() {
        if (managerController != null) managerController.createDish();
    }
}