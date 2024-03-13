package no.fintlabs.adapter;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependentResourceConfig;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.FlaisKubernetesDependentResource;
import no.fintlabs.SecretService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;

@Slf4j
@Component
public class FintAdapterSecretDependentResource extends FlaisKubernetesDependentResource<Secret, FintAdapterCrd, FintAdapterSpec> {

    private final SecretService secretService;

    public FintAdapterSecretDependentResource(
            FintAdapterWorkflow workflow,
            FintAdapterDependentResource fintAdapterDependentResource,
            SecretService secretService,
            KubernetesClient kubernetesClient
    ) {
        super(Secret.class, workflow, kubernetesClient);
        this.secretService = secretService;
        dependsOn(fintAdapterDependentResource);
        configureWith(
                new KubernetesDependentResourceConfig<Secret>()
                        .setLabelSelector("app.kubernetes.io/managed-by=finterator")
        );
    }

    @Override
    protected Secret desired(FintAdapterCrd resource, Context<FintAdapterCrd> context) {
        log.debug("Desired secret for {}", resource.getMetadata().getName());

        Adapter fintAdapter = context.getSecondaryResource(Adapter.class).orElseThrow();
        HashMap<String, String> labels = new HashMap<>(resource.getMetadata().getLabels());
        labels.put("app.kubernetes.io/managed-by", "finterator");

        return new SecretBuilder()
                .withNewMetadata()
                .withName(resource.getMetadata().getName())
                .withNamespace(resource.getMetadata().getNamespace())
                .withLabels(labels)
                .endMetadata()
                .withType("Opaque")
                .addToData("fint.core.oauth2.username", encode(fintAdapter.getName()))
                .addToData("fint.core.oauth2.password", encode(secretService.decrypt(fintAdapter.getPassword())))
                .addToData("fint.core.oauth2.client-id", encode((fintAdapter.getClientId())))
                .addToData("fint.core.oauth2.client-secret", encode(secretService.decrypt(fintAdapter.getClientSecret())))
                .build();
    }

    public String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    public String decode(String value) {
        return new String(Base64.getDecoder().decode(value.getBytes()));
    }
}
