package no.fintlabs.adapter;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.CustomerObjectResponseException;
import no.fintlabs.FintCustomerObjectEvent;
import no.fintlabs.SecretService;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static no.fintlabs.CrdUtilities.getValueFromAnnotationByKey;

@Slf4j
@Repository
public class FintAdapterRepository {

    private final AdapterEventRequestProducerService adapterEventRequestProducerService;
    private final SecretService secretService;

    public FintAdapterRepository(AdapterEventRequestProducerService adapterEventRequestProducerService, SecretService secretService) {
        this.adapterEventRequestProducerService = adapterEventRequestProducerService;
        this.secretService = secretService;
    }

    public Adapter add(Adapter desired, FintAdapterCrd crd) {

        log.debug("Repository add: Desired name: {}, Desired clientId: {}, crd {}", desired.getName(), desired.getClientId(), crd);

        Optional<AdapterEvent> adapterEvent = adapterEventRequestProducerService.get(AdapterEvent
                .builder()
                .object(desired)
                .orgId(crd.getSpec().getOrgId())
                .operation(FintCustomerObjectEvent.Operation.CREATE)
                .build());

        log.debug("Repository add: Adapter event: {}", adapterEvent);

        if (adapterEvent.isPresent()) {
            AdapterEvent adapterEvent1 = adapterEvent.get();
            log.debug("adapterEvent1: {}", adapterEvent1);
            if (adapterEvent1.hasError()) {
                throw new CustomerObjectResponseException(adapterEvent1.getErrorMessage());
            }
            Adapter adapter = adapterEvent1.getObject();

            log.info("Adapter {}", adapter);
            return adapter;
        }

        throw new RuntimeException("An error has occurred while creating adapter: " + desired.getName());
    }

    public Adapter update(Adapter desired, FintAdapterCrd crd) {

        return adapterEventRequestProducerService
                .get(AdapterEvent
                        .builder()
                        .object(desired)
                        .orgId(crd.getSpec().getOrgId())
                        .operation(FintCustomerObjectEvent.Operation.UPDATE)
                        .build()
                )
                .map(adapterEvent -> {
                    if (adapterEvent.hasError()) {
                        throw new CustomerObjectResponseException(adapterEvent.getErrorMessage());
                    }
                    return adapterEvent;
                })
                .map(FintCustomerObjectEvent::getObject)
                .orElseThrow(() -> new RuntimeException("An unexpected error occurred while updating adapter: " + desired.getName()));
    }

    public Set<Adapter> get(FintAdapterCrd crd) {

        Optional<String> dn = getValueFromAnnotationByKey(crd, FintAdapterDependentResource.ANNOTATION_ADAPTER_DN);
        if (dn.isEmpty()) {
            log.debug("Skipping adapter lookup due to missing DN in CRD.");
            return Collections.emptySet();
        }

        Optional<AdapterEvent> responseOptional = adapterEventRequestProducerService.get(createRequestEvent(crd, dn.get()));

        if (responseOptional.isEmpty()) {
            throw new CustomerObjectResponseException("Empty response from Kafka. The request has probably timed out. Client: " + dn.get());
        }

        AdapterEvent response = responseOptional.get();

        if (response.hasError()){
            throw new CustomerObjectResponseException(response.getErrorMessage());
        }

        if (response.getObject() == null) {
            log.debug("Object in response is null");
            return Collections.emptySet();
        }

        return Collections.singleton(response.getObject());
    }

    private AdapterEvent createRequestEvent(FintAdapterCrd crd, String dn) {
        return AdapterEvent
                .builder()
                .object(Adapter
                        .builder()
                        .dn(dn)
                        .publicKey(secretService.getPublicKeyString())
                        .build())
                .orgId(crd.getSpec().getOrgId())
                .operation(FintCustomerObjectEvent.Operation.READ)
                .build();
    }

    public void delete(Adapter adapter, FintAdapterCrd primary) {
        adapterEventRequestProducerService.get(AdapterEvent
                .builder()
                .object(adapter)
                .orgId(primary.getSpec().getOrgId())
                .operation(FintCustomerObjectEvent.Operation.DELETE)
                .build());

        log.info("Adapter {} deleted", adapter.getDn());
    }
}
