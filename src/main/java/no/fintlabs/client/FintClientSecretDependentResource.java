package no.fintlabs.client;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.Matcher;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Component
@KubernetesDependent(labelSelector = "app.kubernetes.io/managed-by=flaiserator")
public class FintClientSecretDependentResource
        extends CRUDKubernetesDependentResource<Secret, FintClientCrd> {

    public FintClientSecretDependentResource(FintClientWorkflow workflow, FintClientDependentResource fintClientDependentResource, KubernetesClient kubernetesClient) {

        super(Secret.class);
        workflow.addDependentResource(this).dependsOn(fintClientDependentResource);
        client = kubernetesClient;
    }


    @Override
    protected Secret desired(FintClientCrd resource, Context<FintClientCrd> context) {

        log.debug("Desired secret for {}", resource.getMetadata().getName());

        Optional<FintClient> fileShare = context.getSecondaryResource(FintClient.class);
        FintClient azureFintClient = fileShare.orElseThrow();

        HashMap<String, String> labels = new HashMap<>(resource.getMetadata().getLabels());

        labels.put("app.kubernetes.io/managed-by", "flaiserator");
        return new SecretBuilder()
                .withNewMetadata()
                .withName(resource.getMetadata().getName())
                .withNamespace(resource.getMetadata().getNamespace())
                .withLabels(labels)
                .endMetadata()
                .withStringData(new HashMap<>() {{
                    //put("fint.azure.storage-account.connection-string", azureFintClient.getConnectionString());
                    //put("fint.azure.storage-account.file-share.name", azureFintClient.getShareName());
                }})
                .build();


    }

    // TODO: 18/10/2022 Need to improve matching
    @Override
    public Matcher.Result<Secret> match(Secret actual, FintClientCrd primary, Context<FintClientCrd> context) {
        final var desiredSecretName = primary.getMetadata().getName();
        return Matcher.Result.nonComputed(actual.getMetadata().getName().equals(desiredSecretName));
    }
}
