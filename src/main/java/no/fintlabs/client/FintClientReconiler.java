package no.fintlabs.client;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.EventSourceProvider;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Workflow;
import io.javaoperatorsdk.operator.processing.dependent.workflow.WorkflowReconcileResult;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.CrdValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ControllerConfiguration(
        generationAwareEventProcessing = false
)
public class FintClientReconiler implements Reconciler<FintClientCrd>,
        Cleaner<FintClientCrd>,
        ErrorStatusHandler<FintClientCrd>,
        EventSourceInitializer<FintClientCrd> {

    private final FintClientWorkflow workflow;
    private final List<? extends EventSourceProvider<FintClientCrd>> eventSourceProviders;
    private final List<? extends Deleter<FintClientCrd>> deleters;

    public FintClientReconiler(FintClientWorkflow workflow,
                               List<? extends EventSourceProvider<FintClientCrd>> eventSourceProviders,
                               List<? extends Deleter<FintClientCrd>> deleters) {
        this.workflow = workflow;
        this.eventSourceProviders = eventSourceProviders;
        this.deleters = deleters;
    }


    @Override
    public UpdateControl<FintClientCrd> reconcile(FintClientCrd resource,
                                                  Context<FintClientCrd> context) {

        CrdValidator.validate(resource);

        Workflow<FintClientCrd> fileShareWorkflow = workflow.build();
        log.debug("Reconciling {} dependent resources", fileShareWorkflow.getDependentResources().size());
        WorkflowReconcileResult reconcile = fileShareWorkflow.reconcile(resource, context);


        List<String> results = new ArrayList<>();
        reconcile.getReconcileResults().forEach((dependentResource, reconcileResult) -> results.add(dependentResource.toString() + " -> " + reconcileResult.getOperation().name()));

        FintClientStatus fintClientStatus = new FintClientStatus();
        fintClientStatus.setDependentResourceStatus(results);
        resource.setStatus(fintClientStatus);
        return UpdateControl.patchStatus(resource);
    }

    @Override
    public DeleteControl cleanup(FintClientCrd resource, Context<FintClientCrd> context) {
        deleters.forEach(dr -> dr.delete(resource, context));
        return DeleteControl.defaultDelete();
    }

    @Override
    public ErrorStatusUpdateControl<FintClientCrd> updateErrorStatus(FintClientCrd resource, Context<FintClientCrd> context, Exception e) {
        FintClientStatus fintClientStatus = new FintClientStatus();
        fintClientStatus.setErrorMessage(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        resource.setStatus(fintClientStatus);
        return ErrorStatusUpdateControl.updateStatus(resource);
    }

    @Override
    public Map<String, EventSource> prepareEventSources(EventSourceContext<FintClientCrd> context) {
        EventSource[] eventSources = eventSourceProviders
                .stream()
                .map(dr -> dr.initEventSource(context))
                .toArray(EventSource[]::new);
        return EventSourceInitializer.nameEventSources(eventSources);
    }


}
