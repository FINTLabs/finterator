package no.fintlabs.client.producers;

import no.fintlabs.client.FintClient;
import no.fintlabs.kafka.requestreply.RequestProducerFactory;
import no.fintlabs.kafka.requestreply.topic.ReplyTopicService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClientGetRequestReplyProducerService extends ClientRequestReplyProducer<ClientRequest, FintClient> {

    public ClientGetRequestReplyProducerService(
            RequestProducerFactory requestProducerFactory,
            ReplyTopicService replyTopicService,
            @Value("${fint.application-id}") String applicationId
    ) {
        super(
                requestProducerFactory,
                replyTopicService,
                applicationId,
                "client-get",
                "client",
                ClientRequest.class,
                FintClient.class
        );
    }

}
