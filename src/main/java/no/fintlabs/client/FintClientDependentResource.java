package no.fintlabs.client;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.DesiredEqualsMatcher;
import io.javaoperatorsdk.operator.processing.dependent.Matcher;
import io.javaoperatorsdk.operator.processing.dependent.Updater;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.FlaisExternalDependentResource;
import no.fintlabs.SecretService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;


@Slf4j
@Component
public class FintClientDependentResource
        extends FlaisExternalDependentResource<Client, FintClientCrd, FintClientSpec>
        implements Updater<Client, FintClientCrd> {


    public static final String ANNOTATION_CLIENT_DN = "flais.io/client-dn";

    @Value("${fint.application-id}")
    private String applicationId;

    private final FintClientRepository fintClientRepository;
    private final SecretService secretService;

    public FintClientDependentResource(FintClientWorkflow workflow,
                                       FintClientRepository fintClientRepository, SecretService secretService) {
        super(Client.class, workflow);
        this.fintClientRepository = fintClientRepository;
        this.secretService = secretService;
        setPollingPeriod(Duration.ofMinutes(1).toMillis());
    }

    @Override
    public Client update(Client actual, Client desired, FintClientCrd primary, Context<FintClientCrd> context) {
        log.info("Updating");
        return fintClientRepository.update(desired, primary);
    }

    @Override
    protected Client desired(FintClientCrd primary, Context<FintClientCrd> context) {
        log.debug("Desired storage account for {}:", primary.getMetadata().getName());

        return context
                .getSecondaryResource(Client.class)
                .map(handleDesiredForExisting(primary))
                .orElseGet(handleDesiredOnNew(primary));
    }

    private Supplier<Client> handleDesiredOnNew(FintClientCrd primary) {
        return () -> {
            String clientName = String.format("%s-%s", primary.getMetadata().getName(), RandomStringUtils.randomAlphabetic(5).toLowerCase());
            Client client = Client
                    .builder()
                    .name(clientName)
                    .shortDescription(clientName)
                    .note(generateNote(primary))
                    .publicKey(secretService.getPublicKeyString())
                    .isManaged(true)
                    .build();
            primary.getSpec().getComponents()
                    .forEach(component -> client.addComponent(String.format("ou=%s,ou=components,o=fint", component)));
            log.info("No client found in event store. Desired client is: {}", client);

            return client;
        };
    }

    private String generateNote(FintClientCrd primary) {
        return String.format("%s\n\n%s%s", primary.getSpec().getNote(), "Denne klienten er automatisk opprettet og håndteres av ", applicationId.toUpperCase());
    }

    private Function<Client, Client> handleDesiredForExisting(FintClientCrd primary) {
        return currentClient -> {
            log.info("Found client {} in event store", currentClient.getDn());

            Client desiredClient = SerializationUtils.clone(currentClient);
            desiredClient.setNote(generateNote(primary));
            desiredClient.getComponents().clear();
            desiredClient.setManaged(true);
            primary.getSpec().getComponents()
                    .forEach(component -> desiredClient.addComponent(String.format("ou=%s,ou=components,o=fint", component)));

            return desiredClient;
        };
    }

    @Override
    public void delete(FintClientCrd primary, Context<FintClientCrd> context) {
        try {
            context.getSecondaryResource(Client.class)
                    .ifPresent(client -> fintClientRepository.delete(client, primary));
        } catch (IllegalArgumentException e) {
            log.error("An error occurred when deleting {}", primary.getMetadata().getName());
            log.error("Error message is {}", e.getMessage());
            log.error("This is probably because we were not able to fetch the storage account from Azure. Probably" +
                    " because it does not exist. You can most likely ignore this error ;)");
        }
    }

    @Override
    public Client create(Client desired, FintClientCrd primary, Context<FintClientCrd> context) {

        log.info("Creating...");
        log.info("Client is present in context: {}", context.getSecondaryResource(Client.class).isPresent());

        Client client = fintClientRepository.add(desired, primary);

        primary.getMetadata().getAnnotations().put(ANNOTATION_CLIENT_DN, client.getDn());

        return client;
    }

    @Override
    public Set<Client> fetchResources(FintClientCrd primaryResource) {
        return fintClientRepository.get(primaryResource);
    }

    @Override
    public Matcher.Result<Client> match(Client actualResource, FintClientCrd primary, Context<FintClientCrd> context) {
        DesiredEqualsMatcher<Client, FintClientCrd> matcher = new DesiredEqualsMatcher<>(this);

        return matcher.match(actualResource, primary, context);
    }
}
