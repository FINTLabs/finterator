package no.fintlabs.adapter;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import no.fintlabs.SecretService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class FintAdapterDependentResourceTest {

    @Test
    public void testHandleDesiredForExisting() {
        FintAdapterCrd primary = new FintAdapterCrd();
        Adapter currentAdapter = currentAdapter();
        Adapter desiredAdapter = desiredAdapter();
        primary.setMetadata(new ObjectMeta());
        System.out.println("Primary: " + primary);

        try {
            FintAdapterDependentResource fintAdapterDependentResource = new FintAdapterDependentResource(new FintAdapterWorkflow(), mock(FintAdapterRepository.class), new SecretService());
            Method privateMethod = FintAdapterDependentResource.class.getDeclaredMethod("handleDesiredForExisting", FintAdapterCrd.class);
            privateMethod.setAccessible(true);
            Object function = privateMethod.invoke(fintAdapterDependentResource, primary);
            Function<Adapter, Adapter> handleDesiredForExisting = (Function<Adapter, Adapter>) function;
            desiredAdapter = handleDesiredForExisting.apply(currentAdapter);
            Assertions.assertNotEquals(currentAdapter, desiredAdapter);
            Assertions.assertTrue(desiredAdapter.getComponents().isEmpty());
            Assertions.assertTrue(desiredAdapter.getAssetIds().isEmpty());
            Assertions.assertTrue(desiredAdapter.isManaged());
        } catch (Exception e) {
            System.out.println("Error occurred during test: " + e.getMessage());
        }
        System.out.println("currentAdapter: " + currentAdapter);
        System.out.println("desiredAdapter: " + desiredAdapter);
    }

    @Test
    public void testGenerateNote_regularCase() {
        FintAdapterCrd primary = new FintAdapterCrd();
        FintAdapterSpec spec = new FintAdapterSpec();
        spec.setNote("Test note");
        primary.setSpec(spec);
        String applicationId = "TESTAPP";
        String result = generateNote(primary, applicationId);
        assertEquals("Test note\n\nDenne adapteren er automatisk opprettet og håndteres av TESTAPP", result);
    }

    private String generateNote(FintAdapterCrd primary, String applicationId) {
        return String.format("%s\n\n%s%s", primary.getSpec().getNote(), "Denne adapteren er automatisk opprettet og håndteres av ", applicationId.toUpperCase());
    }

    private static Adapter currentAdapter() {
        return new Adapter(
                "dn",
                "current",
                false,
                "shortDescription",
                new ArrayList<>(),
                new ArrayList<>(),
                "note",
                "password",
                "secret",
                "publicKey",
                "clientId",
                new ArrayList<>(),
                new ArrayList<>());
    }
    private static Adapter desiredAdapter() {
        return new Adapter(
                "dn",
                "desired",
                false,
                "shortDescription",
                new ArrayList<>(),
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
