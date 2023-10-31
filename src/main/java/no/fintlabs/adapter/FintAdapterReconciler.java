package no.fintlabs.adapter;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.FlaisReconiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    @Value("${fint.accepted.adapter-name:}")
    private String acceptedName;

    @Value("${fint.accepted.adapter-namespace:}")
    private String acceptedNamespace;

    @Override
    public UpdateControl<FintAdapterCrd> reconcile(FintAdapterCrd resource, Context<FintAdapterCrd> context) {
        //return super.reconcile(resource, context);

        String name = resource.getMetadata().getName();
        String namespace = resource.getMetadata().getNamespace();

        if (name.contains(acceptedName) && namespace.contains(acceptedNamespace)
                && StringUtils.hasText(acceptedName) && StringUtils.hasText(acceptedNamespace)) {
            log.info("Include update for " + name + " in " + namespace);
            return super.reconcile(resource, context);
        } else {
            log.info("Skip update for " + name + " in " + namespace);
            return UpdateControl.noUpdate();
        }
    }
}
