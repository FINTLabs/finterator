package no.fintlabs.client;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.FintCustomerObjectEvent;
import no.fintlabs.SecretService;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static no.fintlabs.CrdUtilities.getValueFromAnnotationByKey;

@Slf4j
@Repository
public class FintClientRepository {


    private final ClientEventRequestProducerService clientEventRequestProducerService;
    private final SecretService secretService;


    public FintClientRepository(ClientEventRequestProducerService clientEventRequestProducerService, SecretService secretService) {
        this.clientEventRequestProducerService = clientEventRequestProducerService;
        this.secretService = secretService;
    }

    public Client add(Client desired, FintClientCrd crd) {


        Optional<ClientEvent> clientEvent = clientEventRequestProducerService.get(ClientEvent
                .builder()
                .object(desired)
                .orgId(crd.getSpec().getOrgId())
                .operation(FintCustomerObjectEvent.Operation.CREATE)
                .build());

        if (clientEvent.isPresent()) {
            ClientEvent clientEvent1 = clientEvent.get();
            if (clientEvent1.hasError()) {
                throw new RuntimeException(clientEvent1.getErrorMessage());
            }
            Client client = clientEvent1.getObject();

            log.info("Client {}", client);

            return client;

        }

        throw new RuntimeException("An error occured while creating client: " + desired.getName());


    }

    public Client update(Client desired, FintClientCrd crd) {

        return clientEventRequestProducerService
                .get(ClientEvent
                        .builder()
                        .object(desired)
                        .orgId(crd.getSpec().getOrgId())
                        .operation(FintCustomerObjectEvent.Operation.UPDATE)
                        .build()
                )
                .map(clientEvent -> {
                    if (clientEvent.hasError()) {
                        throw new RuntimeException(clientEvent.getErrorMessage());
                    }
                    return clientEvent;
                })
                .map(FintCustomerObjectEvent::getObject)
                .orElseThrow(() -> new RuntimeException("An unexpected error occurred while updating client (" + desired.getName() + ")"));

    }

    public Set<Client> get(FintClientCrd crd) {

        return getValueFromAnnotationByKey(crd, FintClientDependentResource.ANNOTATION_CLIENT_DN)
                .map(dn -> clientEventRequestProducerService.get(ClientEvent
                                .builder()
                                .object(Client
                                        .builder()
                                        .dn(dn)
                                        .publicKey(secretService.getPublicKeyString())
                                        .build())
                                .orgId(crd.getSpec().getOrgId())
                                .operation(FintCustomerObjectEvent.Operation.READ)
                                .build())
                        .map(ClientEvent::getObject)
                        .map(Collections::singleton)
                        .orElse(Collections.emptySet())
                )
                .orElse(Collections.emptySet());
    }

    public void delete(Client client, FintClientCrd primay) {
        clientEventRequestProducerService.get(ClientEvent
                .builder()
                .object(client)
                .orgId(primay.getSpec().getOrgId())
                .operation(FintCustomerObjectEvent.Operation.DELETE)
                .build());

        log.info("Client {} deleted", client.getDn());
    }
}
