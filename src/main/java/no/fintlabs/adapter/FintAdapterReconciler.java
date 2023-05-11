package no.fintlabs.adapter;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.FlaisReconiler;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ControllerConfiguration
public class FintAdapterReconciler extends FlaisReconiler<FintAdapterCrd, FintAdapterSpec> {
    public FintAdapterReconciler(
            FintAdapterWorkflow workflow,
            List<? extends DependentResource<?, FintAdapterCrd>> eventSourceProviders,
            List<? extends Deleter<FintAdapterCrd>> deleters
            ) {
        super(workflow, eventSourceProviders, deleters);
    }

    @Override
    public UpdateControl<FintAdapterCrd> reconcile(FintAdapterCrd resource, Context<FintAdapterCrd> context) {
        return super.reconcile(resource, context);
    }
}
