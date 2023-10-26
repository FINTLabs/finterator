package no.fintlabs.client;

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
public class FintClientSecretDependentResource
        extends FlaisKubernetesDependentResource<Secret, FintClientCrd, FintClientSpec> {

    private final SecretService secretService;

    public FintClientSecretDependentResource(FintClientWorkflow workflow,
                                             FintClientDependentResource fintClientDependentResource,
                                             SecretService secretService,
                                             KubernetesClient kubernetesClient) {

        super(Secret.class, workflow, kubernetesClient);
        this.secretService = secretService;
        dependsOn(fintClientDependentResource);
        configureWith(
                new KubernetesDependentResourceConfig<Secret>()
                        .setLabelSelector("app.kubernetes.io/managed-by=finterator")
        );
    }


    @Override
    protected Secret desired(FintClientCrd resource, Context<FintClientCrd> context) {

        log.debug("Desired secret for {}", resource.getMetadata().getName());

        Client fintClient = context.getSecondaryResource(Client.class).orElseThrow();

        HashMap<String, String> labels = new HashMap<>(resource.getMetadata().getLabels());

        labels.put("app.kubernetes.io/managed-by", "finterator");

        return new SecretBuilder()
                .withNewMetadata()
                .withName(resource.getMetadata().getName())
                .withNamespace(resource.getMetadata().getNamespace())
                .withLabels(labels)
                .endMetadata()
                .withType("Opaque")
                .addToData("fint.core.oauth2.username", encode(fintClient.getName()))
                .addToData("fint.core.oauth2.password", encode(secretService.decrypt(fintClient.getPassword())))
                .addToData("fint.core.oauth2.client-id", encode((fintClient.getClientId())))
                .addToData("fint.core.oauth2.client-secret", encode(secretService.decrypt(fintClient.getClientSecret())))
                .build();


    }

    public String encode(String value) {
        if (value == null) value = "";
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    public String decode(String value) {
        return new String(Base64.getDecoder().decode(value.getBytes()));
    }

//    @Override
//    public Matcher.Result<Secret> match(Secret actualResource, FintClientCrd primary, Context<FintClientCrd> context) {
//        Matcher.Result<Secret> match = super.match(actualResource, primary, context);
//        log.info("Matching Secret result {}", match.matched());
//        return match;//super.match(actualResource, primary, context);
//    }
}
