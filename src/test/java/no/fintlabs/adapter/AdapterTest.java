package no.fintlabs.adapter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AdapterTest {
    @Test
    public void testAddAssetNotInList() {
        Adapter adapter = testAdapter();
        adapter.addAsset("asset1");
        assertTrue(adapter.getAssets().contains("asset1"));
    }

    @Test
    public void testAddAssetAlreadyInList() {
        Adapter adapter = testAdapter();
        adapter.addAsset("asset1");
        adapter.addAsset("asset1");
        assertEquals(1, adapter.getAssets().size());
    }

    @Test
    public void testAddAssetIsCaseInsensitive() {
        Adapter adapter = testAdapter();

        adapter.addAsset("asset1");
        adapter.addAsset("ASSET1");

        assertEquals(1, adapter.getAssets().size());
        assertEquals("asset1", adapter.getAssets().get(0));
    }

    @Test
    public void testRemoveAssetWhenAssetExists() {
        Adapter adapter = testAdapter();

        adapter.addAsset("asset1");
        adapter.removeAsset("asset1");

        assertTrue(adapter.getAssets().isEmpty());
    }

    @Test
    public void testRemoveAssetIsCaseInsensitive() {
        Adapter adapter = testAdapter();

        adapter.addAsset("asset1");
        adapter.removeAsset("ASSET1");

        assertTrue(adapter.getAssets().isEmpty());
    }

    @Test
    public void testRemoveAssetWhenAssetDoesNotExist() {
        Adapter adapter = testAdapter();

        adapter.addAsset("asset1");
        adapter.removeAsset("asset2");

        assertEquals(1, adapter.getAssets().size());
        assertEquals("asset1", adapter.getAssets().get(0));
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

    @Test
    public void testAddComponentIsCaseInsensitive() {
        Adapter adapter = testAdapter();

        adapter.addComponent("component1");
        adapter.addComponent("COMPONENT1");

        assertEquals(1, adapter.getComponents().size());
        assertEquals("component1", adapter.getComponents().get(0));
    }

    @Test
    public void testRemoveComponentWhenComponentExists() {
        Adapter adapter = testAdapter();

        adapter.addComponent("component1");
        adapter.removeComponent("component1");

        assertTrue(adapter.getComponents().isEmpty());
    }

    @Test
    public void testRemoveComponentIsCaseInsensitive() {
        Adapter adapter = testAdapter();

        adapter.addComponent("component1");
        adapter.removeComponent("COMPONENT1");

        assertTrue(adapter.getComponents().isEmpty());
    }

    @Test
    public void testRemoveComponentWhenComponentDoesNotExist() {
        Adapter adapter = testAdapter();

        adapter.addComponent("component1");
        adapter.removeComponent("component2");

        assertEquals(1, adapter.getComponents().size());
        assertEquals("component1", adapter.getComponents().get(0));
    }

    @Test
    public void testSetAccessPackageReplacesExistingAccessPackage() {
        Adapter adapter = testAdapter();

        adapter.setAccessPackage("accessPackage1");
        adapter.setAccessPackage("accessPackage2");

        assertEquals(1, adapter.getAccessPackages().size());
        assertEquals("accessPackage2", adapter.getAccessPackages().get(0));
    }

    private static Adapter testAdapter() {
        return new Adapter(
                "",
                "",
                false,
                "",
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
