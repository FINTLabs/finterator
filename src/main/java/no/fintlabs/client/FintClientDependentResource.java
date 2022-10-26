package no.fintlabs.client;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.EventSourceProvider;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.external.PerResourcePollingDependentResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;


@Slf4j
@Component
public class FintClientDependentResource
        extends PerResourcePollingDependentResource<FintClient, FintClientCrd>
        implements EventSourceProvider<FintClientCrd>,
        Creator<FintClient, FintClientCrd>,
        Deleter<FintClientCrd> {


    private final FintClientService fintClientService;

    public FintClientDependentResource(FintClientWorkflow workflow,
                                       FintClientService fintClientService) {
        super(FintClient.class, Duration.ofMinutes(10).toMillis());
        this.fintClientService = fintClientService;
        workflow.addDependentResource(this);
    }

    @Override
    protected FintClient desired(FintClientCrd primary, Context<FintClientCrd> context) {
        log.debug("Desired storage account for {}:", primary.getMetadata().getName());
        log.debug("\t{}", primary);

        return FintClient.builder()
                //.resourceGroup(primary.getSpec().getResourceGroup())
                //.storageAccountName(primary.getMetadata().getName())
                .build();
    }

    @Override
    public void delete(FintClientCrd primary, Context<FintClientCrd> context) {
        try {
        context.getSecondaryResource(FintClient.class)
                .ifPresent(fintClientService::delete);
        } catch (IllegalArgumentException e) {
            log.error("An error occurred when deleting {}", primary.getMetadata().getName());
            log.error("Error message is {}", e.getMessage());
            log.error("This is probably because we were not able to fetch the storage account from Azure. Probably" +
                    " because it does not exist. You can most likely ignore this error ;)");
        }
    }

    @Override
    public FintClient create(FintClient desired, FintClientCrd primary, Context<FintClientCrd> context) {
        return fintClientService.add(primary);
    }

    @Override
    public Set<FintClient> fetchResources(FintClientCrd primaryResource) {
        return fintClientService.get(primaryResource);
    }
}
