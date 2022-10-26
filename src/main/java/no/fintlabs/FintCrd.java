package no.fintlabs;

import io.fabric8.kubernetes.client.CustomResource;

public abstract class FintCrd<T extends FintSpec, P> extends CustomResource<T, P> {
}
