package no.fintlabs.client;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;
import no.fintlabs.FintCrd;

@Group("fintlabs.no")
@Version("v1alpha1")
@Kind("FintClient")
public class FintClientCrd extends FintCrd<FintClientSpec, FintClientStatus> implements Namespaced {
    @Override
    protected FintClientStatus initStatus() {
        return new FintClientStatus();
    }

    @Override
    protected FintClientSpec initSpec() {
        return new FintClientSpec();
    }


}
