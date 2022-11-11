package no.fintlabs.client;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;
import no.fintlabs.FlaisCrd;

@Group("fintlabs.no")
@Version("v1alpha1")
@Kind("FintClient")
public class FintClientCrd extends FlaisCrd<FintClientSpec> implements Namespaced {

    @Override
    protected FintClientSpec initSpec() {
        return new FintClientSpec();
    }


}
