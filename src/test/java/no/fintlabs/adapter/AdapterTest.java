package no.fintlabs.adapter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AdapterTest {
    @Test
    public void testAddAssetIdNotInList() {
        Adapter adapter = testAdapter();
        adapter.addAssetId("asset1");
        assertTrue(adapter.getAssetIds().contains("asset1"));
    }

    @Test
    public void testAddAssetIdAlreadyInList() {
        Adapter adapter = testAdapter();
        adapter.addAssetId("asset1");
        adapter.addAssetId("asset1");
        assertEquals(1, adapter.getAssetIds().size());
    }

    @Test
    public void testAddComponentWhenListIsEmpty() {
        Adapter adapter = testAdapter();
        adapter.addComponent("component1");
        assertTrue(adapter.getComponents().contains("component1"));
        assertEquals(1, adapter.getComponents().size());
    }

    @Test
    public void testAddComponentWhenComponentDoesNotExist() {
        Adapter adapter = testAdapter();
        adapter.addComponent("component1");
        assertEquals(1,adapter.getComponents().size());
        assertEquals("component1",adapter.getComponents().get(0));
    }

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
