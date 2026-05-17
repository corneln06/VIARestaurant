package org.store.viarestaurant.viewModel.components;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.store.viarestaurant.model.entities.Reservation;
import org.store.viarestaurant.model.entities.RestaurantTable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ReservationScheduleView
{
  private static final LocalTime SERVICE_START = LocalTime.of(17, 0);
  private static final int SLOT_COUNT = 12;
  private static final int SLOT_WIDTH = 80;
  private static final int LABEL_WIDTH = 80;
  private static final int ROW_HEIGHT = 56;

  private final DateTimeFormatter timeFormatter =
          DateTimeFormatter.ofPattern("HH:mm");

  private Consumer<Reservation> onReservationClicked;

  public void setOnReservationClicked(
          Consumer<Reservation> onReservationClicked)
  {
    this.onReservationClicked = onReservationClicked;
  }

  public void draw(
      GridPane scheduleGrid,
      Pane scheduleOverlayPane,
      List<RestaurantTable> tables,
      List<Reservation> reservations)
  {
    if(scheduleGrid == null || scheduleOverlayPane == null)
    {
      return;
    }

    clear(scheduleGrid, scheduleOverlayPane);
    setupColumns(scheduleGrid);
    setupRows(scheduleGrid, tables);
    drawHeaders(scheduleGrid);
    Map<Integer, Integer> tableRows = drawTableRows(scheduleGrid, tables);
    drawReservations(scheduleGrid, reservations, tableRows);
    setupSize(scheduleGrid, scheduleOverlayPane, tables.size());
    drawNowLine(scheduleOverlayPane, tables.size());
  }

  private void clear(GridPane grid, Pane overlay)
  {
    grid.getChildren().clear();
    grid.getColumnConstraints().clear();
    grid.getRowConstraints().clear();
    overlay.getChildren().clear();
  }

  private void setupColumns(GridPane grid)
  {
    ColumnConstraints labelCol = new ColumnConstraints();
    labelCol.setPrefWidth(LABEL_WIDTH);
    grid.getColumnConstraints().add(labelCol);

    for(int s = 0; s < SLOT_COUNT; s++)
    {
      ColumnConstraints slotCol = new ColumnConstraints();
      slotCol.setPrefWidth(SLOT_WIDTH);
      grid.getColumnConstraints().add(slotCol);
    }
  }

  private void setupRows(GridPane grid, List<RestaurantTable> tables)
  {
    RowConstraints headerRow = new RowConstraints();
    headerRow.setPrefHeight(40);
    grid.getRowConstraints().add(headerRow);

    for(RestaurantTable ignored : tables)
    {
      RowConstraints row = new RowConstraints();
      row.setPrefHeight(ROW_HEIGHT);
      grid.getRowConstraints().add(row);
    }
  }

  private void drawHeaders(GridPane grid)
  {
    Label corner = new Label("Table");
    corner.getStyleClass().add("gantt-header");
    grid.add(corner, 0, 0);

    for(int s = 0; s < SLOT_COUNT; s++)
    {
      Label timeLabel =
          new Label(SERVICE_START.plusMinutes(s * 30L).format(timeFormatter));

      timeLabel.getStyleClass().add("gantt-header");
      GridPane.setHalignment(timeLabel, HPos.CENTER);

      grid.add(timeLabel, s + 1, 0);
    }
  }

  private Map<Integer, Integer> drawTableRows(
      GridPane grid,
      List<RestaurantTable> tables)
  {
    Map<Integer, Integer> tableRows = new LinkedHashMap<>();

    int rowIndex = 1;

    for(RestaurantTable table : tables)
    {
      tableRows.put(table.getId(), rowIndex);

      Label label = new Label("T" + table.getId());
      label.getStyleClass().add("gantt-table-label");

      grid.add(label, 0, rowIndex);

      rowIndex++;
    }

    return tableRows;
  }

  private void drawReservations(
      GridPane grid,
      List<Reservation> reservations,
      Map<Integer, Integer> tableRows)
  {
    for(Reservation reservation : reservations)
    {
      if(reservation.getTable() == null)
      {
        continue;
      }

      Integer row =
          tableRows.get(reservation.getTable().getId());

      if(row == null)
      {
        continue;
      }

      int minutesFromStart =
          reservation.getDateTime().getHour() * 60
              + reservation.getDateTime().getMinute()
              - SERVICE_START.getHour() * 60;

      int startCol =
          Math.max(0, Math.min(SLOT_COUNT - 1, minutesFromStart / 30));

      Label block =
          new Label(
              reservation.getName()
                  + " • "
                  + reservation.getPartySize()
                  + " guests"
          );

      block.getStyleClass().add("reservation-block");
      block.setMaxWidth(Double.MAX_VALUE);
      block.setOnMouseClicked(e ->
      {
        if(onReservationClicked != null)
        {
          onReservationClicked.accept(reservation);
        }
      });

      grid.add(block, startCol + 1, row);
      GridPane.setColumnSpan(block, 2);
    }
  }

  private void setupSize(GridPane grid, Pane overlay, int tableCount)
  {
    double totalWidth =
        LABEL_WIDTH + SLOT_COUNT * SLOT_WIDTH;

    double totalHeight =
        40 + tableCount * ROW_HEIGHT;

    grid.setPrefSize(totalWidth, totalHeight);
    overlay.setPrefSize(totalWidth, totalHeight);
  }

  private void drawNowLine(Pane overlay, int tableCount)
  {
    long minutesNow =
        LocalTime.now().getHour() * 60L
            + LocalTime.now().getMinute();

    long minutesStart =
        SERVICE_START.getHour() * 60L;

    long minutesEnd =
        LocalTime.of(23, 0).getHour() * 60L;

    if(minutesNow < minutesStart || minutesNow > minutesEnd)
    {
      return;
    }

    double totalHeight =
        40 + tableCount * ROW_HEIGHT;

    double offset =
        (minutesNow - minutesStart) / 30.0 * SLOT_WIDTH;

    Rectangle nowLine = new Rectangle(2, totalHeight);
    nowLine.setFill(Color.RED);
    nowLine.setLayoutX(LABEL_WIDTH + offset);
    nowLine.setLayoutY(0);

    overlay.getChildren().add(nowLine);
  }
}