package no.fintlabs.client;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public
class FintClientStatus extends ObservedGenerationAwareStatus {
    private List<String> dependentResourceStatus = new ArrayList<>();
    private String errorMessage;
}
