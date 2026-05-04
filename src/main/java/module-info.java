module org.store.viarestaurant {
  requires javafx.controls;
  requires javafx.fxml;

  opens org.store.viarestaurant to javafx.fxml;
  exports org.store.viarestaurant;
}