package no.fintlabs.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import no.fintlabs.FintCustomerObjectEvent;

@Getter
@AllArgsConstructor
@SuperBuilder
public class AdapterEvent extends FintCustomerObjectEvent<Adapter> {

    public AdapterEvent(Adapter object, String orgId, FintCustomerObjectEvent.Operation operation) {
        super(object, orgId, operation, null);
    }
}
