package no.fintlabs.adapter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdapterTest {
    // Test that assetId is added if it is not in the list
    @Test
    public void testAddAssetIdNotInList() {
        Adapter adapter = testAdapter();
        adapter.addAssetId("asset1");
        assertTrue(adapter.getAssetIds().contains("asset1"));
    }

    // Test that assetId is not added if it is already in the list
    @Test
    public void testAddAssetIdAlreadyInList() {
        Adapter adapter = testAdapter();
        adapter.addAssetId("asset1");
        adapter.addAssetId("asset1");
        assertEquals(1,adapter.getAssetIds().size());
    }

    // Test that component is added if it is not in the list
    @Test
    public void testAddComponentWhenListIsEmpty() {
        Adapter adapter = testAdapter();
        adapter.addComponent("component1");
        assertTrue(adapter.getComponents().contains("component1"));
        assertEquals(1,adapter.getComponents().size());
    }

    // Test that component is added if it does not exist
    @Test
    public void testAddComponentWhenComponentDoesNotExist() {
        Adapter adapter = testAdapter();
        adapter.addComponent("component1");
        assertEquals(1,adapter.getComponents().size());
        assertEquals("component1",adapter.getComponents().get(0));
    }

    // Test that component is not added if it already exists
    @Test
    public void testAddComponentWhenComponentExists() {
        Adapter adapter = testAdapter();
        adapter.addComponent("component1");
        adapter.addComponent("component1");

        assertEquals(1,adapter.getComponents().size());
    }

    private static Adapter testAdapter() {
        return new Adapter(
                "",
                "",
                false,
                "",
                new ArrayList<>(),
                new ArrayList<>(),
                "",
                "",
                "",
                "",
                "",
                new ArrayList<>(),
                new ArrayList<>());
    }
}
