package no.fintlabs.client;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.FlaisExternalDependentResource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;


@Slf4j
@Component
public class FintClientDependentResource
        extends FlaisExternalDependentResource<FintClient, FintClientCrd, FintClientSpec> {


    private final FintClientService fintClientService;

    public FintClientDependentResource(FintClientWorkflow workflow,
                                       FintClientService fintClientService) {
        super(FintClient.class, workflow);
        this.fintClientService = fintClientService;
        workflow.addDependentResource(this);
        setPollingPeriod(Duration.ofMinutes(10).toMillis());
    }

    @Override
    protected FintClient desired(FintClientCrd primary, Context<FintClientCrd> context) {
        log.debug("Desired storage account for {}:", primary.getMetadata().getName());
        log.debug("\t{}", primary);

        return FintClient.builder()
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
