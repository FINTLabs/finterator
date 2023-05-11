package no.fintlabs.adapter;

import no.fintlabs.kafka.event.EventProducer;
import no.fintlabs.kafka.event.EventProducerFactory;
import no.fintlabs.kafka.event.EventProducerRecord;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;

public class FintAdapterProducer {

    private final EventProducer<AdapterEvent> eventProducer;

    public FintAdapterProducer(EventProducerFactory entityProducerFactory) {
        this.eventProducer = entityProducerFactory
                .createProducer(AdapterEvent.class);
    }

    public void send(AdapterEvent adapterEvent) {
        eventProducer.send(
                EventProducerRecord.<AdapterEvent>builder()
                        .topicNameParameters(
                                EventTopicNameParameters
                                        .builder()
                                        .orgId("flais.io")
                                        .domainContext("fint-service")
                                        .eventName("adapter")
                                        .build()
                        )
                        .value(adapterEvent)
                        .build()
        );
    }
}
