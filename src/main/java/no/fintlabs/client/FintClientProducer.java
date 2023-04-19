package no.fintlabs.client;

import no.fintlabs.kafka.event.EventProducer;
import no.fintlabs.kafka.event.EventProducerFactory;
import no.fintlabs.kafka.event.EventProducerRecord;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import org.springframework.stereotype.Component;

//@Component
public class FintClientProducer {


    private final EventProducer<ClientEvent> eventProducer;

    public FintClientProducer(EventProducerFactory entityProducerFactory) {
        this.eventProducer = entityProducerFactory
                .createProducer(ClientEvent.class);
    }

    public void send(ClientEvent clientEvent) {

        eventProducer.send(
                EventProducerRecord.<ClientEvent>builder()
                        .topicNameParameters(
                                EventTopicNameParameters
                                        .builder()
                                        .orgId("flais.io")       // Optional if set as application property
                                        .domainContext("fint-service")
                                        .eventName("client")
                                        .build())
                        .value(clientEvent)
                        .build()

        );

    }


}
