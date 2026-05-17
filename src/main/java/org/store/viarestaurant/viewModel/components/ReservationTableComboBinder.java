package org.store.viarestaurant.viewModel.components;

import javafx.scene.control.ComboBox;
import org.store.viarestaurant.model.entities.RestaurantTable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReservationTableComboBinder
{
  private final Map<String, RestaurantTable> tableMap =
      new LinkedHashMap<>();

  public void populate(
      ComboBox<String> comboBox,
      List<RestaurantTable> tables)
  {
    tableMap.clear();
    comboBox.getItems().clear();

    for(RestaurantTable table : tables)
    {
      String label =
          "Table "
              + table.getId()
              + " • "
              + table.getMaxSitting()
              + " seats";

      tableMap.put(label, table);
      comboBox.getItems().add(label);
    }
  }

  public RestaurantTable getSelectedTable(
      ComboBox<String> comboBox)
  {
    return tableMap.get(comboBox.getValue());
  }
  public String getLabelForTable(RestaurantTable table)
  {
    return tableMap.entrySet()
            .stream()
            .filter(entry ->
                    Objects.equals(entry.getValue().getId(), table.getId())
            )
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
  }
}
