package no.fintlabs.client;

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
public class FintClientRepository {


    private final ClientEventRequestProducerService clientEventRequestProducerService;
    private final SecretService secretService;


    public FintClientRepository(ClientEventRequestProducerService clientEventRequestProducerService, SecretService secretService) {
        this.clientEventRequestProducerService = clientEventRequestProducerService;
        this.secretService = secretService;
    }

    public Client add(Client desired, FintClientCrd crd) {

        Optional<ClientEvent> optionalClientEvent = clientEventRequestProducerService.get(ClientEvent
                .builder()
                .object(desired)
                .orgId(crd.getSpec().getOrgId())
                .operation(FintCustomerObjectEvent.Operation.CREATE)
                .build());

        if (optionalClientEvent.isEmpty()) {
            throw new RuntimeException("An error occured while creating client: " + desired.getName());
        }

        ClientEvent clientEvent = optionalClientEvent.get();
        if (clientEvent.hasError()) {
            throw new RuntimeException(clientEvent.getErrorMessage());
        }

        Client client = clientEvent.getObject();
        log.info("Client {}", client);
        return client;
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
                        throw new CustomerObjectResponseException(clientEvent.getErrorMessage());
                    }
                    return clientEvent;
                })
                .map(FintCustomerObjectEvent::getObject)
                .orElseThrow(() -> new RuntimeException("An unexpected error occurred while updating client (" + desired.getName() + ")"));

    }

    public Set<Client> get(FintClientCrd crd) {

        Optional<String> dn = getValueFromAnnotationByKey(crd, FintClientDependentResource.ANNOTATION_CLIENT_DN);
        if (dn.isEmpty()) {
            throw new RuntimeException("Unable to find client DN");
        }

        Optional<ClientEvent> responseOptional = clientEventRequestProducerService.get(createRequestEvent(crd, dn.get()));

        if (responseOptional.isEmpty()) {
            throw new CustomerObjectResponseException("Empty response from Kafka. The request has probably timed out. Client: " + dn.get());
        }

        ClientEvent response = responseOptional.get();

        if (response.hasError()) {
            throw new CustomerObjectResponseException(response.getErrorMessage());
        }

        if (response.getObject() == null) {
            log.debug("Object in response is null");
            return Collections.emptySet();
        }

        return Collections.singleton(response.getObject());
    }

    private ClientEvent createRequestEvent(FintClientCrd crd, String dn) {
        return ClientEvent
                .builder()
                .object(Client
                        .builder()
                        .dn(dn)
                        .publicKey(secretService.getPublicKeyString())
                        .build())
                .orgId(crd.getSpec().getOrgId())
                .operation(FintCustomerObjectEvent.Operation.READ)
                .build();
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
