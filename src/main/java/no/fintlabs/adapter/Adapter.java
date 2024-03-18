package no.fintlabs.adapter;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@ToString(exclude = {"password", "clientSecret"})
@Builder
@Jacksonized
@EqualsAndHashCode
public final class Adapter implements Serializable {

    @Getter
    private String dn;

    @Getter
    private String name;

    @Getter
    @Setter
    private boolean isManaged;

    @Getter
    private String shortDescription;

    @Getter
    @Builder.Default
    private List<String> assets = new ArrayList<>();

    @Getter
    @Builder.Default
    private List<String> assetIds = new ArrayList<>();

    @Getter
    @Setter
    private String note;

    @Getter
    private String password;

    @Getter
    private String clientSecret;

    @Getter
    @Setter
    private String publicKey;

    @Getter
    private String clientId;

    @Getter
    @Builder.Default
    private List<String> components = new ArrayList<>();

    @Getter
    @Builder.Default
    private List<String> accessPackages = new ArrayList<>();

    public void addAssets(String assetDn) {
        if (assets.stream().noneMatch(assetDn::equalsIgnoreCase)) {
            assets.add(assetDn);
        }
    }

    public void removeAssets(String assetDn) {
        assets.removeIf(assetId -> assetId.equalsIgnoreCase(assetDn));
    }

    public void addAssetId(String assetIdDn) {
        if (assetIds.stream().noneMatch(assetIdDn::equalsIgnoreCase)) {
            assetIds.add(assetIdDn);
        }
    }

    public void removeAssetId(String assetIdDn) {
        assetIds.removeIf(assetId -> assetId.equalsIgnoreCase(assetIdDn));
    }

    public void addComponent(String componentDn) {
        if (components.stream().noneMatch(componentDn::equalsIgnoreCase)) {
            components.add(componentDn);
        }
    }

    public void removeComponent(String componentDn) {
        components.removeIf(component -> component.equalsIgnoreCase(componentDn));
    }

    public void setAccessPackage(String accessPackageDn) {
        accessPackages.clear();
        accessPackages.add(accessPackageDn);
    }
}
