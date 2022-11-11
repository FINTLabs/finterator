package no.fintlabs.client;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.client.producers.ClientCreateOrUpdateRequestReplyProducerService;
import no.fintlabs.client.producers.ClientDeleteRequestReplyProducerService;
import no.fintlabs.client.producers.ClientGetRequestReplyProducerService;
import no.fintlabs.client.producers.ClientRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class FintClientService {

    private final ClientDeleteRequestReplyProducerService deleteProducerService;
    private final ClientGetRequestReplyProducerService getProducerService;
    private final ClientCreateOrUpdateRequestReplyProducerService createOrUpdateProducerService;

    public FintClientService(ClientDeleteRequestReplyProducerService deleteProducerService, ClientGetRequestReplyProducerService getProducerService, ClientCreateOrUpdateRequestReplyProducerService createOrUpdateProducerService) {
        this.deleteProducerService = deleteProducerService;
        this.getProducerService = getProducerService;
        this.createOrUpdateProducerService = createOrUpdateProducerService;
    }

    public FintClient add(FintClientCrd crd) {

        return createOrUpdateProducerService.get(ClientRequest
                        .builder()
                        .name(crd.getMetadata().getName())
                        .orgId(crd.getSpec().getOrgId())
                        .note(crd.getSpec().getNote())
                        .shortDescription(crd.getSpec().getShortDescription())
                        .components(crd.getSpec().getComponents())
                        .build())
                .orElseThrow();
    }

    public Set<FintClient> get(FintClientCrd crd) {

        return getProducerService.get(ClientRequest
                        .builder()
                        .name(crd.getMetadata().getName())
                        .orgId(crd.getSpec().getOrgId())
                        .note(crd.getSpec().getNote())
                        .shortDescription(crd.getSpec().getShortDescription())
                        .components(crd.getSpec().getComponents())
                        .build())
                .map(Collections::singleton)
                .orElse(Collections.emptySet());
    }

    public void delete(FintClient fintClient) {

        deleteProducerService.get(ClientRequest
                .builder()
                .name(fintClient.getUsername())
                .orgId(fintClient.getOrgId())
                .build());
    }
}
