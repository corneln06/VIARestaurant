package org.store.viarestaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class SessionManager
{
  private static final SessionManager INSTANCE = new SessionManager();

  private final Map<String, LoginAccount> accounts;
  private final List<Table> tables;
  private final Map<Integer, List<OrderLine>> orders;
  private final List<MenuItems> menu;
  private final Map<Integer, Reservation> reservations;
  private final Set<Integer> sentTables;
  private Workers currentUser;
  private int nextReservationId;
  private int nextMenuId;

  private SessionManager()
  {
    this.accounts = new HashMap<>();
    this.tables = new ArrayList<>();
    this.orders = new HashMap<>();
    this.menu = new ArrayList<>();
    this.reservations = new LinkedHashMap<>();
    this.sentTables = new LinkedHashSet<>();
    this.nextReservationId = 1;
    this.nextMenuId = 1;

    seedWorkers();
    seedTables();
    seedMenu();
  }

  public static SessionManager getInstance()
  {
    return INSTANCE;
  }

  public Workers authenticate(String username, String password)
  {
    // TODO: replace with actual backend call
    String key = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    LoginAccount account = accounts.get(key);
    if (account == null || !account.password().equals(password))
    {
      return null;
    }

    currentUser = account.worker();
    return currentUser;
  }

  public void createReservation(String guestName, LocalDateTime dateTime,
      int partySize, int tableId)
  {
    // TODO: replace with actual backend call
    Reservation reservation =
        new Reservation(nextReservationId++, guestName, dateTime, partySize, tableId);
    reservations.put(reservation.id(), reservation);

    Table table = getTableById(tableId);
    if (table != null)
    {
      table.setReserved();
    }
  }

  public void addOrderLine(int tableId, MenuItems item)
  {
    // TODO: replace with actual backend call
    List<OrderLine> lines = orders.computeIfAbsent(tableId, key -> new ArrayList<>());

    for (int i = 0; i < lines.size(); i++)
    {
      OrderLine line = lines.get(i);
      if (line.item().getId() == item.getId())
      {
        lines.set(i, new OrderLine(line.item(), line.qty() + 1));
        markTableSeated(tableId);
        return;
      }
    }

    lines.add(new OrderLine(item, 1));
    markTableSeated(tableId);
  }

  public void removeOrderLine(int tableId, MenuItems item)
  {
    // TODO: replace with actual backend call
    List<OrderLine> lines = orders.get(tableId);
    if (lines == null)
    {
      return;
    }

    for (int i = 0; i < lines.size(); i++)
    {
      OrderLine line = lines.get(i);
      if (line.item().getId() != item.getId())
      {
        continue;
      }

      if (line.qty() > 1)
      {
        lines.set(i, new OrderLine(line.item(), line.qty() - 1));
      }
      else
      {
        lines.remove(i);
      }

      if (lines.isEmpty())
      {
        orders.remove(tableId);
      }
      return;
    }
  }

  public void sendToKitchen(int tableId)
  {
    // TODO: replace with actual backend call
    List<OrderLine> lines = orders.get(tableId);
    if (lines == null || lines.isEmpty())
    {
      return;
    }

    sentTables.add(tableId);
    System.out.println("Sending table " + tableId + " order to kitchen.");
  }

  public void closeTable(int tableId)
  {
    // TODO: replace with actual backend call
    orders.remove(tableId);
    sentTables.remove(tableId);

    Table table = getTableById(tableId);
    if (table != null)
    {
      table.setAvailable();
    }
  }

  public void addMenuItem(String name, double price, MenuTypes type)
  {
    // TODO: replace with actual backend call
    menu.add(new MenuItems(nextMenuId++, name, type, false, new ArrayList<>(), price));
  }

  public double calculateSubtotal(int tableId)
  {
    return orders.getOrDefault(tableId, List.of())
        .stream()
        .mapToDouble(line -> line.qty() * line.item().getPrice())
        .sum();
  }

  public void setTableAvailable(int tableId)
  {
    // TODO: replace with actual backend call
    Table table = getTableById(tableId);
    if (table != null)
    {
      table.setAvailable();
    }
    removeReservationForTable(tableId);
  }

  public void setTableReserved(int tableId)
  {
    // TODO: replace with actual backend call
    Table table = getTableById(tableId);
    if (table != null)
    {
      table.setReserved();
    }
  }

  public void setTableUnavailable(int tableId)
  {
    // TODO: replace with actual backend call
    markTableSeated(tableId);
  }

  public Workers getCurrentUser()
  {
    return currentUser;
  }

  public List<Table> getTables()
  {
    return Collections.unmodifiableList(tables);
  }

  public Table getTableById(int tableId)
  {
    return tables.stream().filter(table -> table.getId() == tableId).findFirst().orElse(null);
  }

  public List<OrderLine> getOrderLines(int tableId)
  {
    return List.copyOf(orders.getOrDefault(tableId, List.of()));
  }

  public List<MenuItems> getMenu()
  {
    return Collections.unmodifiableList(menu);
  }

  public List<Reservation> getReservations()
  {
    return reservations.values()
        .stream()
        .sorted(Comparator.comparing(Reservation::dateTime))
        .toList();
  }

  public List<Reservation> getReservationsForDate(LocalDate date)
  {
    return reservations.values()
        .stream()
        .filter(reservation -> reservation.dateTime().toLocalDate().equals(date))
        .sorted(Comparator.comparing(Reservation::dateTime))
        .toList();
  }

  public Reservation getReservationForTable(int tableId)
  {
    return reservations.values()
        .stream()
        .filter(reservation -> reservation.tableId() == tableId)
        .sorted(Comparator.comparing(Reservation::dateTime))
        .findFirst()
        .orElse(null);
  }

  public Set<Integer> getSentTables()
  {
    return Collections.unmodifiableSet(sentTables);
  }

  private void markTableSeated(int tableId)
  {
    Table table = getTableById(tableId);
    if (table != null)
    {
      table.setSeated();
    }
  }

  private void removeReservationForTable(int tableId)
  {
    reservations.entrySet().removeIf(entry -> entry.getValue().tableId() == tableId);
  }

  private void seedWorkers()
  {
    accounts.put("host", new LoginAccount("host", new Host(1, "Ava", "Madsen")));
    accounts.put("waiter", new LoginAccount("waiter", new Waiter(2, "Leo", "Hansen")));
    accounts.put("manager", new LoginAccount("manager", new Manager(3, "Nora", "Jensen")));
  }

  private void seedTables()
  {
    int[] capacities = {2, 2, 4, 4, 2, 2, 4, 4, 6, 6, 4, 4, 2, 4, 6, 8};
    for (int i = 0; i < capacities.length; i++)
    {
      tables.add(new Table(i + 1, capacities[i], new AvailableState()));
    }
  }

  private void seedMenu()
  {
    menu.add(seedMenuItem("Tomato Bruschetta", MenuTypes.Starter, true, 8.50));
    menu.add(seedMenuItem("Crispy Calamari", MenuTypes.Starter, false, 10.50));
    menu.add(seedMenuItem("Truffle Pasta", MenuTypes.Main, true, 19.00));
    menu.add(seedMenuItem("Grilled Salmon", MenuTypes.Main, false, 24.00));
    menu.add(seedMenuItem("Steak Frites", MenuTypes.Main, false, 28.00));
    menu.add(seedMenuItem("Chocolate Tart", MenuTypes.Dessert, true, 9.00));
    menu.add(seedMenuItem("Lemon Sorbet", MenuTypes.Dessert, true, 7.50));
    menu.add(seedMenuItem("Sparkling Water", MenuTypes.Beverage, true, 4.00));
    menu.add(seedMenuItem("House Lemonade", MenuTypes.Beverage, true, 5.50));
    menu.add(seedMenuItem("Red Wine Glass", MenuTypes.AlcoholicBeverage, true, 8.00));
  }

  private MenuItems seedMenuItem(String name, MenuTypes type, boolean isVegetarian,
      double price)
  {
    return new MenuItems(nextMenuId++, name, type, isVegetarian, new ArrayList<>(), price);
  }

  private record LoginAccount(String password, Workers worker)
  {
  }

  public record OrderLine(MenuItems item, int qty)
  {
  }

  public record Reservation(int id, String guestName, LocalDateTime dateTime, int partySize,
                            int tableId)
  {
  }
}
