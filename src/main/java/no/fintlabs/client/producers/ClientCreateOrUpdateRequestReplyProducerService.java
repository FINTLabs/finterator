package no.fintlabs.client.producers;

import no.fintlabs.client.FintClient;
import no.fintlabs.kafka.requestreply.RequestProducerFactory;
import no.fintlabs.kafka.requestreply.topic.ReplyTopicService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClientCreateOrUpdateRequestReplyProducerService extends ClientRequestReplyProducer<ClientRequest, FintClient> {

    public ClientCreateOrUpdateRequestReplyProducerService(
            RequestProducerFactory requestProducerFactory,
            ReplyTopicService replyTopicService,
            @Value("${fint.application-id}") String applicationId
    ) {
        super(
                requestProducerFactory,
                replyTopicService,
                applicationId,
                "client-create",
                "client",
                ClientRequest.class,
                FintClient.class
        );
    }

}
