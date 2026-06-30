package no.fintlabs.adapter;

import lombok.*;
import no.fintlabs.FlaisSpec;

import java.util.Collections;
import java.util.List;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FintAdapterSpec implements FlaisSpec {

    @Getter
    private String orgId;

    @Getter
    private String note;

    @Builder.Default
    private List<String> components = Collections.emptyList();

    @Builder.Default
    private List<String> assets = Collections.emptyList();

    public List<String> getComponents() {
        return components == null ? Collections.emptyList() : components;
    }

    public List<String> getAssets() {
        return assets == null ? Collections.emptyList() : assets;
    }

}
