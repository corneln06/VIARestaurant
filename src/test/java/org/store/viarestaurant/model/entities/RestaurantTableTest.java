package org.store.viarestaurant.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.store.viarestaurant.model.state.AvailableState;
import org.store.viarestaurant.model.state.ReservedState;
import org.store.viarestaurant.model.state.SeatedState;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTableTest {

    private RestaurantTable table;

    @BeforeEach
    public void setUp() {
        table = new RestaurantTable(1, 4);
    }

    @Test // I
    public void getStatus_afterConstruction_isAvailableState() {
        assertInstanceOf(AvailableState.class, table.getStatus());
    }

    @Test // I
    public void getId_afterConstruction_returnsCorrectId() {
        assertEquals(1, table.getId());
    }

    @Test // I
    public void getMaxSitting_afterConstruction_returnsCorrectCapacity() {
        assertEquals(4, table.getMaxSitting());
    }

    @Test // S
    public void setMaxSitting_newValue_maxSittingUpdated() {
        table.setMaxSitting(8);
        assertEquals(8, table.getMaxSitting());
    }

    @Test // B
    public void setMaxSitting_zero_maxSittingIsZero() {
        table.setMaxSitting(0);
        assertEquals(0, table.getMaxSitting());
    }

    @Test // B
    public void setMaxSitting_one_maxSittingIsOne() {
        table.setMaxSitting(1);
        assertEquals(1, table.getMaxSitting());
    }

    @Test // S
    public void setState_seatedState_statusIsSeated() {
        table.setState(new SeatedState());
        assertInstanceOf(SeatedState.class, table.getStatus());
    }

    @Test // S
    public void setState_reservedState_statusIsReserved() {
        table.setState(new ReservedState());
        assertInstanceOf(ReservedState.class, table.getStatus());
    }

    @Test // S
    public void setState_availableAfterSeated_statusIsAvailable() {
        table.setState(new SeatedState());
        table.setState(new AvailableState());
        assertInstanceOf(AvailableState.class, table.getStatus());
    }

    @Test // S
    public void setState_toReserved_statusNameIsReserved() {
        table.setState(new ReservedState());
        assertEquals("Reserved", table.getStatus().getName());
    }

    @Test // I
    public void equals_sameInstance_returnsTrue() {
        assertEquals(table, table);
    }

    @Test // I
    public void equals_sameId_returnsTrue() {
        RestaurantTable other = new RestaurantTable(1, 8);
        assertEquals(table, other);
    }

    @Test // I
    public void equals_differentId_returnsFalse() {
        RestaurantTable other = new RestaurantTable(2, 4);
        assertNotEquals(table, other);
    }

    @Test // Z
    public void equals_null_returnsFalse() {
        assertNotEquals(null, table);
    }

    @Test // I
    public void equals_differentClass_returnsFalse() {
        assertNotEquals("not a table", table);
    }

    @Test // I
    public void hashCode_sameId_producesSameHash() {
        RestaurantTable other = new RestaurantTable(1, 10);
        assertEquals(table.hashCode(), other.hashCode());
    }

    @Test // I
    public void hashCode_calledTwice_returnsSameValue() {
        assertEquals(table.hashCode(), table.hashCode());
    }
}
