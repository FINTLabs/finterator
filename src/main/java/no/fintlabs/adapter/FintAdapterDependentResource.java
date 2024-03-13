package no.fintlabs.adapter;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.DesiredEqualsMatcher;
import io.javaoperatorsdk.operator.processing.dependent.Matcher;
import io.javaoperatorsdk.operator.processing.dependent.Updater;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.FlaisExternalDependentResource;
import no.fintlabs.SecretService;
import no.fintlabs.client.Client;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Component
public class FintAdapterDependentResource
        extends FlaisExternalDependentResource<Adapter, FintAdapterCrd, FintAdapterSpec>
        implements Updater<Adapter, FintAdapterCrd> {

    public static final String ANNOTATION_ADAPTER_DN = "flais.io/adapter-dn";

    @Value("${fint.application-id}")
    private String applicationId;

    private final FintAdapterRepository fintAdapterRepository;
    private final SecretService secretService;

    public FintAdapterDependentResource(FintAdapterWorkflow workflow,
                                        FintAdapterRepository fintAdapterRepository,
                                        SecretService secretService) {
        super(Adapter.class, workflow);
        this.fintAdapterRepository = fintAdapterRepository;
        this.secretService = secretService;
        setPollingPeriod(Duration.ofMinutes(60).toMillis());
    }

    @Override
    public Adapter update(Adapter actual, Adapter desired, FintAdapterCrd primary, Context<FintAdapterCrd> context) {
        log.info("Updating");
        return fintAdapterRepository.update(desired, primary);
    }

    @Override
    public Adapter desired(FintAdapterCrd primary, Context<FintAdapterCrd> context) {
        log.debug("Desired storage account for: {}", primary.getMetadata().getName());

        return context
                .getSecondaryResource(Adapter.class)
                .map(handleDesiredForExisting(primary))
                .orElseGet(handleDesiredOnNew(primary));
    }

    private Supplier<Adapter> handleDesiredOnNew(FintAdapterCrd primary) {
        return () -> {
            String adapterName = String.format("%s-%s", primary.getMetadata().getName(), RandomStringUtils.randomAlphabetic(5).toLowerCase());
            Adapter adapter = Adapter
                    .builder()
                    .name(adapterName)
                    .shortDescription(adapterName)
                    .note(generateNote(primary))
                    .publicKey(secretService.getPublicKeyString())
                    .isManaged(true)
                    .build();
            primary.getSpec().getComponents()
                    .forEach(component -> adapter.addComponent(String.format("ou=%s,ou=components,o=fint", component)));

            String orgId = primary.getSpec().getOrgId().replace(".","_");
            primary.getSpec().getAssets()
                    .forEach(asset -> adapter.addAssets(String.format("ou=%s,ou=assets,ou=%s,ou=organisations,o=fint", asset.replace(".","_"), orgId)));
            log.info("No adapter found in event store. Desired adapter is: {}", adapter);

            return adapter;
        };
    }

    private String generateNote(FintAdapterCrd primary) {
        return String.format("%s\n\n%s%s", primary.getSpec().getNote(), "Denne adapteren er automatisk opprettet og håndteres av ", applicationId.toUpperCase());
    }

    private Function<Adapter, Adapter> handleDesiredForExisting(FintAdapterCrd primary) {
        return currentAdapter -> {
            log.info("Found adapter {} in event store", currentAdapter.getDn());

            Adapter desiredAdapter = SerializationUtils.clone(currentAdapter);
            desiredAdapter.setNote(generateNote(primary));
            desiredAdapter.getComponents().clear();
            desiredAdapter.getAssets().clear();
            desiredAdapter.setManaged(true);
            primary.getSpec().getComponents()
                    .forEach(component -> desiredAdapter.addComponent(String.format("ou=%s,ou=components,o=fint", component)));

            String orgId = primary.getSpec().getOrgId().replace(".","_");
            primary.getSpec().getAssets()
                    .forEach(asset -> desiredAdapter.addAssets(String.format("ou=%s,ou=assets,ou=%s,ou=organisations,o=fint", asset.replace(".","_"), orgId)));

            return desiredAdapter;
        };
    }

    public void delete(FintAdapterCrd primary, Context<FintAdapterCrd> context) {
        try {
            context.getSecondaryResource(Adapter.class)
                    .ifPresent(adapter -> fintAdapterRepository.delete(adapter, primary));
        } catch (IllegalArgumentException e) {
            log.error("An error occurred when deleting {}", primary.getMetadata().getName());
            log.error("Error message is {}", e.getMessage());
            log.error("This is probably because we were not able to fetch the storage account from Azure. Probably" +
                    " because it does not exist. You can most likely ignore this error ;)");
        }
    }

    @Override
    public Adapter create(Adapter desired, FintAdapterCrd primary, Context<FintAdapterCrd> context) {
        log.info("Creating adapter...");
        log.info("Adapter is present in context: {}", context.getSecondaryResource(Adapter.class).isPresent());

        Adapter adapter = fintAdapterRepository.add(desired, primary);

        primary.getMetadata().getAnnotations().put(ANNOTATION_ADAPTER_DN, adapter.getDn());

        return adapter;
    }

    @Override
    public Set<Adapter> fetchResources(FintAdapterCrd primaryResource) {
        Set<Adapter> adapters = fintAdapterRepository.get(primaryResource);

        for (var adapter : adapters) {
            if (isSecretOrPasswordMissing(adapter)) {
                adapter.setNote("Trigger update because clientSecret or password is empty");
                log.info("Change adapter '{}' to trigger update", adapter.getName());
            }
        }

        return adapters;
    }

    private boolean isSecretOrPasswordMissing(Adapter adapter) {
        return /*adapter.isManaged() &&*/ (StringUtils.isEmpty(adapter.getClientSecret()) || StringUtils.isEmpty(adapter.getPassword()));
    }

    @Override
    public Matcher.Result<Adapter> match(
            Adapter actualResource, FintAdapterCrd primary, Context<FintAdapterCrd> context
    ) {
        DesiredEqualsMatcher<Adapter, FintAdapterCrd> matcher = new DesiredEqualsMatcher<>(this);

        return matcher.match(actualResource, primary, context);
    }

}
