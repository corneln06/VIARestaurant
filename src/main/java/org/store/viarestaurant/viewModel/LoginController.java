package org.store.viarestaurant.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.store.viarestaurant.model.entities.Workers;
import org.store.viarestaurant.server.Client;
import org.store.viarestaurant.server.dto.LoginRequest;
import org.store.viarestaurant.server.dto.LoginResponse;
import org.store.viarestaurant.view.HelloApplication;

import java.io.IOException;

public class LoginController extends NavigationController
{
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private Client client;

    @FXML
    public void initialize()
    {
        try
        {
            client = new Client();
            client.connect();

            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            showError("Cannot connect to server.");
        }
    }

    @FXML
    private void handleLogin()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try
        {
            LoginRequest request =
                new LoginRequest(username, password);

            client.send(request);

            LoginResponse response =
                (LoginResponse) client.receive();

            if(!response.isSuccess())
            {
                showError("Invalid credentials.");
                return;
            }
            client.startListening();

            Workers worker =
                response.getRole();

            Parent root =
                loadDashboard(worker);

            Stage stage =
                (Stage) usernameField
                    .getScene()
                    .getWindow();

            stage.getScene().setRoot(root);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            showError("Login failed.");
        }
    }

    private Parent loadDashboard(Workers worker)
        throws IOException
    {
        String fxml = switch(worker.getRole())
        {
            case Host ->
                "/org/store/viarestaurant/HostDashboard.fxml";

            case Waiter ->
                "/org/store/viarestaurant/WaiterDashboard.fxml";

            case Manager ->
                "/org/store/viarestaurant/ManagerDashboard.fxml";
        };

        FXMLLoader loader =
            new FXMLLoader(
                HelloApplication.class.getResource(fxml));

        Parent root = loader.load();

        NavigationController controller =
            loader.getController();

        controller.initClient(client);
        controller.initData(worker);


        return root;
    }

    private void showError(String message)
    {
        errorLabel.setText(message);

        errorLabel.setVisible(true);
        errorLabel.setManaged(true);

        passwordField.clear();
    }
}