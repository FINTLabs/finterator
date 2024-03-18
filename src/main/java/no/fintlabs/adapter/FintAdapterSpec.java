package no.fintlabs.adapter;

import lombok.*;
import no.fintlabs.FlaisSpec;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FintAdapterSpec implements FlaisSpec {

    private String orgId;
    private String note;
    private List<String> components = Collections.emptyList();
    private List<String> assetIds = Collections.emptyList();
//    private List<String> assets = Collections.emptyList();

}
