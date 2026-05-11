package org.store.viarestaurant.model.state;

public class TableStateFactory {

    public static TableState fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        return switch (value) {
            case "Available" -> new AvailableState();
            case "Reserved" -> new ReservedState();
            case "Seated" -> new SeatedState();
            default -> throw new IllegalArgumentException("Unknown state: " + value);
        };
    }
}
