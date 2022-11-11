package no.fintlabs.client;

import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.FlaisReconiler;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ControllerConfiguration
public class FintClientReconiler extends FlaisReconiler<FintClientCrd, FintClientSpec> {


    public FintClientReconiler(FintClientWorkflow workflow,
                               List<? extends DependentResource<?, FintClientCrd>> eventSourceProviders,
                               List<? extends Deleter<FintClientCrd>> deleters) {

        super(workflow, eventSourceProviders, deleters);

    }


}
