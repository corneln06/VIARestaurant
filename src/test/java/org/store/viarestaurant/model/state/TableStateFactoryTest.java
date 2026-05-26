package org.store.viarestaurant.model.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TableStateFactoryTest {

    @Test // I
    public void fromString_available_returnsAvailableState() {
        assertInstanceOf(AvailableState.class, TableStateFactory.fromString("Available"));
    }

    @Test // I
    public void fromString_reserved_returnsReservedState() {
        assertInstanceOf(ReservedState.class, TableStateFactory.fromString("Reserved"));
    }

    @Test // I
    public void fromString_seated_returnsSeatedState() {
        assertInstanceOf(SeatedState.class, TableStateFactory.fromString("Seated"));
    }

    @Test // I
    public void fromString_eachValidInput_stateNameMatchesInput() {
        assertEquals("Available", TableStateFactory.fromString("Available").getName());
        assertEquals("Reserved",  TableStateFactory.fromString("Reserved").getName());
        assertEquals("Seated",    TableStateFactory.fromString("Seated").getName());
    }

    @Test // I
    public void fromString_calledTwice_returnsDifferentInstances() {
        assertNotSame(TableStateFactory.fromString("Available"),
                      TableStateFactory.fromString("Available"));
    }

    @Test // E
    public void fromString_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> TableStateFactory.fromString(null));
    }

    @Test // E
    public void fromString_null_exceptionMessageContainsCannotBeNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> TableStateFactory.fromString(null));
        assertTrue(ex.getMessage().contains("cannot be null"));
    }

    @Test // E
    public void fromString_unknownString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> TableStateFactory.fromString("Occupied"));
    }

    @Test // E
    public void fromString_unknownString_exceptionMessageContainsValue() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> TableStateFactory.fromString("Occupied"));
        assertTrue(ex.getMessage().contains("Occupied"));
    }

    @Test // Z
    public void fromString_emptyString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> TableStateFactory.fromString(""));
    }

    @Test // B
    public void fromString_lowercaseAvailable_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> TableStateFactory.fromString("available"));
    }

    @Test // B
    public void fromString_valueWithLeadingSpace_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> TableStateFactory.fromString(" Available"));
    }
}
