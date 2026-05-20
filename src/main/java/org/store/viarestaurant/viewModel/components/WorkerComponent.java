package org.store.viarestaurant.viewModel.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.model.enums.WorkerRole;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.viewModel.services.WorkersService;

import java.io.IOException;

public class WorkerComponent
{
  private StackPane newWorkerOverlay;
  private TextField workerFirstNameField;
  private TextField workerLastNameField;
  private ComboBox<String> workerRoleComboBox;
  private TextField workerEmailField;
  private PasswordField workerPasswordField;
  private Label newWorkerErrorLabel;
  private TableView<Workers> workersTable;
  private Button submitWorkerButton;
  private Button deleteWorkerButton;
  private Label workerModalTitle;

  private WorkersService workersService;
  private boolean isEditMode = false;
  private Workers editingWorker;

  public void initModal(
      StackPane overlay,
      TextField firstNameField,
      TextField lastNameField,
      ComboBox<String> roleComboBox,
      TextField emailField,
      PasswordField passwordField,
      Label errorLabel,
      TableView<Workers> table,
      Button submitButton,
      Button deleteButton,
      Label modalTitle
  )
  {
    this.newWorkerOverlay = overlay;
    this.workerFirstNameField = firstNameField;
    this.workerLastNameField = lastNameField;
    this.workerRoleComboBox = roleComboBox;
    this.workerEmailField = emailField;
    this.workerPasswordField = passwordField;
    this.newWorkerErrorLabel = errorLabel;
    this.workersTable = table;
    this.submitWorkerButton = submitButton;
    this.deleteWorkerButton = deleteButton;
    this.workerModalTitle = modalTitle;

    workerRoleComboBox.getItems().setAll("Manager", "Host", "Waiter");

    workerRoleComboBox.setButtonCell(new ListCell<>()
    {
      @Override
      protected void updateItem(String item, boolean empty)
      {
        super.updateItem(item, empty);
        if (item == null || empty)
        {
          setText("Role");
          setStyle("-fx-text-fill: #9ea8c0;");
        }
        else
        {
          setText(item);
          setStyle("-fx-text-fill: #1d2440;");
        }
      }
    });

    configureWorkerColumns();
    initWorkerRowDoubleClick();
  }

  public void initClient(Client client)
  {
    this.workersService = new WorkersService(client);

    this.workersService.onWorkersLoaded(response ->
        workersTable.getItems().setAll(response.getWorkers())
    );

    this.workersService.onWorkerCreated(response ->
    {
      try { refreshWorkersTable(); }
      catch (IOException e) { e.printStackTrace(); }
    });

    this.workersService.onWorkerDeleted(response ->
    {
      try { refreshWorkersTable(); }
      catch (IOException e) { e.printStackTrace(); }
    });

    this.workersService.onWorkerUpdated(response ->
    {
      try { refreshWorkersTable(); }
      catch (IOException e) { e.printStackTrace(); }
    });
  }

  private void initWorkerRowDoubleClick()
  {
    workersTable.setRowFactory(tv ->
    {
      TableRow<Workers> row = new TableRow<>();
      row.setOnMouseClicked(event ->
      {
        if (event.getClickCount() == 2 && !row.isEmpty())
        {
          openEditWorkerModal(row.getItem());
        }
      });
      return row;
    });
  }

  public void openNewWorkerModal()
  {
    setEditMode(false);

    workerFirstNameField.clear();
    workerLastNameField.clear();
    workerEmailField.clear();
    workerPasswordField.clear();
    workerRoleComboBox.getSelectionModel().clearSelection();

    hideError();
    showOverlay(newWorkerOverlay);
  }

  private void openEditWorkerModal(Workers worker)
  {
    setEditMode(true);
    editingWorker = worker;

    workerFirstNameField.setText(worker.getFirstName());
    workerLastNameField.setText(worker.getLastName());
    workerEmailField.setText(worker.getEmail());
    workerRoleComboBox.setValue(worker.getRole().toString());
    workerPasswordField.clear();

    hideError();
    showOverlay(newWorkerOverlay);
  }

  public void closeNewWorkerModal()
  {
    hideOverlay(newWorkerOverlay);
  }


  public void createWorker()
  {
    hideError();

    String firstName = workerFirstNameField.getText() == null ? "" : workerFirstNameField.getText().trim();
    if (firstName.isBlank()) { showError("First name is required."); return; }

    String lastName = workerLastNameField.getText() == null ? "" : workerLastNameField.getText().trim();
    if (lastName.isBlank()) { showError("Last name is required."); return; }

    String roleStr = workerRoleComboBox.getValue();
    if (roleStr == null) { showError("Please select a role."); return; }

    String email = workerEmailField.getText() == null ? "" : workerEmailField.getText().trim();
    if (email.isBlank()) { showError("Email is required."); return; }

    String emailRegex = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
    if (!email.matches(emailRegex))
    {
      showError("Please enter a valid email address.");
      return;
    }

    boolean duplicate = workersTable.getItems().stream()
        .anyMatch(w -> w.getEmail().equalsIgnoreCase(email)
            && (editingWorker == null || w.getId().intValue() != editingWorker.getId().intValue()));
    if (duplicate)
    {
      showError("This email is already registered.");
      return;
    }

    WorkerRole role = WorkerRole.valueOf(roleStr);

    if (isEditMode)
    {
      try
      {
        workersService.updateWorker(
            editingWorker.getId(),
            firstName,
            lastName,
            email,
            role
        );
        closeNewWorkerModal();
      }
      catch (IOException e)
      {
        showError("Connection error: " + e.getMessage());
      }
      return;
    }
    String password = workerPasswordField.getText() == null ? "" : workerPasswordField.getText().trim();
    if (password.isBlank()) { showError("Password is required."); return; }

    if (password.length() < 8)
    {
      showError("Password must be at least 8 characters.");
      return;
    }

    try
    {
      workersService.createWorker(firstName, lastName, email, password, role);
      closeNewWorkerModal();
    }
    catch (IOException e)
    {
      showError("Connection error: " + e.getMessage());
    }
  }

  public void deleteWorker()
  {
    if (editingWorker == null)
    {
      showError("No worker selected.");
      return;
    }

    try
    {
      workersService.deleteWorker(editingWorker.getId());
      closeNewWorkerModal();
    }
    catch (IOException e)
    {
      showError("Connection error: " + e.getMessage());
    }
  }


  public void refreshWorkersTable() throws IOException
  {
    configureWorkerColumns();
    workersService.loadWorkers();
  }

  @SuppressWarnings("unchecked")
  private void configureWorkerColumns()
  {
    TableColumn<Workers, String> firstNameCol =
        (TableColumn<Workers, String>) workersTable.getColumns().get(0);
    TableColumn<Workers, String> lastNameCol =
        (TableColumn<Workers, String>) workersTable.getColumns().get(1);
    TableColumn<Workers, String> roleCol =
        (TableColumn<Workers, String>) workersTable.getColumns().get(2);
    TableColumn<Workers, String> emailCol =
        (TableColumn<Workers, String>) workersTable.getColumns().get(3);
    TableColumn<Workers, String> passwordCol =
        (TableColumn<Workers, String>) workersTable.getColumns().get(4);


    firstNameCol.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getFirstName()));

    lastNameCol.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getLastName()));

    roleCol.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getRole().toString()));

    emailCol.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getEmail()));

    passwordCol.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getPassword()));
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

  private void setEditMode(boolean edit)
  {
    isEditMode = edit;
    workerModalTitle.setText(edit ? "Edit Worker" : "New Worker");
    submitWorkerButton.setText(edit ? "Apply" : "Create");
    deleteWorkerButton.setVisible(edit);
    deleteWorkerButton.setManaged(edit);
    workerPasswordField.setVisible(!edit);
    workerPasswordField.setManaged(!edit);
  }


  private void showError(String message)
  {
    newWorkerErrorLabel.setText(message);
    newWorkerErrorLabel.setVisible(true);
    newWorkerErrorLabel.setManaged(true);
  }

  private void hideError()
  {
    newWorkerErrorLabel.setVisible(false);
    newWorkerErrorLabel.setManaged(false);
  }
}