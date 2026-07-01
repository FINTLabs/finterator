package no.fintlabs.adapter;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import no.fintlabs.SecretService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class FintAdapterDependentResourceTest {

    @Test
    public void testHandleDesiredForExistingWithEmptyAssetsAndComponents() throws Exception {
        FintAdapterCrd primary = primary(
                "fintlabs.no",
                "Test note",
                List.of(),
                List.of()
        );

        Adapter currentAdapter = currentAdapter();

        Function<Adapter, Adapter> handleDesiredForExisting = handleDesiredForExistingFunction();

        Adapter desiredAdapter = handleDesiredForExisting.apply(currentAdapter);

        Assertions.assertNotEquals(currentAdapter, desiredAdapter);
        Assertions.assertTrue(desiredAdapter.getComponents().isEmpty());
        Assertions.assertTrue(desiredAdapter.getAssets().isEmpty());
        Assertions.assertTrue(desiredAdapter.isManaged());
        Assertions.assertEquals(
                "Test note\n\nDenne adapteren er automatisk opprettet og håndteres av TESTAPP",
                desiredAdapter.getNote()
        );
    }

    @Test
    public void testHandleDesiredForExistingMapsAssetsToDns() throws Exception {
        FintAdapterCrd primary = primary(
                "fintlabs.no",
                "Test note",
                List.of(),
                List.of("asset.test.no")
        );

        Adapter currentAdapter = currentAdapter();

        Function<Adapter, Adapter> handleDesiredForExisting = handleDesiredForExistingFunction(primary);

        Adapter desiredAdapter = handleDesiredForExisting.apply(currentAdapter);

        Assertions.assertEquals(1, desiredAdapter.getAssets().size());
        Assertions.assertEquals(
                "ou=asset_test_no,ou=assets,ou=fintlabs-no,ou=organisations,o=test",
                desiredAdapter.getAssets().get(0)
        );
    }

    @Test
    public void testHandleDesiredForExistingMapsMultipleAssetsToDns() throws Exception {
        FintAdapterCrd primary = primary(
                "fintlabs.no",
                "Test note",
                List.of(),
                List.of("asset.one.no", "asset.two.no")
        );

        Adapter currentAdapter = currentAdapter();

        Function<Adapter, Adapter> handleDesiredForExisting =
                handleDesiredForExistingFunction(primary);

        Adapter desiredAdapter = handleDesiredForExisting.apply(currentAdapter);

        Assertions.assertEquals(2, desiredAdapter.getAssets().size());
        Assertions.assertTrue(desiredAdapter.getAssets().contains(
                "ou=asset_one_no,ou=assets,ou=fintlabs-no,ou=organisations,o=test"
        ));
        Assertions.assertTrue(desiredAdapter.getAssets().contains(
                "ou=asset_two_no,ou=assets,ou=fintlabs-no,ou=organisations,o=test"
        ));
    }

    @Test
    public void testHandleDesiredForExistingClearsOldAssets() throws Exception {
        FintAdapterCrd primary = primary(
                "fintlabs.no",
                "Test note",
                List.of(),
                List.of("new.asset.no")
        );

        Adapter currentAdapter = currentAdapter();
        currentAdapter.addAsset("ou=old_asset,ou=assets,ou=fintlabs-no,ou=organisations,o=fint");

        Function<Adapter, Adapter> handleDesiredForExisting =
                handleDesiredForExistingFunction(primary);

        Adapter desiredAdapter = handleDesiredForExisting.apply(currentAdapter);

        Assertions.assertEquals(1, desiredAdapter.getAssets().size());
        Assertions.assertFalse(desiredAdapter.getAssets().contains(
                "ou=old_asset,ou=assets,ou=fintlabs-no,ou=organisations,o=fint"
        ));
        Assertions.assertEquals(
                "ou=new_asset_no,ou=assets,ou=fintlabs-no,ou=organisations,o=test",
                desiredAdapter.getAssets().get(0)
        );
    }

    @Test
    public void testHandleDesiredForExistingMapsComponentsToDns() throws Exception {
        FintAdapterCrd primary = primary(
                "fintlabs.no",
                "Test note",
                List.of("component1"),
                List.of()
        );

        Adapter currentAdapter = currentAdapter();

        Function<Adapter, Adapter> handleDesiredForExisting =
                handleDesiredForExistingFunction(primary);

        Adapter desiredAdapter = handleDesiredForExisting.apply(currentAdapter);

        Assertions.assertEquals(1, desiredAdapter.getComponents().size());
        Assertions.assertEquals(
                "ou=component1,ou=components,o=fint",
                desiredAdapter.getComponents().get(0)
        );
    }

    @Test
    public void testHandleDesiredForExistingClearsOldComponents() throws Exception {
        FintAdapterCrd primary = primary(
                "fintlabs.no",
                "Test note",
                List.of("newComponent"),
                List.of()
        );

        Adapter currentAdapter = currentAdapter();
        currentAdapter.addComponent("ou=oldComponent,ou=components,o=fint");

        Function<Adapter, Adapter> handleDesiredForExisting =
                handleDesiredForExistingFunction(primary);

        Adapter desiredAdapter = handleDesiredForExisting.apply(currentAdapter);

        Assertions.assertEquals(1, desiredAdapter.getComponents().size());
        Assertions.assertFalse(desiredAdapter.getComponents().contains(
                "ou=oldComponent,ou=components,o=fint"
        ));
        Assertions.assertEquals(
                "ou=newComponent,ou=components,o=fint",
                desiredAdapter.getComponents().get(0)
        );
    }

    @Test
    public void testGenerateNote_regularCase() {
        FintAdapterCrd primary = new FintAdapterCrd();

        FintAdapterSpec spec = new FintAdapterSpec();
        spec.setNote("Test note");
        primary.setSpec(spec);

        String applicationId = "TESTAPP";

        String result = generateNote(primary, applicationId);

        Assertions.assertEquals(
                "Test note\n\nDenne adapteren er automatisk opprettet og håndteres av TESTAPP",
                result
        );
    }

    @SuppressWarnings("unchecked")
    private Function<Adapter, Adapter> handleDesiredForExistingFunction() throws Exception {
        FintAdapterDependentResource fintAdapterDependentResource =
                new FintAdapterDependentResource(
                        new FintAdapterWorkflow(),
                        mock(FintAdapterRepository.class),
                        mock(SecretService.class)
                );

        ReflectionTestUtils.setField(fintAdapterDependentResource, "applicationId", "TESTAPP");

        Method privateMethod = FintAdapterDependentResource.class
                .getDeclaredMethod("handleDesiredForExisting", FintAdapterCrd.class);
        privateMethod.setAccessible(true);

        FintAdapterCrd emptyPrimary = primary(
                "fintlabs.no",
                "Test note",
                List.of(),
                List.of()
        );

        return (Function<Adapter, Adapter>) privateMethod.invoke(
                fintAdapterDependentResource,
                emptyPrimary
        );
    }

    @SuppressWarnings("unchecked")
    private Function<Adapter, Adapter> handleDesiredForExistingFunction(FintAdapterCrd primary) throws Exception {
        FintAdapterDependentResource fintAdapterDependentResource =
                new FintAdapterDependentResource(
                        new FintAdapterWorkflow(),
                        mock(FintAdapterRepository.class),
                        mock(SecretService.class)
                );

        ReflectionTestUtils.setField(fintAdapterDependentResource, "applicationId", "TESTAPP");

        Method privateMethod = FintAdapterDependentResource.class
                .getDeclaredMethod("handleDesiredForExisting", FintAdapterCrd.class);
        privateMethod.setAccessible(true);

        return (Function<Adapter, Adapter>) privateMethod.invoke(
                fintAdapterDependentResource,
                primary
        );
    }

    private static FintAdapterCrd primary(
            String orgId,
            String note,
            List<String> components,
            List<String> assets
    ) {
        FintAdapterCrd primary = new FintAdapterCrd();
        primary.setMetadata(new ObjectMeta());

        FintAdapterSpec spec = new FintAdapterSpec();
        spec.setOrgId(orgId);
        spec.setNote(note);
        spec.setComponents(new ArrayList<>(components));
        spec.setAssets(new ArrayList<>(assets));

        primary.setSpec(spec);

        return primary;
    }

    private String generateNote(FintAdapterCrd primary, String applicationId) {
        return String.format(
                "%s\n\n%s%s",
                primary.getSpec().getNote(),
                "Denne adapteren er automatisk opprettet og håndteres av ",
                applicationId.toUpperCase()
        );
    }

    private static Adapter currentAdapter() {
        return new Adapter(
                "dn",
                "current",
                false,
                "shortDescription",
                new ArrayList<>(),
                "note",
                "password",
                "secret",
                "publicKey",
                "clientId",
                new ArrayList<>(),
                new ArrayList<>());
    }
}
