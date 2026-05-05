package org.store.viarestaurant.model.state;

public class TableStateFactory {

    public static TableState fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        switch (value.toUpperCase()) {
            case "AVAILABLE":
                return new AvailableState();
            case "RESERVED":
                return new ReservedState();
            case "OCCUPIED":
                return new SeatedState();
            default:
                throw new IllegalArgumentException("Unknown state: " + value);
        }
    }
}
