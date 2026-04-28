module org.store.viarestaurant {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.sql;

  exports org.store.viarestaurant.model.state;
  opens org.store.viarestaurant.model.state to javafx.fxml;
  exports org.store.viarestaurant.view;
  opens org.store.viarestaurant.view to javafx.fxml;
  exports org.store.viarestaurant.viewModel;
  opens org.store.viarestaurant.viewModel to javafx.fxml;
  exports org.store.viarestaurant.model.entities;
  opens org.store.viarestaurant.model.entities to javafx.fxml;
  exports org.store.viarestaurant.model.enums;
  opens org.store.viarestaurant.model.enums to javafx.fxml;
}